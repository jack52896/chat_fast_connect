package util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

/**
 * @author yujie
 * @createTime 2022/10/7 17:55
 * @description
 */
@Slf4j
public class PropertiesUtil {

    public static Properties properties;

    static {
        try {
            properties = new Properties();
            properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            log.error(e.getClass().getSimpleName(), e);
        }
    }
}
