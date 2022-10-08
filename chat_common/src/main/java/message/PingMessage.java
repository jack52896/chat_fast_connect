package message;

import lombok.Data;

import java.util.Map;

/**
 * @author yujie
 * @createTime 2022/10/8 13:41
 * @description
 */
@Data
public class PingMessage {

    private String messageId;

    private PingType pingType;

    private String serviceName;

    private String hostName;

    private String hostAddress;

    private String port;

    private Map<String, PingMessage> map;

    public PingMessage() {
    }

    public PingMessage(String serviceName, String hostName, String hostAddress, String port) {
        this.serviceName = serviceName;
        this.hostName = hostName;
        this.hostAddress = hostAddress;
        this.port = port;
    }

    public PingMessage(String hostName, String hostAddress, String port) {
        this.hostName = hostName;
        this.hostAddress = hostAddress;
        this.port = port;
    }

    public enum PingType{
        NONE_PACKAGE("空包"), PUSH_SERVICES("主动推送服务信息"), GET_SERVICES("申请获取服务"), RETURN_SERVICES("返回服务信息");
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
