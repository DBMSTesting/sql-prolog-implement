package sqlancer.mysql.gen;

import java.util.List;

public class Constrain {

    public String tableName;
    public List<String> columnName;
    public List<List<String>> constrain;
    public List<List<String>> constrainContent;
    public String withoutConstrain;

    public Constrain(String tableName,List<String> columnName,List<List<String>> constrain,List<List<String>> constrainContent,String withoutConstrain){
        this.tableName = tableName;
        this.columnName = columnName;
        this.constrain = constrain;
        this.constrainContent = constrainContent;
        this.withoutConstrain = withoutConstrain;
    }
}
