package annoation;

import java.lang.annotation.*;

/**
 * @author dailinyu
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {

}
