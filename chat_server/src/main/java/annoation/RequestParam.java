package annoation;

import java.lang.annotation.*;

/**
 * @author yujie
 * @createTime 2022/10/7 11:35
 * @description
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    String value();

}
