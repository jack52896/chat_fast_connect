import aspect.query.Query;
import aspect.query.impl.QueryImpl;
import domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author yujie
 * @createTime 2022/10/5 18:28
 * @description
 */
public class test1 {
    public static void main(String[] args) throws SQLException {
//        System.out.println(QueryImpl.query("show databases"));
        System.out.println(QueryImpl.query("user"));
    }
}
