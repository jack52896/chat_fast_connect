package aspect.service;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * @author yujie
 * @createTime 2022/10/5 20:04
 * @description
 */
public interface CustomizeStatement {

    Object executorQuery(String sql, Connection connection);
}
