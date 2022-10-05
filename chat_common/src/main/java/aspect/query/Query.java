package aspect.query;

import aspect.handler.ConnectionHandler;
import aspect.query.dsl.DslBuilder;
import aspect.service.CustomizeStatement;
import aspect.service.impl.CustomizeStatementImpl;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;

/**
 * @author yujie
 * @createTime 2022/10/5 19:36
 * @description
 */
@Slf4j
public class Query  {

    public static CustomizeStatement query(DslBuilder dslBuilder){
        CustomizeStatementImpl customizeStatement = new CustomizeStatementImpl();
        Object object = Proxy.newProxyInstance(customizeStatement.getClass().getClassLoader(), customizeStatement.getClass().getInterfaces(), new ConnectionHandler(customizeStatement, dslBuilder));
        return (CustomizeStatement) object;
    }

}
