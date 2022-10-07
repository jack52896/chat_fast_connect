package protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public interface Serializer {

    <T> byte[] encode(T t);

    <T> T decode(byte[] bytes);

    @Slf4j
    enum SerializerType implements Serializer{

        jackson("jackson"){
            @Override
            public <T> byte[] encode(T t) {
                byte[] bytes = new byte[0];
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(t);
                    bytes = bos.toByteArray();
                } catch (IOException e) {
//                    e.printStackTrace();
                    log.error(e.getClass().getSimpleName(), e);
                }
                return bytes;
            }

            @Override
            public <T> T decode(byte[] bytes) {
                Object t = new Object();
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    t = ois.readObject();
                } catch (Exception e) {
//                    e.printStackTrace();
                    log.error(e.getClass().getSimpleName(), e);
                }
                return (T) t;
            }
        },
        json("json"){
            @Override
            public <T> byte[] encode(T t) {
                ObjectMapper objectMapper = new ObjectMapper();
                String s = null;
                try {
                    s = objectMapper.writeValueAsString(t);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return Optional.ofNullable(s).orElseThrow(()->new RuntimeException("序列化失败")).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public <T> T decode(byte[] bytes) {
                ObjectMapper objectMapper = new ObjectMapper();
                Object object = null;
                try {
                    object = objectMapper.readValue(bytes, Object.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return (T) Optional.ofNullable(object).orElseThrow(()->new RuntimeException("反序列化失败"));
            }
        };

        private String message;

        SerializerType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public static SerializerType alias(String message){
            for (SerializerType value : SerializerType.values()) {
                if (Objects.equals(value.getMessage(), message)) {
                    return value;
                }
            }
            return SerializerType.jackson;
        }

    }
//    enum other implements Serializer{
//        jackson, json;
//
//        @Override
//        public <T> byte[] encode(T t) {
//            return new byte[0];
//        }
//
//        @Override
//        public <T> T decode(byte[] bytes) {
//            return null;
//        }
//    }
}
