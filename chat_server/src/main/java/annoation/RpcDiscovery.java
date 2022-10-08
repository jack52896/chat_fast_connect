package annoation;

import java.lang.annotation.*;

/**
 * @author yujie
 * @createTime 2022/10/8 15:00
 * @description
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcDiscovery {

    String value();

}
