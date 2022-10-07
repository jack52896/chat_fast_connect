package handler.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import message.RpcRequestMessage;
import util.PropertiesUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yujie
 * @createTime 2022/10/7 16:50
 * @description
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestHandler extends ChannelInboundHandlerAdapter {

    public static Map<String, String> map = new ConcurrentHashMap();

    public static String rpcstatus = null;

    static {
        Arrays.asList(PropertiesUtil.properties.getProperty("rpc.service.impl").split("\\|"))
                .stream()
                .forEach(s -> {
                    String[] split = s.split(",");
                    map.put(split[0], split[1]);
                });
        rpcstatus = PropertiesUtil.properties.getProperty("rpc.enable");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if("false".equals(rpcstatus)){
            super.channelRead(ctx, msg);
        }
        if(msg instanceof RpcRequestMessage){
            RpcRequestMessage message = (RpcRequestMessage) msg;
            try {
                String serviceImplAllFileName = map.get(message.getInterfaceName());
                Class<?> aClass = Class.forName(serviceImplAllFileName);
                Method method = aClass.getMethod(message.getMethodName(), message.getParameterTypes());
                Object obj = aClass.newInstance();
                Object invoke = method.invoke(obj, message.getParameterValue());
                ctx.writeAndFlush(invoke);
                //TODO
            } catch (Exception e) {
                log.error("客户端发送的rpc调用失败, 请联系调用方, 消息id:{}, 异常信息:{}",message.getMessageId(), e.getClass().getSimpleName(), e);
            }
        }
        super.channelRead(ctx, msg);
    }
}
