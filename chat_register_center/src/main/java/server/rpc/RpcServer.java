package server.rpc;

import handler.netty.ClientHeartBeatHandler;
import handler.netty.ServerHeartBeatHandler;
import handler.rpc.RpcRegisterHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import protocol.all.PingProtocol;
import util.PropertiesUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author yujie
 * @createTime 2022/10/8 11:38
 * @description
 */
@Slf4j
public class RpcServer {
    public static void start(){
        int port = Integer.parseInt(PropertiesUtil.properties.getProperty("register.port"));
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
                            pipeline.addLast(" ping protocol ", new PingProtocol());
                            pipeline.addLast(new RpcRegisterHandler());
                            pipeline.addLast(new IdleStateHandler(10, 10, 20, TimeUnit.SECONDS));
                            pipeline.addLast(new ServerHeartBeatHandler());
                        }
                    }).bind(port).sync();
            log.info("注册中心成功初始化");
        } catch (Exception e) {
            log.error("端口绑定失败, 异常信息:{}", e.getClass().getSimpleName(), e);
        }
        log.info("server register rpc start , port:{}", port);
    }
}

