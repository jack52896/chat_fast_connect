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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author yujie
 * @createTime 2022/10/8 15:43
 * @description 连接注册中心后服务提供方的handler
 */
@Slf4j
public class RpcPingChannelHandler extends ChannelInboundHandlerAdapter {

    private static ScheduledExecutorService service = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        service.scheduleAtFixedRate(()->{
            PingMessage pingMessage = new PingMessage();
            pingMessage.setPingType(PingMessage.PingType.NONE_PACKAGE);
            ctx.writeAndFlush(pingMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }, 1, 2, TimeUnit.SECONDS);
        InetAddress addr = InetAddress.getLocalHost();
        PingMessage pushMessage = new PingMessage();
        pushMessage.setPingType(PingMessage.PingType.PUSH_SERVICES);
        pushMessage.setHostName(addr.getHostName());
        String serverPort = PropertiesUtil.properties.getProperty("rpc.port");
        pushMessage.setPort(serverPort);
        pushMessage.setHostAddress(addr.getHostAddress());
        pushMessage.setAddress(addr.getHostAddress()+":"+serverPort);
        Set<String> strings = ClassUtil.RpcServiceNameClazzMap.keySet();
        pushMessage.setServiceNames(strings);
        pushMessage.setApplicationName(PropertiesUtil.properties.getProperty("application.name"));
        ctx.channel().writeAndFlush(pushMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        log.info("服务端成功推送消息到注册中心:{}", pushMessage);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof PingMessage){
            PingMessage pingMessage = new PingMessage();
            if(pingMessage.getPingType() == PingMessage.PingType.ERROR_PACKAGE){
                log.error("将服务注册到注册中心时报错:{}", pingMessage.getError());
            }else if(pingMessage.getPingType() == PingMessage.PingType.SIGNALL){
                service.scheduleAtFixedRate(()->{
                    PingMessage message = new PingMessage();
                    message.setPingType(PingMessage.PingType.NONE_PACKAGE);
                    ctx.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                }, 1, 2, TimeUnit.SECONDS);
            }
        }
        super.channelRead(ctx, msg);
    }
}
