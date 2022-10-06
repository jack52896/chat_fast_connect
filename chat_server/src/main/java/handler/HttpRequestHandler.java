package handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import util.URLUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author yujie
 * @createTime 2022/10/6 16:06
 * @description
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpRequestHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            FullHttpRequest request = (FullHttpRequest) msg;
            String contentType = request.headers().get("Content-Type");
            log.info("http协议版本号:{}", request.protocolVersion());
            log.info("请求的方式:{}", request.method().name());
            log.info("请求的路径:{}", request.uri());
            log.info("请求头的文本类型:{}", contentType);
            if(contentType.equals("application/x-www-form-urlencoded")){
                ByteBuf byteBuf = request.content();
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes, 0, byteBuf.readableBytes());
                String content = new String(bytes, StandardCharsets.UTF_8);
                log.info("请求头的文本类型:{}, 请求的内容：{}", contentType, content);
                Map<String, String> stringStringMap = URLUtil.formData(content);

            }
        }
        super.channelRead(ctx, msg);
    }
}
