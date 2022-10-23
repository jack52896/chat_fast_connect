package controller;

import annoation.Controller;
import annoation.RequestMapping;
import annoation.RequestParam;
import customize.request.MultipartRquest;
import domain.User;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yujie
 * @createTime 2022/10/6 18:36
 * @description
 */
@Controller
@Slf4j
public class A {

    @RequestMapping("/lujing")
    public String list(@RequestParam("user") User user){
        return String.valueOf(user.getId());
    }

    @RequestMapping("/testUpload")
    public String testUpload(MultipartRquest request){
        log.info("请求类型:{}", request.getContentType());
        return request.getContentType();
    }
}
