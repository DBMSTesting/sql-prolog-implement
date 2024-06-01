package java_prolog;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptMysql {
    public static String scriptMysql(String sql, String database) throws Throwable {
        String result = null;
        try {
            int ColumnCount;
            //int RowCount;
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/"+database+"?useUnicode=true&characterEncoding=utf8&useSSL=true"; //换成要连接的数据库信息
            String user = "root";
            String password = "tcl990522";
            Class.forName("com.mysql.jdbc.Driver");

            Connection conn = (Connection) DriverManager.getConnection ( url, user, password );
            int sum = 0;
            for(int j = 0;j<sql.length();j++){
                if(sql.substring(j,j+1).equals("'")){
                    sum ++;
                }
            }if(sum%2!=0){
                return "引号错误";
            }
            String sqls = sql; //sql
            PreparedStatement ps = conn.prepareStatement ( sqls );
            ResultSet rs = ps.executeQuery ();
            List list = new ArrayList<String>();

            ResultSetMetaData rsmd = rs.getMetaData ();
            while (rs.next ()) {
                ColumnCount = rsmd.getColumnCount ();
                Map<String,Object> rowData = new HashMap<String,Object>();

                for (int i = 1; i <= ColumnCount; i++) {
                    rowData.put(rsmd.getColumnName(i),rs.getObject(i));
//                            System.out.println(rsmd.getColumnName(i));
//                            System.out.println(rs.getObject(i));
                    list.add(rs.getObject(i));
                }
            }
            ps.close ();
            conn.close ();
            result =  list.toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace ();
        } catch (SQLException e) {
            e.printStackTrace ();
        }
        return result;
    }
}
