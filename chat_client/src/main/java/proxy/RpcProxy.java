package proxy;

import aspect.RpcInvocationHandler;
import handler.netty.RpcResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
                        nioSocketChannel.pipeline()
                                .addLast(new RpcResponseHandler());
                    }
                });
        try {
            channelFuture = bootstrap.connect(new InetSocketAddress(rpchost, rpcport)).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(channelFuture).orElseThrow(()->new RuntimeException("连接rpc服务器失败")).channel();
    }


}
