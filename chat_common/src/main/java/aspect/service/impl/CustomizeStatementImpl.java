package aspect.service.impl;

import aspect.service.CustomizeStatement;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author yujie
 * @createTime 2022/10/5 20:05
 * @description
 */
@Slf4j
public class CustomizeStatementImpl implements CustomizeStatement {

    @Override
    public Object executorQuery(String sql, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            log.error(e.getClass().getSimpleName(), e);
        }
        return null;
    }
}
