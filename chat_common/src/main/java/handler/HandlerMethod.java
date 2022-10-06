package handler;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author yujie
 * @createTime 2022/10/6 17:18
 * @description
 */
@Data
public class HandlerMethod {

    private String url;

    private Method method;

}
