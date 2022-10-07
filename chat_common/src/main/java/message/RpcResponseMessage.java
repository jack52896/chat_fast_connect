package message;

import lombok.Data;

/**
 * @author yujie
 * @createTime 2022/10/7 16:52
 * @description
 */
@Data
public class RpcResponseMessage {

    /**
     * 消息id
     */
    private String messageId;
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    public RpcResponseMessage() {
    }

    public RpcResponseMessage(String messageId, Object returnValue, Exception exceptionValue) {
        this.messageId = messageId;
        this.returnValue = returnValue;
        this.exceptionValue = exceptionValue;
    }
}
