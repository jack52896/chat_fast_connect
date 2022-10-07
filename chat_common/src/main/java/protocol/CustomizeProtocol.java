package protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author yujie
 * @createTime 2022/10/3 17:16
 * @description
 */
@Slf4j
//@ChannelHandler.Sharable
public class CustomizeProtocol extends ByteToMessageCodec<String> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String s, ByteBuf byteBuf) throws Exception {
        //魔数
        byteBuf.writeBytes(new byte[]{1, 2 , 3, 4});
        //版本号
        byteBuf.writeByte(1);
        //序列化算法 jackson / json
        byteBuf.writeByte(0);
        //报文类型
        byteBuf.writeByte(1);
        byte[] bytes = Serializer.SerializerType.jackson.encode(s);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNumber = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializerType = byteBuf.readByte();
        byte contentType = byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes, 0 , length);
        Serializer.SerializerType type = Serializer.SerializerType.alias("jackson");
        Object message = type.decode(bytes);
        log.info("magicNumber:{}, version:{}, serializerType:{}, contentType:{}, length:{}, message:{}", magicNumber, version, serializerType, contentType, length, message);
        list.add(message);
    }
}
