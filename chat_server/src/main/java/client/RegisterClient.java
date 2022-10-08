package client;

import handler.RpcPingChannelHandler;
import handler.netty.ClientHeartBeatHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import protocol.all.PingProtocol;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author yujie
 * @createTime 2022/10/8 13:24
 * @description
 */
@Slf4j
public class RegisterClient {

    private String host;

    private Integer port;

    public RegisterClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public Channel start(){
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
                        pipeline.addLast(" ping protocol ", new PingProtocol());
                        pipeline.addLast(new RpcPingChannelHandler());
                        pipeline.addLast(new IdleStateHandler(10, 10, 20, TimeUnit.SECONDS));
                        pipeline.addLast(new ClientHeartBeatHandler());
                    }
                });
        try {
            log.info("连接至注册中心:{},{}", host, port);
            channelFuture = bootstrap.connect(new InetSocketAddress(host, port)).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(channelFuture).orElseThrow(()->new RuntimeException("连接注册中心失败")).channel();

    }

}
