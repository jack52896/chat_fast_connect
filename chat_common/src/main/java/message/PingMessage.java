package message;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author yujie
 * @createTime 2022/10/8 13:41
 * @description
 */
@Data
public class PingMessage {

    private String messageId = UUID.randomUUID().toString();

    private PingType pingType;

    private String error;

    /**
     * 实例名称
     */
    private String applicationName;

    private String hostName;

    private String hostAddress;

    private String port;

    private String address;

    /**
     * K- 所有的服务
     */
    private Set<String> serviceNames;

    public PingMessage() {
    }

    public enum PingType{
        ERROR_PACKAGE("错误信息"), SIGNALL("确认服务是否存活"),NONE_PACKAGE("空包"), PUSH_SERVICES("主动推送服务信息"), GET_SERVICES("主动拉取服务信息"), RETURN_SERVICES("返回服务信息");
        private String message;
        PingType(String message){
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
