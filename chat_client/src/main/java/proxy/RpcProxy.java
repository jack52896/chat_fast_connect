package proxy;

import aspect.RpcInvocationHandler;
import handler.netty.RpcResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import protocol.decoder.RpcResponseDecoder;
import protocol.encoder.RpcRequestEncoder;
import protocol.encoder.RpcResponseEncoder;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Objects;
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
            rpcport = Integer.parseInt(properties.getProperty("rpc.port"));
            rpchost = properties.getProperty("rpc.host");
        } catch (Exception e) {
            log.error(e.getClass().getSimpleName(), e);
        }
    }

    private static Channel channel = null;

    public static Object getRpcService(Class<?> clazz){
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new RpcInvocationHandler(getChannel(), clazz));
    }

    public static synchronized Channel getChannel(){
        if(Objects.nonNull(channel)){
            return channel;
        }else{
            channel = initChannel();
            return channel;
        }
    }

    private static Channel initChannel() {
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

                        log.info("已加载控制器:{}",pipeline);
                    }
                });
        try {
            log.info("连接至远程服务:{},{}", rpchost, rpcport);
            channelFuture = bootstrap.connect(new InetSocketAddress(rpchost, rpcport)).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(channelFuture).orElseThrow(()->new RuntimeException("连接rpc服务器失败")).channel();
    }


}
