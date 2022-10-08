package handler;

import contain.ChannelClientBean;
import contant.StaticContant;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import message.PingMessage;
import util.URLUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yujie
 * @createTime 2022/10/8 17:05
 * @description
 */
@Slf4j
public class RpcPingReturnHandler extends ChannelInboundHandlerAdapter {

    private static Map<String, Promise> map = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("成功连接至注册中心，发送主动拉取服务请求");
        PingMessage pingMessage = new PingMessage();
        pingMessage.setPingType(PingMessage.PingType.GET_SERVICES);
        ctx.channel().writeAndFlush(pingMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof PingMessage){
            PingMessage pingMessage = (PingMessage) msg;
            if(pingMessage.getPingType() == PingMessage.PingType.RETURN_SERVICES){
                ChannelClientBean.map = pingMessage.getMap();
            }
            Promise<Object> promise = URLUtil.map.remove(StaticContant.WAIT_REGISTER_CENTER);
            promise.setSuccess(new Object());
        }
        super.channelRead(ctx, msg);
    }
}
