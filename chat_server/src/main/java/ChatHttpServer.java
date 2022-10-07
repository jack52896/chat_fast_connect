import handler.HeartBeatHandler;
import handler.HttpRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;
import pool.DataSourcePool;

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
        String port = properties.getProperty("netty.port");
        String property = properties.getProperty("dataSource.enable");
        if("true".equals(property)){
            new DataSourcePool();
        }
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
//        SslHandler sslHandler = new SslHandler();
        HttpRequestHandler httpRequestHandler = new HttpRequestHandler();
        HeartBeatHandler heartBeatHandler = new HeartBeatHandler();
        try {
            serverBootstrap
                    .group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            //添加http解码器，解析http请求以及加入https
                            channel.pipeline()
//                                    .addLast("ssl", sslHandler);
                                    .addLast("http server protocol", new HttpServerCodec())
                                    .addLast("http object aggregator" , new HttpObjectAggregator(1024 * 10))
                                    .addLast("http handler", httpRequestHandler)
                                    .addLast("heart beat", heartBeatHandler);
                        }
                    }).bind(Integer.parseInt(port)).sync();
        } catch (InterruptedException e) {
            log.error("端口绑定失败, 异常信息:{}", e.getClass().getSimpleName(), e);
        }
        log.info("server netty start , port:{}", port);


    }

    public static void main(String[] args) {
        start();
    }
}
