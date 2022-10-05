package aspect.handler;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.DBConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yujie
 * @createTime 2022/10/5 20:03
 * @description
 */
@Slf4j
public class ConnectionHandler implements InvocationHandler {

    private Object object;

    private String sql;

    public ConnectionHandler(Object object, String sql){
        this.object = object;
        this.sql = sql;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Connection connection = DBConnection.getConnection();
        String str = "select * from ";
        ResultSet resultSet = (ResultSet) method.invoke(object, str + sql, connection);
        Map<String, Field> stringFieldMap = DBConnection.mapperMap.get(sql);
        Class aClass = DBConnection.tableEntityMap.get(sql);
        List<Object> objectList = new ArrayList<>();
        while(resultSet.next()){
            stringFieldMap.keySet().forEach(s -> {
                try {
                    Object result = aClass.newInstance();
                    String value = resultSet.getString(s);
                    Integer integer = Integer.valueOf(value);
                    Field field = stringFieldMap.get(s);
                    field.setAccessible(true);
                    field.set(result, field.getType().cast(integer));
                    objectList.add(result);
                } catch (Exception e) {
                    log.error(e.getClass().getSimpleName(), e);
                }
            });
        }
        log.info("执行sql:{}", str + sql);
        return objectList;
    }
}
