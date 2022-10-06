import aspect.query.Query;
import aspect.query.dsl.DslBuilder;
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
//        System.out.println(QueryImpl.query("user"));
        DslBuilder dslBuilder = new DslBuilder();
        System.out.println(dslBuilder.select("id").from("user").where("id", "1").transform());
        System.out.println(QueryImpl.query(dslBuilder));
        System.out.println(QueryImpl.query(dslBuilder));
        System.out.println(QueryImpl.query(dslBuilder));
        System.out.println(QueryImpl.query(dslBuilder));
        System.out.println(QueryImpl.query(dslBuilder));
        System.out.println(QueryImpl.query(dslBuilder));
        System.out.println(QueryImpl.query(dslBuilder));


    }
}
