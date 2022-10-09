package handler.netty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import message.PingMessage;
import util.PropertiesUtil;

import java.net.InetAddress;

/**
 * @author yujie
 * @createTime 2022/10/8 14:27
 * @description
 */
@Slf4j
public class ServerHeartBeatHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
//        log.info("超时未发送心跳包, 对应的channel:{}", ctx.channel().remoteAddress());
        switch (idleStateEvent.state()){
            case READER_IDLE:{
                log.info("服务端心跳似乎停止了，尝试连接服务端");
                PingMessage pingMessage = new PingMessage();
                pingMessage.setPingType(PingMessage.PingType.SIGNALL);
                //发送心跳包失败直接关闭通道
                ctx.channel().writeAndFlush(pingMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
            case WRITER_IDLE:{break;}
            case ALL_IDLE:{break;}
        }
        super.userEventTriggered(ctx, evt);
    }
}
