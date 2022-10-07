package util;

import annoation.Controller;
import annoation.RequestMapping;
import handler.HandlerMethod;
import lombok.extern.slf4j.Slf4j;

import javax.naming.ldap.Control;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yujie
 * @createTime 2022/10/6 17:36
 * @description
 */
@Slf4j
public class ClassUtil {

    public static Map<String, HandlerMethod> map = new ConcurrentHashMap<>();

    public static Map<Method, Object> methodObjectMap = new ConcurrentHashMap<>();

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
        try {
            File file = new File(Objects.requireNonNull(ClassUtil.class.getClassLoader().getResource(path.replaceAll("\\.", "/"))).getFile());
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                if(listFile.isDirectory()){
                    scan(path+"."+listFile.getName());
                }else{
                    if(!listFile.getName().endsWith(".class")){
                        continue;
                    }
                    String allFileName = path+"."+listFile.getName().replace(".class", "");
                    checkMethod(allFileName);
                }
            }
        } catch (Exception e) {
            log.error("加载controller层失败:{}", e.getClass().getSimpleName(), e);
        }
    }

    private static synchronized void checkMethod(String allFileName) {
        try {
            Class<?> aClass = Class.forName(allFileName);
            if(!aClass.isAnnotationPresent(Controller.class)){
                return;
            }
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                if(method.isAnnotationPresent(RequestMapping.class)){
                    String value = method.getAnnotation(RequestMapping.class).value();
                    HandlerMethod handlerMethod = new HandlerMethod();
                    handlerMethod.setUrl(value);
                    handlerMethod.setMethod(method);
                    if(Objects.nonNull(map.get(value))){
                        throw new RuntimeException("请检查url路径是否重复:"+value);
                    }
                    map.put(value, handlerMethod);
                    Optional.ofNullable(methodObjectMap.get(method)).ifPresentOrElse(o -> {
                        throw new RuntimeException("路径重复注册");
                    }, ()->{
                        try {
                            Object object = aClass.newInstance();
                            methodObjectMap.put(method, object);
                        } catch (Exception e) {
                            log.error(e.getClass().getSimpleName(), e);
                        }
                    });
                }
            }
        } catch (ClassNotFoundException e) {
            log.error(e.getClass().getSimpleName(), e);
        }
    }

    public static Object getObject(Field field, String value) {
        Object resultValue = null;
        if(field.getType().equals(Integer.class)){
            resultValue = Integer.valueOf(value);
        }else if(field.getType().equals(String.class)){
            resultValue = value;
        }
        return resultValue;
    }
}
