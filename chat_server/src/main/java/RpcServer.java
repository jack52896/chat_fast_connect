import handler.HeartBeatHandler;
import handler.HttpRequestHandler;
import handler.netty.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;
import protocol.CustomizeProtocol;

/**
 * @author yujie
 * @createTime 2022/10/7 19:47
 * @description
 */
@Slf4j
public class RpcServer {

    public static void start(int port){
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
//        SslHandler sslHandler = new SslHandler();
        HttpRequestHandler httpRequestHandler = new HttpRequestHandler();
        HeartBeatHandler heartBeatHandler = new HeartBeatHandler();
        RpcRequestHandler rpcRequestHandler = new RpcRequestHandler();
        try {
            serverBootstrap
                    .group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            //添加http解码器，解析http请求以及加入https
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("rpc server protocol", new CustomizeProtocol());
                            pipeline.addLast("rpc handler", rpcRequestHandler);
                            pipeline.addLast("heart beat", heartBeatHandler);
                            log.info("已成功加载控制器:{}", pipeline);
                        }
                    }).bind(port).sync();
        } catch (InterruptedException e) {
            log.error("端口绑定失败, 异常信息:{}", e.getClass().getSimpleName(), e);
        }
        log.info("server rpc start , port:{}", port);
    }

}
