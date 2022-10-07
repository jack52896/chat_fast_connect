package aspect;

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

    private Channel channel;

    private Class<?> clazz;

    public RpcInvocationHandler(Channel channel, Class<?> clazz){
        this.channel = channel;
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequestMessage message = new RpcRequestMessage(
                UUID.randomUUID().toString(),
                clazz.getName(),
                method.getName(),
                method.getReturnType(),
                method.getParameterTypes(),
                args);
        Channel channel = RpcProxy.getChannel();
        channel.writeAndFlush(message);
        DefaultPromise<Object> promise = new DefaultPromise(channel.eventLoop());
        promise.await();
        if(promise.isSuccess()){
            return promise.getNow();
        }else{
            log.error(promise.cause().getClass().getSimpleName(), promise.cause());
            throw new RuntimeException(promise.cause());
        }
    }
}
