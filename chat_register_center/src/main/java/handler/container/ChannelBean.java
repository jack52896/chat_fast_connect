package handler.container;

import message.PingMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yujie
 * @createTime 2022/10/8 13:58
 * @description
 */
public class ChannelBean {

    /**
     * k - ip+port
     * v - applicationName
     */
    public static Map<String, String> addressAppMap = new HashMap<>();

    /**
     * k - applicationName
     * v - pingMessage
     */
    public static Map<String, PingMessage> appServiceMap = new HashMap<>();

}
