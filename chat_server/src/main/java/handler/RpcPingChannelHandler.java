package handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import message.PingMessage;
import util.ClassUtil;
import util.PropertiesUtil;

import java.net.InetAddress;
import java.util.UUID;

/**
 * @author yujie
 * @createTime 2022/10/8 15:43
 * @description 连接注册中心后服务提供方的handler
 */
public class RpcPingChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        ClassUtil.RpcServiceNameClazzMap.forEach((k, v)->{
            PingMessage pingMessage = new PingMessage();
            pingMessage.setMessageId(UUID.randomUUID().toString());
            pingMessage.setPingType(PingMessage.PingType.NONE_PACKAGE);
            pingMessage.setServiceName(k);
            pingMessage.setHostAddress(addr.getHostAddress());
            pingMessage.setHostName(addr.getHostName());
            pingMessage.setPort(PropertiesUtil.properties.getProperty("rpc.port"));
            ctx.channel().writeAndFlush(pingMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        });
        super.channelActive(ctx);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}
