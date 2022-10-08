package handler.container;

import io.netty.channel.Channel;
import message.PingMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yujie
 * @createTime 2022/10/8 13:58
 * @description
 */
public class ChannleBean {

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
