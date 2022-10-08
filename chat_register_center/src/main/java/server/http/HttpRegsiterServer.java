package server.http;

import handler.http.HttpRegisterHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import protocol.all.PingProtocol;
import util.PropertiesUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author yujie
 * @createTime 2022/10/8 14:34
 * @description
 */
@Slf4j
public class HttpRegsiterServer {

    public static void start(){
        int port = Integer.parseInt(PropertiesUtil.properties.getProperty("register.http.port"));
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap
                    .group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(" http protocol ", new HttpServerCodec());
                            pipeline.addLast("http object aggregator" , new HttpObjectAggregator(1024 * 10));
                            pipeline.addLast("http register handler" , new HttpRegisterHandler());

                        }
                    }).bind(port).sync();
            log.info("注册中心 http server start ,port:{}", port);
        } catch (Exception e) {
            log.error("端口绑定失败, 异常信息:{}", e.getClass().getSimpleName(), e);
        }
        log.info("server register http start , port:{}", port);
    }

}
