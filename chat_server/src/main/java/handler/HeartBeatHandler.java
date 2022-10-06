package handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yujie
 * @createTime 2022/10/4 15:50
 * @description
 */
@ChannelHandler.Sharable
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private final Map<ChannelHandlerContext, AtomicInteger> map = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("heart beat");
        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
        log.info("心跳检测");
        switch (idleStateEvent.state()){
            case READER_IDLE:{
                //未发送ping消息
                AtomicInteger atomicInteger = map.getOrDefault(ctx, new AtomicInteger(1));
                map.put(ctx, new AtomicInteger(atomicInteger.addAndGet(1)));
                if(map.getOrDefault(ctx, new AtomicInteger(1)).get() > 3){
                    log.info("节点：{}未响应关闭", ctx.channel().remoteAddress());
                    ctx.channel().close();
                }
            }
            case WRITER_IDLE:{break;}
            case ALL_IDLE:{break;}
        }
        super.userEventTriggered(ctx, evt);
    }
}
