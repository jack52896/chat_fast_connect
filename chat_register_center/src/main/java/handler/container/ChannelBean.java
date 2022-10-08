package handler.container;

import message.PingMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yujie
 * @createTime 2022/10/8 13:58
 * @description
 */
public class ChannelBean {

    public static Map<String, PingMessage> map ;
    static {
        map = new HashMap<>();
    }

    /**
     * 获取服务列表
     */
    public static Set<String> listService(){
        return map.keySet();
    }
}
