package handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import util.ClassUtil;
import util.URLUtil;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static io.netty.handler.codec.stomp.StompHeaders.CONTENT_LENGTH;
import static io.netty.handler.codec.stomp.StompHeaders.CONTENT_TYPE;

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
            log.info("http协议版本号:{}, 请求的方式:{}, 请求的路径:{}, 请求头的文本类型:{}", request.protocolVersion(), request.method().name(),  request.uri(), contentType);
            ByteBuf byteBuf = request.content();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes, 0, byteBuf.readableBytes());
            String content = new String(bytes, StandardCharsets.UTF_8);
//            log.info("请求头的文本类型:{}, 请求的内容：{}", contentType, content);
            getResult(ctx, request, content);
        }
        super.channelRead(ctx, msg);
    }

    private void getResult(ChannelHandlerContext ctx, FullHttpRequest request, String content) {
//        Map<String, String> stringStringMap = URLUtil.formData(content);
        Optional.ofNullable(ClassUtil.map.get(request.uri())).ifPresentOrElse(handlerMethod -> {
            Method method = handlerMethod.getMethod();
            Object object = Optional.ofNullable(ClassUtil.methodObjectMap.get(method)).orElseThrow(() -> new RuntimeException("请检查注解，注册失败"));
            Object result = null;
            try {
                result = method.invoke(object);
            } catch (Exception e) {
                log.error(e.getClass().getSimpleName(), e);
            }
            ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String string = objectMapper.writeValueAsString(result);
                buffer.writeBytes(string.getBytes(StandardCharsets.UTF_8));
            } catch (JsonProcessingException e) {
                log.error(e.getClass().getSimpleName(), e);
            }
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
            response.headers().set(CONTENT_TYPE, "application/json");
            response.headers().set(CONTENT_LENGTH, buffer.readableBytes());
            ctx.writeAndFlush(response);
            ctx.channel().close();
        },()->{
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(CONTENT_TYPE, "application/json");
            ctx.writeAndFlush(response);
            ctx.channel().close();
        });
    }
}
