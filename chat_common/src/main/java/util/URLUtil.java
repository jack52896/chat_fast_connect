package util;

import io.netty.util.concurrent.Promise;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yujie
 * @createTime 2022/10/6 17:26
 * @description
 */
public class URLUtil {

    public static Map<String, Promise<Object>> map = new HashMap<>();

    public static Map<String, String> formData(String str){
        Map<String, String> map = new HashMap<>();
        String[] groups = str.split("&");
        for (String group : groups) {
            String[] groupList = group.split("=");
            map.put(groupList[0], groupList[1]);
        }
        return map;
    }

    public static Object json(String str){
        return null;
    }
}
