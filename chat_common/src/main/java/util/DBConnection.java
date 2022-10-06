package util;

import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * @author yujie
 * @createTime 2022/10/5 18:08
 * @description
 */
@Slf4j
public class DBConnection {

    public static String url;

    private static String userName;

    private static String pwd;

    public static Map<String, Map<String, Field>> mapperMap = new HashMap<>();

    public static Map<String, Class> tableEntityMap = new HashMap<>();

    public static Map<String, Map<String,String>> tableFieldMap = new HashMap<>();

    static {
        try {
            log.info("数据库连接开始初始化");
            Properties properties = new Properties();
            properties.load(DBConnection.class.getClassLoader().getResourceAsStream("application.properties"));
            String driverName = properties.getProperty("driverName");
            url = properties.getProperty("dataSource.url");
            userName = properties.getProperty("dataSource.userName");
            pwd = properties.getProperty("dataSource.pwd");
            Class.forName(driverName);
            log.info("数据库连接结束初始化");
            log.info("连接URL:{}", url);
        } catch (Exception e) {
            log.error("读取配置文件失败，无法打开数据库连接, 异常信息:{}", e.getClass().getSimpleName(), e);
        }

    }

    static {
        try {
            log.info("开启读取mapper.xml");
            mapperMap = new HashMap<>();
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder=factory.newDocumentBuilder();
            Document document = builder.parse(DBConnection.class.getClassLoader().getResourceAsStream("mapper.xml"));
            NodeList nodeList = document.getElementsByTagName("mapper");
            for(int  i  = 0; i< nodeList.getLength(); i++){
                Node mapper = nodeList.item(i);
                NamedNodeMap mapperNodeMap = mapper.getAttributes();
                Node table = mapperNodeMap.getNamedItem("table");
                Node entity = mapperNodeMap.getNamedItem("entity");
                tableEntityMap.put(table.getNodeValue(), Class.forName(entity.getNodeValue()));
                NodeList fieldList = mapper.getChildNodes();
                Map<String, Field> columnMap = new HashMap<>();
                Map<String, String> dataTypeMap = new HashMap<>();
                for(int j = 0; j< fieldList.getLength(); j++){
                    Node item = fieldList.item(j);
                    if(item instanceof Element){
                        NamedNodeMap column = item.getAttributes();
                        Node name = column.getNamedItem("name");
                        Node field = column.getNamedItem("field");
                        Node dataType = column.getNamedItem("dataType");
                        Class<?> aClass = Class.forName(entity.getNodeValue());
                        for (Field declaredField : aClass.getDeclaredFields()) {
                            if(declaredField.getName().equals(field.getNodeValue())){
                                columnMap.put(name.getNodeValue(), declaredField);
                                dataTypeMap.put(name.getNodeValue(), dataType.getNodeValue());
                            }
                        }
                    }
                }
                tableFieldMap.put(table.getNodeValue(), dataTypeMap);
                mapperMap.put(table.getNodeValue(), columnMap);
            }
            log.info("mapper.xml读取完毕");
        } catch (Exception e) {
            log.error(e.getClass().getSimpleName(), e);
        }
    }

    /**
     * 获取一次数据库连接
     * @return
     */
    public static Connection getConnection(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, userName, pwd);
        } catch (SQLException e) {
            log.error("获取连接失败, 异常信息为:{}", e.getClass().getSimpleName(), e);
        }
        return Optional.ofNullable(connection).orElseThrow(()->new RuntimeException("获取连接失败"));
    }


}
