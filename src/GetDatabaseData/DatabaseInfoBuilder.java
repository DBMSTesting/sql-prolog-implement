package GetDatabaseData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInfoBuilder {
    private static final String DB_URL = "jdbc:mysql://211.81.52.44:3308?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "MySQLTesting";

    public static void main(String[] args) {
        String filePath = "/Users/tashou/fsdownload/database1017-cur.log";
        String databaseInfo = buildDatabaseInfo(filePath);
        System.out.println(databaseInfo);
    }

    public static String buildDatabaseInfo(String filePath) {
        String []database = filePath.split("/");
        String []split1 = database[database.length-1].split("-cur");
        String databaseName = split1[0];
        StringBuilder databaseInfoBuilder = new StringBuilder();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();



             BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            statement.execute("DROP DATABASE IF EXISTS " + databaseName);
            statement.execute("CREATE DATABASE " + databaseName);
            statement.execute("USE " + databaseName);
            while ((line = br.readLine()) != null) {
                String []sqlNotSelect = line.split("--");
                if(sqlNotSelect.length>=1){
                    String []keyword = sqlNotSelect[0].split("\\s+");
                    if(keyword.length>=1){
                        if(!keyword[0].equals("SELECT") && !keyword[0].equals("")){
                            System.out.println(line);
                            try{
                                statement.execute(line);
                            }catch (SQLException sqlException){
                                sqlException.printStackTrace();
                            }
                        }
                    }
                }
            }

            String databaseInfo = InfoBuilder();
            //statement.close();
            System.out.println("Database tables created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return databaseInfoBuilder.toString();
    }

    public static String InfoBuilder() throws SQLException {
        StringBuilder databaseInfoBuilder = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();){

            databaseInfoBuilder.append("[");
            Statement statement1 = connection.createStatement();
            //statement.execute("USE " + databaseName);
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                databaseInfoBuilder.append("[")
                        .append(tableName)
                        .append(",");

                //ResultSet columnsResultSet = connection.getMetaData().getColumns(null, null, tableName, null);
                //System.out.println(columnsResultSet);

                List<String> columnNames = new ArrayList<>();
//                while (columnsResultSet.next()) {
//                    String columnName = columnsResultSet.getString("COLUMN_NAME");
//                    columnNames.add(columnName);
//                }
//                HashSet<String> columnNamesSet=new HashSet<>(columnNames);
//                List<String> listWithoutDuplicates = new ArrayList<>(columnNamesSet);
//                databaseInfoBuilder.append(listWithoutDuplicates.toString())
//                        .append(",");

                ResultSet dataResultSet = statement1.executeQuery("SELECT * FROM " + tableName);
                ResultSetMetaData rsmd = dataResultSet.getMetaData();
                int count = rsmd.getColumnCount();
                for(int i=0;i<count;i++){
                    columnNames.add(rsmd.getColumnName(i+1));
                }
                databaseInfoBuilder.append(columnNames.toString())
                        .append(",");
                while (dataResultSet.next()) {
                    List<String> rowData = new ArrayList<>();
                    for (String columnName : columnNames) {
                        String value = dataResultSet.getString(columnName);
                        rowData.add(value);
                    }
                    databaseInfoBuilder.append(rowData.toString())
                            .append(",");
                }
                //dataResultSet.close();
                removeTrailingComma(databaseInfoBuilder);

                databaseInfoBuilder.append("],");
            }
            //resultSet.close();
            //statement.close();
            removeTrailingComma(databaseInfoBuilder);

            databaseInfoBuilder.append("]");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return databaseInfoBuilder.toString();
    }


    private static void removeTrailingComma(StringBuilder builder) {
        int lastIndex = builder.length() - 1;
        if (builder.charAt(lastIndex) == ',') {
            builder.deleteCharAt(lastIndex);
        }
    }
}
