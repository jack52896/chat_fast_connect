package aspect.query.impl;

import aspect.query.Query;

/**
 * @author yujie
 * @createTime 2022/10/5 20:37
 * @description
 */
public class QueryImpl {
    public static Object query(String sql){
        return  Query.query(sql).executorQuery(null, null);
    }
}
