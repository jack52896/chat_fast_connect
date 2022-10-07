package handler;

import annoation.RequestBody;
import annoation.RequestParam;
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
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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
        Optional.ofNullable(ClassUtil.map.get(request.uri())).ifPresentOrElse(handlerMethod -> {
            Method method = handlerMethod.getMethod();
            Object object = Optional.ofNullable(ClassUtil.methodObjectMap.get(method)).orElseThrow(() -> new RuntimeException("请检查注解，注册失败"));
            Object result = getReturnObject(handlerMethod.getMethod(), request, content, object);
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

    public Object getReturnObject(Method method, FullHttpRequest request, String content, Object object){
        Object result = null;
        try {
            String contentType = request.headers().get("Content-Type");
            switch (contentType){
                case "application/x-www-form-urlencoded":{
                    Class aClass1 = null;

                    List<Parameter> parameterList = new ArrayList<>();
                    Parameter[] parameters = method.getParameters();
                    for (Parameter parameter : parameters) {
                        if(parameter.isAnnotationPresent(RequestParam.class)){
                            parameterList.add(parameter);
                        }
                    }
                    if(parameterList.size() > 1){
                        throw new RuntimeException("RequestParam注解只能存在一个");
                    }
                    if(!parameterList.isEmpty()){
                        Parameter parameter = parameterList.get(0);
                        aClass1 = Class.forName(parameter.getParameterizedType().getTypeName());
                    }

                    if(Objects.nonNull(aClass1)){
                        Object parameter = aClass1.newInstance();
                        Map<String, String> stringStringMap = URLUtil.formData(content);
                        Arrays.stream(aClass1.getDeclaredFields()).forEach(field -> {
                            String value =  stringStringMap.get(field.getName());
                            if(Objects.nonNull(value)){
                                try {
                                    Object resultValue = ClassUtil.getObject(field, value);
                                    field.setAccessible(true);
                                    field.set(parameter, resultValue);
                                } catch (IllegalAccessException e) {
                                    log.error("返回对象生成失败:{}",e.getClass().getSimpleName(), e);
                                }
                            }
                        });
                        result = method.invoke(object, parameter);
                    }else{
                        result = method.invoke(object);
                    }

                    break;
                }
                case  "application/json":{
                    //需要进行参数转换的结果 class集合
                    ArrayList<Class<?>> classes = new ArrayList<>();
                    Parameter[] parameters = method.getParameters();
                    for (Parameter parameter : parameters) {
                        if(parameter.isAnnotationPresent(RequestBody.class)){
                            classes.add(Class.forName(parameter.getParameterizedType().getTypeName()));
                        }
                    }
                    Object[] params = new Object[classes.size()];
                    int i = 0;
                    for (int j = 0; j < classes.size(); j++) {
                        Class<?> aClass = classes.get(i);
                        ObjectMapper objectMapper = new ObjectMapper();
                        params[i] = objectMapper.readValue(content,aClass);
                    }
                    result = method.invoke(object,params);
                }
                default:
            }

        } catch (Exception e) {
            log.error("参数字段对应不正确，请检查:{}",e.getClass().getSimpleName(), e);
        }
        return result;
    }
}
