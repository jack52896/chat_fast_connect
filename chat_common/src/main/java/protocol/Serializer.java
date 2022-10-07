package protocol;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Objects;

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
                return new byte[0];
            }

            @Override
            public <T> T decode(byte[] bytes) {
                return null;
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
