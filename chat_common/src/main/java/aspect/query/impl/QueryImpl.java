package aspect.query.impl;

import aspect.query.Query;
import aspect.query.dsl.DslBuilder;

/**
 * @author yujie
 * @createTime 2022/10/5 20:37
 * @description
 */
public class QueryImpl {
    public static Object query(DslBuilder dslBuilder){
        return  Query.query(dslBuilder).executorQuery(null, null);
    }
}
