package pool;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yujie
 * @createTime 2022/10/5 18:52
 * @description 数据库连接池
 */
@Slf4j
public class DataSourcePool {

    private static LinkedList<Connection> freeConnnection = new LinkedList<>();

//    private static LinkedList<Connection> useConnection = new LinkedList<>();

    private static Lock lock = new ReentrantLock();

    private static Condition condition = lock.newCondition();

    //空闲池中可用的连接
    private static AtomicInteger connections;

    public DataSourcePool(){
    }

    static {
        connections = new AtomicInteger(0);
        init();
    }

    public static void init(){
        log.info("初始化连接池");
        log.info("连接池配置, 最大空闲连接数:{}, 最大的在线连接数:{}, 等待获取连接时间:{}",
                DBConnection.properties.getProperty("dataSource.maxConnections"),
                DBConnection.properties.getProperty("dataSource.maxActiveConnections"),
                DBConnection.properties.getProperty("dataSource.waitTime"));
        Integer initConnection = Integer.parseInt(DBConnection.properties.getProperty("dataSource.initConnections"));
        for(int i = 0 ;i < initConnection;i++){
            freeConnnection.add(DBConnection.getConnection());
            connections.incrementAndGet();
        }
    }

    public static synchronized Connection getConnection(){
            Connection connection = null;
            if(connections.get() < Integer.parseInt(DBConnection.properties.getProperty("dataSource.maxActiveConnections"))){
                if(freeConnnection.size() > 0){
                    connection = freeConnnection.removeFirst();
                }else{
                    connection = DBConnection.getConnection();
                }

                if(DBConnection.isActive(connection)){
//                    useConnection.add(connection);
                    connections.addAndGet(1);
                }else{
                    connection = DBConnection.getConnection();
                }
            }else{
                try {
                    condition.await(Long.parseLong(DBConnection.properties.getProperty("dataSource.waitTime")), TimeUnit.SECONDS);
                    connection = getConnection();
                } catch (Exception e) {
                    log.error(e.getClass().getSimpleName(), e);
                }
            }
        return connection;
    }

    public static synchronized void releaseConnection(Connection connection){
        if(DBConnection.isActive(connection)){
            if(freeConnnection.size() < Integer.parseInt(DBConnection.properties.getProperty("dataSource.maxConnections"))){
                freeConnnection.add(connection);
            }else{
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("关闭连接失败, 异常信息:{}",e.getClass().getSimpleName(), e);
                }
            }
            connections.decrementAndGet();
        }else{
            log.error("关闭连接失败，连接已丢失");
        }
    }

}
