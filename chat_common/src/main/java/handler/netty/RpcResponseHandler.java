package handler.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import message.RpcResponseMessage;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yujie
 * @createTime 2022/10/7 16:48
 * @description
 */
@ChannelHandler.Sharable
@Slf4j
public class RpcResponseHandler extends ChannelInboundHandlerAdapter {

    public static Map<String, Promise<Object>> promiseMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof RpcResponseMessage){
            RpcResponseMessage responseMessage = (RpcResponseMessage) msg;
            Promise<Object> promise = promiseMap.remove(responseMessage.getMessageId());
            log.info("收到提供方响应，消息id:{}", responseMessage.getMessageId());
            if(Objects.nonNull(promise)){
                Object returnValue = responseMessage.getReturnValue();
                Exception exceptionValue = responseMessage.getExceptionValue();
                if(Objects.nonNull(exceptionValue)){
                    promise.setFailure(exceptionValue);
                }else{
                    promise.setSuccess(returnValue);
                }
            }else{
                log.error("调用失败, 请通知提供方是否开放服务");
            }
        }
        super.channelRead(ctx, msg);
    }
}
