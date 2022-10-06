package aspect.query.dsl;

/**
 * @author yujie
 * @createTime 2022/10/5 23:49
 * @description
 */
public class DslBuilder {

    protected String sql;

    private String basePath;

    private String[] args;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public DslBuilder select(String...args){
        sql="select ";
        this.args = args;
        for (String arg : args) {
            sql += arg;
        }
        return this;
    }

    public DslBuilder from(String table){
        sql += (" from "  + table);
        this.basePath = table;
        return this;
    }

    public DslBuilder where(String target, String value){
        sql += " where " + target + " = " + value;
        return this;
    }

    public String transform(){
        return this.sql;
    }


}
