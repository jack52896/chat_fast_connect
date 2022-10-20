package aspect.query;

import aspect.query.dsl.DslBuilder;
import aspect.service.CustomizeStatement;
import aspect.service.impl.CustomizeStatementImpl;
import lombok.extern.slf4j.Slf4j;
import pool.DBConnection;
import pool.DataSourcePool;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yujie
 * @createTime 2022/10/5 19:36
 * @description
 */
@Slf4j
public class Query  {

    protected static CustomizeStatement customizeStatement = null;

    static {
        customizeStatement = new CustomizeStatementImpl();
    }

    public static CustomizeStatement query(DslBuilder dslBuilder){
        CustomizeStatementImpl customizeStatement = new CustomizeStatementImpl();
        Object object = Proxy.newProxyInstance(customizeStatement.getClass().getClassLoader(),
                customizeStatement.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Connection connection = DataSourcePool.getConnection();
                        ResultSet resultSet = (ResultSet) method.invoke(customizeStatement, dslBuilder.transform(), connection);
                        Map<String, Field> stringFieldMap = DBConnection.mapperMap.get(dslBuilder.getBasePath());
                        Class aClass = DBConnection.tableEntityMap.get(dslBuilder.getBasePath());
                        List<Object> objectList = new ArrayList<>();
                        while(resultSet.next()){
                            stringFieldMap.keySet().forEach(s -> {
                                try {
                                    Object result = aClass.newInstance();
                                    String fieldType = DBConnection.tableFieldMap.get(dslBuilder.getBasePath()).get(s);
                                    Object value = resultSet.getObject(s, Class.forName(fieldType));
                                    Field field = stringFieldMap.get(s);
                                    field.setAccessible(true);
                                    field.set(result, value);
                                    objectList.add(result);
                                } catch (Exception e) {
                                    log.error(e.getClass().getSimpleName(), e);
                                }
                            });
                        }
                        log.info("connection:{}", connection);
                        log.info("执行sql:{}", dslBuilder.transform());
                        log.info("执行完毕，释放connection至连接池");
                        DataSourcePool.releaseConnection(connection);
                        return objectList;
                    }
                });
        return (CustomizeStatement) object;
    }

}
