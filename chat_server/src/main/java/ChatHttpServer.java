import client.RegisterClient;
import handler.HttpRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;
import pool.DataSourcePool;
import util.PropertiesUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * @author yujie
 * @createTime 2022/10/6 16:03
 * @description
 */
@Slf4j
public class ChatHttpServer {

    private static Properties properties;

    static {
        try {
            properties = new Properties();
            properties.load(ChatHttpServer.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void start(){
        String port = PropertiesUtil.properties.getProperty("netty.port");
        String property = PropertiesUtil.properties.getProperty("dataSource.enable");
        String rpcstatus = PropertiesUtil.properties.getProperty("rpc.enable");
        String rpcport = PropertiesUtil.properties.getProperty("rpc.port");
        String regsiterHost = PropertiesUtil.properties.getProperty("rpc.register.host");
        String regsiterPort = PropertiesUtil.properties.getProperty("rpc.register.port");
        String regsiteStatus = PropertiesUtil.properties.getProperty("rpc.register.enbale");
        if("true".equals(property)){
            new DataSourcePool();
        }
        if("true".equals(rpcstatus)){
            RpcServer.start(Integer.parseInt(rpcport));
        }
        if("true".equals(regsiteStatus)){
            Channel start = new RegisterClient(regsiterHost, Integer.parseInt(regsiterPort)).start();
        }
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        HttpRequestHandler httpRequestHandler = new HttpRequestHandler();
        try {
            serverBootstrap
                    .group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("http server protocol", new HttpServerCodec());
                            pipeline.addLast("http object aggregator" , new HttpObjectAggregator(1024 * 10));
                            pipeline.addLast("http handler", httpRequestHandler);
                            log.info("已成功加载控制器:{}", pipeline);
                        }
                    }).bind(Integer.parseInt(port)).sync();
        } catch (Exception e) {
            log.error("端口绑定失败, 异常信息:{}", e.getClass().getSimpleName(), e);
        }
        log.info("server netty start , port:{}", port);


    }

    public static void main(String[] args) {
        start();
    }
}
