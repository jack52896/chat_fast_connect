package handler.rpc;

import handler.container.ChannleBean;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import message.PingMessage;

import java.util.Objects;
import java.util.UUID;

/**
 * @author yujie
 * @createTime 2022/10/8 11:45
 * @description 注册中心的handler
 */
@Slf4j
public class RpcRegisterHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        PingMessage pingMessage = new PingMessage();
        pingMessage.setMessageId(UUID.randomUUID().toString());
        pingMessage.setPingType(PingMessage.PingType.RETURN_SERVICES);
        pingMessage.setMap(ChannleBean.map);
        ctx.channel().writeAndFlush(pingMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof PingMessage){
            PingMessage pingMessage = (PingMessage) msg;
            if(pingMessage.getPingType() == PingMessage.PingType.NONE_PACKAGE){
                if(Objects.nonNull(ChannleBean.map.get(pingMessage.getServiceName()))){
                    ChannleBean.map.put(pingMessage.getServiceName(), pingMessage);
                }else{
                    ChannleBean.map.put(pingMessage.getServiceName(), pingMessage);
                }
            }

        }
        super.channelRead(ctx, msg);
    }
}
