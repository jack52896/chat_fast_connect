package util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author linyu.dai
 * @project chat_fast_connect
 * @description
 * @creat 2022/10/7 15:35:42
 */
@Slf4j
public class JsonUtil {

    public static Object readFromJsonString(String jsonData, Class<?> c) {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Object result = null;
        try {
            result = om.readValue(jsonData, c);
        } catch (Exception e) {
            log.error("转化数据{}为{}失败，{}", jsonData, c.getTypeName(), e);
        }
        return result;
    }

    public static <T> String objectToString(T object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("{}创建json数据错误", object.getClass().getSimpleName(), e);
            return null;
        }
    }
}
