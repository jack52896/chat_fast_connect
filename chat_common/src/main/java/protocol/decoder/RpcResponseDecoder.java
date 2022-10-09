package protocol.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import message.RpcResponseMessage;

import java.io.IOException;
import java.util.List;

/**
 * @author yujie
 * @createTime 2022/10/7 20:35
 * @description
 */
@Slf4j
public class RpcResponseDecoder extends ByteToMessageDecoder {
//    @Override
//    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponseMessage rpcRequestMessage, ByteBuf byteBuf) throws Exception {
//        //魔数
//        byteBuf.writeBytes(new byte[]{1, 2 , 3, 4});
//        //版本号
//        byteBuf.writeByte(1);
//        //序列化算法 jackson / json
//        byteBuf.writeByte(1);
//        byte[] bytes = Serializer.SerializerType.json.encode(rpcRequestMessage);
//        byteBuf.writeInt(bytes.length);
//        byteBuf.writeBytes(bytes);
//    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        RpcResponseMessage responseMessage = null;
        try {
            int magicNumber = byteBuf.readInt();
            byte version = byteBuf.readByte();
            byte serializerType = byteBuf.readByte();
            int length = byteBuf.readInt();
            byte[] bytes = new byte[length];
            byteBuf.readBytes(bytes, 0 , length);
            ObjectMapper objectMapper = new ObjectMapper();
            responseMessage = objectMapper.readValue(bytes, RpcResponseMessage.class);
        } catch (Exception e) {
            log.error("反序泪化失败：{}", e.getClass().getSimpleName(), e);
        }
        list.add(responseMessage);
    }
}
