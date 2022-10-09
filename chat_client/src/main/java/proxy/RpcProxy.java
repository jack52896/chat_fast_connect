package proxy;

import annoation.RpcDiscovery;
import aspect.RpcInvocationHandler;
import contant.StaticContant;
import handler.RpcPingReturnHandler;
import handler.netty.RpcResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import message.PingMessage;
import protocol.all.PingProtocol;
import protocol.decoder.RpcResponseDecoder;
import protocol.encoder.RpcRequestEncoder;
import util.URLUtil;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * @author yujie
 * @createTime 2022/10/7 15:49
 * @description
 */
@Slf4j
public class RpcProxy {

    private static Properties properties;

    private static int rpcport;

    private static String rpchost;

    static {
        try {
            properties = new Properties();
            properties.load(RpcProxy.class.getClassLoader().getResourceAsStream("application.properties"));
            rpcport = Integer.parseInt(properties.getProperty("rpc.register.port"));
            rpchost = properties.getProperty("rpc.register.host");
        } catch (Exception e) {
            log.error(e.getClass().getSimpleName(), e);
        }
    }

    public static Object getRpcService(Class<?> clazz){
        String annoation = Optional.ofNullable(clazz.getAnnotation(RpcDiscovery.class)).orElseThrow(() -> new RuntimeException("no annoation")).value();
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new RpcInvocationHandler(clazz, annoation));
    }

    private static Channel initRegisterChannel() {
        ChannelFuture channelFuture = null;
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast("rpc register server protocol", new PingProtocol());
                        pipeline.addLast("rpc server handler", new RpcPingReturnHandler());
                    }
                });
        try {
            log.info("连接至远程注册中心:{},{}", rpchost, rpcport);
            channelFuture = bootstrap.connect(new InetSocketAddress(rpchost, rpcport)).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } catch (Exception e) {
            log.error("连接远程注册中心失败:{}",e.getClass().getSimpleName(), e);
        }
        return Optional.ofNullable(channelFuture).orElseThrow(()->new RuntimeException("连接rpc服务器失败")).channel();
    }

    public static Channel getChannel() {
        //todo 注册服务初始化之后，在获取map时报错 , cause: 需要等待当前客户端与注册中心交互完毕
        Channel channel = initRegisterChannel();
        DefaultPromise<Object> defaultPromise = new DefaultPromise<>(channel.eventLoop());
        URLUtil.map.put(StaticContant.WAIT_REGISTER_CENTER, defaultPromise);
        try {
            defaultPromise.await();
        } catch (InterruptedException e) {
            log.error(e.getClass().getSimpleName(), e);
        }
        PingMessage pingMessage = (PingMessage) defaultPromise.getNow();
        ChannelFuture channelFuture = null;
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast("rpc server request protocol", new RpcRequestEncoder());
                        pipeline.addLast("rpc server response protocol", new RpcResponseDecoder());
                        pipeline.addLast("rpc server handler", new RpcResponseHandler());
                    }
                });
        try {
            log.info("连接至远程服务:{},{}", pingMessage.getHostAddress(), pingMessage.getPort());
            channelFuture = bootstrap.connect(new InetSocketAddress(pingMessage.getHostAddress(), Integer.parseInt(pingMessage.getPort()))).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(channelFuture).orElseThrow(()->new RuntimeException("连接rpc服务器失败")).channel();
    }


}
