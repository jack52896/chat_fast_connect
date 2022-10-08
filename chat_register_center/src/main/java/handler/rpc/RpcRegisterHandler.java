package handler.rpc;

import handler.container.ChannelBean;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import message.PingMessage;

import java.util.UUID;

/**
 * @author yujie
 * @createTime 2022/10/8 11:45
 * @description 注册中心的handler
 */
@Slf4j
public class RpcRegisterHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof PingMessage){
            PingMessage pingMessage = (PingMessage) msg;
            log.info("注册中心收到消息:{}", pingMessage);
            if(pingMessage.getPingType() == PingMessage.PingType.PUSH_SERVICES){
                ChannelBean.map = pingMessage.getMap();
            }else if(pingMessage.getPingType() == PingMessage.PingType.GET_SERVICES){
                PingMessage returnMessage = new PingMessage();
                returnMessage.setMessageId(UUID.randomUUID().toString());
                returnMessage.setMap(ChannelBean.map);
                returnMessage.setPingType(PingMessage.PingType.RETURN_SERVICES);
                ctx.channel().writeAndFlush(returnMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }

        }
        super.channelRead(ctx, msg);
    }
}
