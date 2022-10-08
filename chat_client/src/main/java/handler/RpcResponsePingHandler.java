package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import message.PingMessage;

/**
 * @author yujie
 * @createTime 2022/10/8 16:34
 * @description
 */
public class RpcResponsePingHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof PingMessage){
            PingMessage pingMessage = (PingMessage) msg;
            if(pingMessage.getPingType() == PingMessage.PingType.RETURN_SERVICES){

            }
        }
        super.channelRead(ctx, msg);
    }
}
