package handler.netty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import message.PingMessage;
import util.PropertiesUtil;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yujie
 * @createTime 2022/10/4 15:50
 * @description
 */
@ChannelHandler.Sharable
@Slf4j
public class ClientHeartBeatHandler extends ChannelInboundHandlerAdapter {

    private final Map<ChannelHandlerContext, AtomicInteger> map = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("heart beat");
        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
//        log.info("超时未发送心跳包, 对应的channel:{}", ctx.channel().remoteAddress());
        switch (idleStateEvent.state()){
            case READER_IDLE:{
                String serviceName = PropertiesUtil.properties.getProperty("rpc.service.name");
                InetAddress addr = InetAddress.getLocalHost();
                log.info("尝试向服务器发送心跳包, 并更新服务地址:{}, 服务器名称:{}", addr.getHostAddress(), addr.getHostName());
                PingMessage pingMessage = new PingMessage(serviceName, addr.getHostName(), addr.getHostAddress(), (String) PropertiesUtil.properties.getProperty("rpc.port"));
                //发送心跳包失败直接关闭通道
                ctx.channel().writeAndFlush(pingMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
            case WRITER_IDLE:{break;}
            case ALL_IDLE:{break;}
        }
        super.userEventTriggered(ctx, evt);
    }
}
