package handler.rpc;

import handler.container.ChannelBean;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import message.PingMessage;
import util.ClassUtil;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author yujie
 * @createTime 2022/10/8 11:45
 * @description 注册中心的handler
 */
@Slf4j
public class RpcRegisterHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof PingMessage){
            PingMessage pingMessage = (PingMessage) msg;
            log.info("注册中心收到消息:{}", pingMessage);
            if(pingMessage.getPingType() == PingMessage.PingType.PUSH_SERVICES){
                String address = pingMessage.getAddress();
                if(Objects.nonNull(ChannelBean.addressAppMap.get(address))){
                    //TODO 通知服务提供方 服务已经被注册 请等待
                    PingMessage errorMessage = new PingMessage();
                    errorMessage.setPingType(PingMessage.PingType.ERROR_PACKAGE);
                    errorMessage.setError("服务已经被注册，请等待服务注销或者修改服务实例");
                    ctx.channel().writeAndFlush(errorMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                }else{
                    ChannelBean.addressAppMap.put(pingMessage.getAddress(), pingMessage.getApplicationName());
                    if(Objects.nonNull(ChannelBean.appServiceMap.get(pingMessage.getApplicationName()))){
                        PingMessage errorMessage = new PingMessage();
                        errorMessage.setPingType(PingMessage.PingType.ERROR_PACKAGE);
                        errorMessage.setError("服务已经被注册，请等待服务注销或者修改服务实例");
                    }else {
                        ChannelBean.appServiceMap.put(pingMessage.getApplicationName(), pingMessage);
                    }
                }
            }else if(pingMessage.getPingType() == PingMessage.PingType.GET_SERVICES){
                PingMessage returnMessage = ChannelBean.appServiceMap.get(pingMessage.getApplicationName());
                if (Objects.nonNull(returnMessage)) {
                    returnMessage.setPingType(PingMessage.PingType.RETURN_SERVICES);
                    ctx.channel().writeAndFlush(returnMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                }
            }

        }
        super.channelRead(ctx, msg);
    }
}
