package handler.http;

import handler.container.ChannelBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import protocol.Serializer;

import java.util.Set;

/**
 * @author yujie
 * @createTime 2022/10/8 14:38
 * @description
 */
@Slf4j
public class HttpRegisterHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            FullHttpRequest request = (FullHttpRequest) msg;
            String contentType = request.headers().get("Content-Type");
            log.info("http协议版本号:{}, 请求的方式:{}, 请求的路径:{}, 请求头的文本类型:{}", request.protocolVersion(), request.method().name(),  request.uri(), contentType);
            byte[] encode = Serializer.SerializerType.json.encode(ChannelBean.map);
            ByteBuf byteBuf = ctx.alloc().buffer();
            byteBuf.writeBytes(encode);
            FullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK, byteBuf);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
        super.channelRead(ctx, msg);
    }
}
