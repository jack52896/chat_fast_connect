package protocol.all;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import message.PingMessage;
import protocol.Serializer;

import java.io.IOException;
import java.util.List;

/**
 * @author yujie
 * @createTime 2022/10/8 13:42
 * @description
 */
@Slf4j
public class PingProtocol extends ByteToMessageCodec<PingMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, PingMessage pingMessage, ByteBuf byteBuf) throws Exception {
        byte[] encode = Serializer.SerializerType.json.encode(pingMessage);
        byteBuf.writeBytes(encode);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readableBytes();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes, 0, length);
        ObjectMapper objectMapper = new ObjectMapper();
        PingMessage object = null;
        try {
            object = objectMapper.readValue(bytes, PingMessage.class);
        } catch (IOException e) {
            log.error(e.getClass().getSimpleName(), e);
        }
        list.add(object);
    }
}
