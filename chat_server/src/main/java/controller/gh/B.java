package controller.gh;

import annoation.Controller;
import annoation.RequestBody;
import annoation.RequestMapping;
import domain.Student;
import domain.User;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yujie
 * @createTime 2022/10/6 18:36
 * @description
 */
@Controller
@Slf4j
public class B {

    @RequestMapping("/222")
    public String list(){
        String str = "{\"name\":\"123\"}";
        return str + "123";
    }

    @RequestMapping("/json")
    public String getJson(@RequestBody Student student){
        log.info("Student: {}",student);
        return "holle,world";
    }
}
