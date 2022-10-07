package controller.gh;

import annoation.Controller;
import annoation.RequestBody;
import annoation.RequestMapping;
import domain.User;

/**
 * @author yujie
 * @createTime 2022/10/6 18:36
 * @description
 */
@Controller
public class B {

    @RequestMapping("/222")
    public String list(){
        String str = "{\"name\":\"123\"}";
        return str + "123";
    }

    @RequestMapping("/json")
    public String getJson(@RequestBody User user){
        return "holle,world"+user.getId();
    }
}
