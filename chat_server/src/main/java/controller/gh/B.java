package controller.gh;

import annoation.Controller;
import annoation.RequestMapping;

/**
 * @author yujie
 * @createTime 2022/10/6 18:36
 * @description
 */
@Controller
public class B {

    @RequestMapping("/222")
    public String list(){
        return "123";
    }
}
