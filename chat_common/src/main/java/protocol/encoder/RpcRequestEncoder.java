package protocol.encoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToByteEncoder;
import message.RpcRequestMessage;
import protocol.Serializer;

import java.io.IOException;
import java.util.List;

/**
 * @author yujie
 * @createTime 2022/10/7 20:35
 * @description
 */
public class RpcRequestEncoder extends MessageToByteEncoder<RpcRequestMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequestMessage rpcRequestMessage, ByteBuf byteBuf) throws Exception {
        //魔数
        byteBuf.writeBytes(new byte[]{1, 2 , 3, 4});
        //版本号
        byteBuf.writeByte(1);
        //序列化算法 jackson / json
        byteBuf.writeByte(1);
        byte[] bytes = Serializer.SerializerType.json.encode(rpcRequestMessage);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

//    @Override
//    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
//        int magicNumber = byteBuf.readInt();
//        byte version = byteBuf.readByte();
//        byte serializerType = byteBuf.readByte();
//        int length = byteBuf.readInt();
//        byte[] bytes = new byte[length];
//        byteBuf.readBytes(bytes, 0 , length);
//        ObjectMapper objectMapper = new ObjectMapper();
//        RpcRequestMessage message = null;
//        try {
//            message = objectMapper.readValue(bytes, RpcRequestMessage.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        list.add(message);
//    }
}
