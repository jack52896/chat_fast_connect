package util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * @author yujie
 * @createTime 2022/10/6 17:36
 * @description
 */
@Slf4j
public class RPCUtil {

    private static Properties properties;

    private static String path;

    static {
        try {
            properties = new Properties();
            properties.load(RPCUtil.class.getClassLoader().getResourceAsStream("application.properties"));
            path = properties.getProperty("controller.path");
            scan(path);
        } catch (IOException e) {
            log.error(e.getClass().getSimpleName(), e);
        }
    }

    public static  void scan(String path){
        try {
            File file = new File(Objects.requireNonNull(RPCUtil.class.getClassLoader().getResource(path.replaceAll("\\.", "/"))).getFile());
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                if(listFile.isDirectory()){
                    scan(path+"."+listFile.getName());
                }else{
                    if(!listFile.getName().endsWith(".class")){
                        continue;
                    }
                    String allFileName = path+"."+listFile.getName().replace(".class", "");
                }
            }
        } catch (Exception e) {
            log.error("加载controller层失败:{}", e.getClass().getSimpleName(), e);
        }
    }

}
