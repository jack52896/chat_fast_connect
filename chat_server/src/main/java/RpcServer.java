import handler.netty.ClientHeartBeatHandler;
import handler.netty.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import protocol.decoder.RpcRequestDecoder;
import protocol.encoder.RpcResponseEncoder;

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
        try {
            serverBootstrap
                    .group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) {
                            //添加http解码器，解析http请求以及加入https
                            log.info("rpc connect :{}", channel);
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("rpc server request protocol", new RpcRequestDecoder());
                            pipeline.addLast("rpc server response protocol", new RpcResponseEncoder());
                            pipeline.addLast("rpc handler", new RpcRequestHandler());
                            pipeline.addLast("heart beat", new ClientHeartBeatHandler());
                        }
                    }).bind(port).sync();
        } catch (Exception e) {
            log.error("端口绑定失败, 异常信息:{}", e.getClass().getSimpleName(), e);
        }
        log.info("server rpc start , port:{}", port);
    }

}
