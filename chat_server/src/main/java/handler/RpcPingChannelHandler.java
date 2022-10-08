package handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import message.PingMessage;
import util.ClassUtil;
import util.PropertiesUtil;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author yujie
 * @createTime 2022/10/8 15:43
 * @description 连接注册中心后服务提供方的handler
 */
@Slf4j
public class RpcPingChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        PingMessage pingMessage = new PingMessage();
        pingMessage.setMessageId(UUID.randomUUID().toString());
        pingMessage.setPingType(PingMessage.PingType.PUSH_SERVICES);
        pingMessage.setHostName(addr.getHostName());
        pingMessage.setPort(PropertiesUtil.properties.getProperty("rpc.port"));
        pingMessage.setHostAddress(addr.getHostAddress());

        Map<String, PingMessage> map = new HashMap<>();
        ClassUtil.RpcServiceNameClazzMap.forEach((k, v)->{
            map.put(k, pingMessage);
        });
        PingMessage pushMessage = new PingMessage();
        pushMessage.setMessageId(UUID.randomUUID().toString());
        pushMessage.setPingType(PingMessage.PingType.PUSH_SERVICES);
        pushMessage.setMap(map);
        ctx.channel().writeAndFlush(pushMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        log.info("服务端成功推送消息到注册中心:{}", pushMessage);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}
