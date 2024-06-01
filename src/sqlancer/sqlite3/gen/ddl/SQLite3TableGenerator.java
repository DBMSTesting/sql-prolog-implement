package sqlancer.sqlite3.gen.ddl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import sqlancer.IgnoreMeException;
import sqlancer.Randomly;
import sqlancer.common.DBMSCommon;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.mysql.gen.Constrain;
import sqlancer.sqlite3.SQLite3Errors;
import sqlancer.sqlite3.SQLite3GlobalState;
import sqlancer.sqlite3.SQLite3Options.SQLite3OracleFactory;
import sqlancer.sqlite3.gen.SQLite3ColumnBuilder;
import sqlancer.sqlite3.gen.SQLite3Common;
import sqlancer.sqlite3.schema.SQLite3Schema;
import sqlancer.sqlite3.schema.SQLite3Schema.SQLite3Column;
import sqlancer.sqlite3.schema.SQLite3Schema.SQLite3Table;
import sqlancer.sqlite3.schema.SQLite3Schema.SQLite3Table.TableKind;

/**
 * See https://www.sqlite.org/lang_createtable.html
 *
 * TODO What's missing:
 * <ul>
 * <li>CREATE TABLE ... AS SELECT Statements</li>
 * </ul>
 */
public class SQLite3TableGenerator {

    private final StringBuilder sb = new StringBuilder();
    private final String tableName;
    private int columnId;
    private boolean containsPrimaryKey;
    private boolean containsAutoIncrement;
    private final List<String> columnNames = new ArrayList<>();
    private final List<SQLite3Column> columns = new ArrayList<>();
    private final SQLite3Schema existingSchema;
    private final SQLite3GlobalState globalState;
    private boolean tempTable;

    public static Constrain constrains;
    public static String table;
    public static List<String> columnsList = new ArrayList<>();
    public static List<List<String>> constrainList = new ArrayList<>();
    public static List<List<String>> constrainContent = new ArrayList<>();
    public static StringBuilder sbNoConstrain = new StringBuilder();
    public static String columnDataType = "";

    public static Constrain getConstrain(){
        constrains = new Constrain(table,columnsList,constrainList,constrainContent,sbNoConstrain.toString());
        return constrains;
    }

    public SQLite3TableGenerator(String tableName, SQLite3GlobalState globalState) {
        this.tableName = tableName;
        this.globalState = globalState;
        this.existingSchema = globalState.getSchema();
    }

    public static SQLQueryAdapter createRandomTableStatement(SQLite3GlobalState globalState) {
        if (globalState.getSchema().getTables().getTables()
                .size() > globalState.getDbmsSpecificOptions().maxNumTables) {
            throw new IgnoreMeException();
        }
        return createTableStatement(globalState.getSchema().getFreeTableName(), globalState);
    }

    public static SQLQueryAdapter createTableStatement(String tableName, SQLite3GlobalState globalState) {
        SQLite3TableGenerator sqLite3TableGenerator = new SQLite3TableGenerator(tableName, globalState);
        sqLite3TableGenerator.start();
        ExpectedErrors errors = new ExpectedErrors();
        SQLite3Errors.addTableManipulationErrors(errors);
        errors.add("second argument to likelihood() must be a constant between 0.0 and 1.0");
        errors.add("non-deterministic functions prohibited in generated columns");
        errors.add("subqueries prohibited in generated columns");
        errors.add("parser stack overflow");
        errors.add("malformed JSON");
        errors.add("JSON cannot hold BLOB values");
        return new SQLQueryAdapter(sqLite3TableGenerator.sb.toString(), errors, true);
    }

    public void start() {
        sb.append("CREATE ");
        sbNoConstrain.append("CREATE ");
//        if (globalState.getDbmsSpecificOptions().testTempTables && Randomly.getBoolean()) {
//            tempTable = true;
//            if (Randomly.getBoolean()) {
//                sb.append("TEMP ");
//            } else {
//                sb.append("TEMPORARY ");
//            }
//        }
        sb.append("TABLE ");
        sbNoConstrain.append("TABLE ");
        if (Randomly.getBoolean()) {
            sb.append("IF NOT EXISTS ");
            sbNoConstrain.append("IF NOT EXISTS ");
        }
        sb.append(tableName);
        table = tableName;
        sb.append(" (");
        sbNoConstrain.append(tableName);
        sbNoConstrain.append(" (");
        boolean allowPrimaryKeyInColumn = Randomly.getBoolean();
        int nrColumns = 1 + Randomly.smallNumber();
        for (int i = 0; i < nrColumns; i++) {
            columns.add(SQLite3Column.createDummy(DBMSCommon.createColumnName(i)));
        }
        for (int i = 0; i < nrColumns; i++) {
            if (i != 0) {
                sb.append(", ");
                sbNoConstrain.append(", ");
            }
            String columnName = DBMSCommon.createColumnName(columnId);
            SQLite3ColumnBuilder columnBuilder = new SQLite3ColumnBuilder()
                    .allowPrimaryKey(allowPrimaryKeyInColumn && !containsPrimaryKey);
            sb.append(columnName);
            sb.append(" ");
            sbNoConstrain.append(columnName);
            sbNoConstrain.append(" ");
            List<String> columnConstrain = new ArrayList<>();
            List<String> columnConstrainContent = new ArrayList<>();
            sb.append(columnBuilder.createColumn(columnName, globalState, columns,columnConstrain,columnConstrainContent));
            sb.append(" ");
//            sbNoConstrain.append(columnBuilder.createColumn(columnName, globalState, columns));
//            sbNoConstrain.append(" ");
            if (columnBuilder.isContainsAutoIncrement()) {
                this.containsAutoIncrement = true;
            }
            if (columnBuilder.isContainsPrimaryKey()) {
                this.containsPrimaryKey = true;
            }

            columnNames.add(columnName);
            columnsList.add(columnName);
            constrainList.add(columnConstrain);
            constrainContent.add(columnConstrainContent);
            columnId++;
        }
//        List<String> constrainOfConlumn = new ArrayList<>();
//        List<String> constrainContentOfConlumn = new ArrayList<>();
        if (!containsPrimaryKey && Randomly.getBooleanWithSmallProbability()) {
            addColumnConstraints("PRIMARY KEY");
            containsPrimaryKey = true;
        }
        if (Randomly.getBooleanWithSmallProbability()) {
            for (int i = 0; i < Randomly.smallNumber(); i++) {
                addColumnConstraints("UNIQUE");
            }
        }

        if (globalState.getDbmsSpecificOptions().testForeignKeys && Randomly.getBooleanWithSmallProbability()) {
            addForeignKey();
        }

        if (globalState.getDbmsSpecificOptions().testCheckConstraints && globalState
                .getDbmsSpecificOptions().oracles != SQLite3OracleFactory.PQS /*
                                                                               * we are currently lacking a parser to
                                                                               * read column definitions, and would
                                                                               * interpret a COLLATE in the check
                                                                               * constraint as belonging to the column
                                                                               */
                && Randomly.getBooleanWithRatherLowProbability()) {
            String checkStr = SQLite3Common.getCheckConstraint(globalState, columns);
            sb.append("CHECK ("+checkStr+") ");
            constrainList.get(0).add("CHECK");
            constrainContent.get(0).add(checkStr);
            //sbNoConstrain.append(SQLite3Common.getCheckConstraint(globalState, columns));
        }

        sb.append(")");
        sbNoConstrain.append(")");
        if (globalState.getDbmsSpecificOptions().testWithoutRowids && containsPrimaryKey && !containsAutoIncrement
                && Randomly.getBoolean()) {
            // see https://sqlite.org/withoutrowid.html
            sb.append(" WITHOUT ROWID");
            sbNoConstrain.append(" WITHOUT ROWID");
        }
//        constrainList.add(constrainOfConlumn);
//        constrainContent.add(constrainContentOfConlumn);
    }

    private void addColumnConstraints(String s) {
        sb.append(", " + s + " (");
        for (int i = 0; i < Randomly.smallNumber() + 1; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            String columnName = Randomly.fromList(columnNames);
            sb.append(columnName);
            columnsList.add(columnName);
            boolean flag = false;
            for(int j = 0;j<columnsList.size();j++){
                if(columnsList.get(i).equals(columnName)){
                    constrainList.get(i).add(s);
                    constrainContent.get(i).add("");
                    flag = true;
                    break;
                }
            }
            if(!flag){
                List<String> columnConstrain = new ArrayList<>();
                List<String> columnConstrainContent = new ArrayList<>();
                columnConstrain.add(s);
                columnConstrainContent.add("");
                constrainList.add(columnConstrain);
                constrainContent.add(columnConstrainContent);
            }
//            if (Randomly.getBoolean()) {
//                sb.append(Randomly.fromOptions(" ASC", " DESC"));
//            }
        }
        sb.append(")");
    }

//    /**
//     * @see https://www.sqlite.org/foreignkeys.html
//     */
    private void addForeignKey() {
        assert globalState.getDbmsSpecificOptions().testForeignKeys;
        List<String> foreignKeyColumns;
        if (Randomly.getBoolean()) {
            foreignKeyColumns = Arrays.asList(Randomly.fromList(columnNames));
        } else {
            foreignKeyColumns = new ArrayList<>();
            do {
                foreignKeyColumns.add(Randomly.fromList(columnNames));
            } while (Randomly.getBoolean());
        }
        sb.append(", FOREIGN KEY(");
        String foreignKeyList = foreignKeyColumns.stream().collect(Collectors.joining(", "));
        String []foreign = foreignKeyList.split(", ");
        List<Integer> columnid = new ArrayList<>();
        for(int i = 0;i<foreign.length;i++){
            for(int j = 0;j<columnsList.size();j++){
                if(foreign[i].equals(columnsList.get(j))){
                    columnid.add(j);
                    constrainList.get(j).add("FOREIGN KEY");
                    break;
                }
            }

        }

        sb.append(foreignKeyList);
        sb.append(")");
        sb.append(" REFERENCES ");
        String referencedTableName;
        List<String> columns = new ArrayList<>();
        if (existingSchema.getDatabaseTables().isEmpty() || Randomly.getBooleanWithSmallProbability()) {
            // the foreign key references our own table
            referencedTableName = tableName;
            for (int i = 0; i < foreignKeyColumns.size(); i++) {
                columns.add(Randomly.fromList(columnNames));
            }
        } else {
            final TableKind type = tempTable ? TableKind.TEMP : TableKind.MAIN;
            List<SQLite3Table> applicableTables = existingSchema.getTables().getTables().stream()
                    .filter(t -> t.getTableType() == type).collect(Collectors.toList());
            if (applicableTables.isEmpty()) {
                referencedTableName = tableName;
                for (int i = 0; i < foreignKeyColumns.size(); i++) {
                    columns.add(Randomly.fromList(columnNames));
                }
            } else {
                SQLite3Table randomTable = Randomly.fromList(applicableTables);
                referencedTableName = randomTable.getName();
                for (int i = 0; i < foreignKeyColumns.size(); i++) {
                    columns.add(randomTable.getRandomColumn().getName());
                }
            }
        }
        sb.append(referencedTableName);
        sb.append("(");
        sb.append(columns.stream().collect(Collectors.joining(", ")));
        sb.append(")");

        //
        for(int q = 0;q<columnid.size();q++){
            constrainContent.get(q).add(referencedTableName+"("+columns.stream().collect(Collectors.joining(", "))+")");
        }
        String cascade = "";
        if(Randomly.getBoolean()){
            addActionClause(" ON DELETE ");
            cascade = "DELETE";
        }else{
            addActionClause(" ON UPDATE ");
            cascade = "UPDATE";
        }
//        addActionClause(" ON DELETE ");
//        addActionClause(" ON UPDATE ");
//        if (Randomly.getBoolean()) {
//            // add a deferrable clause
//            sb.append(" ");
//            String deferrable = Randomly.fromOptions("DEFERRABLE INITIALLY DEFERRED",
//                    "NOT DEFERRABLE INITIALLY DEFERRED", "NOT DEFERRABLE INITIALLY IMMEDIATE", "NOT DEFERRABLE",
//                    "DEFERRABLE INITIALLY IMMEDIATE", "DEFERRABLE");
//            sb.append(deferrable);
//        }
        for(int q = 0;q<columnid.size();q++){
            constrainContent.get(q).add(referencedTableName+"("+columns.stream().collect(Collectors.joining(", "))+"|||||"+cascade);
        }
    }

    private void addActionClause(String string) {
        if (Randomly.getBoolean()) {
            // add an ON DELETE or ON ACTION clause
            sb.append(string);
            sb.append("CASCADE");
            //sb.append(Randomly.fromOptions("NO ACTION", "RESTRICT", "SET NULL", "SET DEFAULT", "CASCADE"));
        }
    }

}
