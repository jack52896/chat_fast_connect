package handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
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
            log.info("http协议版本号:{}", request.protocolVersion());
            log.info("请求的方式:{}", request.method().name());
            log.info("请求的路径:{}", request.uri());
            log.info("请求头的文本类型:{}", contentType);
            if("application/x-www-form-urlencoded".equals(contentType)){
                ByteBuf byteBuf = request.content();
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes, 0, byteBuf.readableBytes());
                String content = new String(bytes, StandardCharsets.UTF_8);
                log.info("请求头的文本类型:{}, 请求的内容：{}", contentType, content);
                getResult(ctx, request, content);
            }else{
                ByteBuf byteBuf = request.content();
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes, 0, byteBuf.readableBytes());
                String content = new String(bytes, StandardCharsets.UTF_8);
                log.info("请求头的文本类型:{}, 请求的内容：{}", contentType, content);
                getResult(ctx, request, content);
            }
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
            ByteBuf buffer = ctx.alloc().buffer();
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(result);
                buffer.writeBytes(bos.toByteArray());
            } catch (IOException e) {
                log.error(e.getClass().getSimpleName(), e);
            }
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
            response.headers().set(CONTENT_TYPE, "application/json");
            response.headers().set(CONTENT_LENGTH, buffer.readableBytes());
            ctx.writeAndFlush(response);
        },()->{
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(CONTENT_TYPE, "application/json");
            ctx.writeAndFlush(response);
        });
    }
}
