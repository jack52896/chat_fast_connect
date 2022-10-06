package util;

import annoation.Controller;
import annoation.RequestMapping;
import handler.HandlerMethod;
import lombok.extern.slf4j.Slf4j;

import javax.naming.ldap.Control;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yujie
 * @createTime 2022/10/6 17:36
 * @description
 */
@Slf4j
public class ClassUtil {

    private static Map<String, HandlerMethod> map = new ConcurrentHashMap<>();

    private static Properties properties;

    private static String path;

    static {
        try {
            properties = new Properties();
            properties.load(ClassUtil.class.getClassLoader().getResourceAsStream("application.properties"));
            path = properties.getProperty("controller.path");
            scan(path);
        } catch (IOException e) {
            log.error(e.getClass().getSimpleName(), e);
        }
    }

    public static  void scan(String path){
        File file = new File(Objects.requireNonNull(ClassUtil.class.getClassLoader().getResource(path)).getFile());
        for (File listFile : Objects.requireNonNull(file.listFiles())) {
            if(listFile.isDirectory()){
                scan(path+"."+listFile);
            }else{
                if(!listFile.getName().endsWith(".class")){
                    continue;
                }
                String allFileName = path+"."+listFile.getName().replace(".class", "");
                checkMethod(allFileName);
            }
        }
    }

    private static void checkMethod(String allFileName) {
        try {
            Class<?> aClass = Class.forName(allFileName);
            if(!aClass.isAnnotationPresent(Controller.class)){
                return;
            }
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                if(method.isAnnotationPresent(RequestMapping.class)){
//TODO
                }
            }
        } catch (ClassNotFoundException e) {
            log.error(e.getClass().getSimpleName(), e);
        }
    }

}
