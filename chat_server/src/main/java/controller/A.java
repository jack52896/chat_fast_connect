package controller;

import annoation.Controller;
import annoation.RequestMapping;
import annoation.RequestParam;
import domain.User;

/**
 * @author yujie
 * @createTime 2022/10/6 18:36
 * @description
 */
@Controller
public class A {

    @RequestMapping("/lujing")
    public String list(@RequestParam("user") User user){
        return String.valueOf(user.getId());
    }
}
