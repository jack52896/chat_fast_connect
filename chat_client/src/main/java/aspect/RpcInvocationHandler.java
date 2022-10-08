package aspect;

import handler.netty.RpcResponseHandler;
import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import message.RpcRequestMessage;
import proxy.RpcProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author yujie
 * @createTime 2022/10/7 15:52
 * @description
 */
@Slf4j
public class RpcInvocationHandler implements InvocationHandler {


    private Class<?> clazz;

    private String rpcServiceName;

    public RpcInvocationHandler(Class<?> clazz, String rpcServiceName){
        this.clazz = clazz;
        this.rpcServiceName = rpcServiceName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            RpcRequestMessage message = new RpcRequestMessage(
                    UUID.randomUUID().toString(),
                    clazz.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args);
            Channel channel = RpcProxy.getChannel(rpcServiceName);
            log.info("rpc connect :{}", channel);

            channel.writeAndFlush(message).sync();
            DefaultPromise<Object> promise = new DefaultPromise(channel.eventLoop());
            RpcResponseHandler.promiseMap.put(message.getMessageId(), promise);
            log.info("已发送请求, 消息id为:{}", message.getMessageId());
            promise.await();
            if(promise.isSuccess()){
                return promise.getNow();
            }else{
                log.error(promise.cause().getClass().getSimpleName(), promise.cause());
                throw new RuntimeException(promise.cause());
            }
        } catch (Exception e) {
            log.error("发送消息失败：{}", e.getClass().getSimpleName(), e);
        }
        return null;
    }
}
