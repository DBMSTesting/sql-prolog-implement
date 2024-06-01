package sqlancer.duckdb.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sqlancer.Randomly;
import sqlancer.common.ast.newast.Node;
import sqlancer.common.gen.UntypedExpressionGenerator;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.duckdb.DuckDBErrors;
import sqlancer.duckdb.DuckDBProvider.DuckDBGlobalState;
import sqlancer.duckdb.DuckDBSchema.DuckDBColumn;
import sqlancer.duckdb.DuckDBSchema.DuckDBCompositeDataType;
import sqlancer.duckdb.DuckDBSchema.DuckDBDataType;
import sqlancer.duckdb.DuckDBToStringVisitor;
import sqlancer.duckdb.ast.DuckDBExpression;
import sqlancer.mysql.gen.Constrain;

public class DuckDBTableGenerator {
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

    public SQLQueryAdapter getQuery(DuckDBGlobalState globalState) {
        ExpectedErrors errors = new ExpectedErrors();
        StringBuilder sb = new StringBuilder();
        String tableName = globalState.getSchema().getFreeTableName();
        sb.append("CREATE TABLE ");
        sb.append(tableName);
        sbNoConstrain.append("CREATE TABLE ");
        sbNoConstrain.append(tableName);
        table = tableName;
        //sb.append("(u UNION");
        sb.append("(");
        sbNoConstrain.append("(");
        List<DuckDBColumn> columns = getNewColumns();
        UntypedExpressionGenerator<Node<DuckDBExpression>, DuckDBColumn> gen = new DuckDBExpressionGenerator(
                globalState).setColumns(columns);
        for (int i = 0; i < columns.size(); i++) {
            List<String> columnConstrain = new ArrayList<>();
            List<String> columnConstrainContent = new ArrayList<>();
            if (i != 0) {
                sb.append(", ");
                sbNoConstrain.append(", ");
            }
            sb.append(columns.get(i).getName());
            columnsList.add(columns.get(i).getName());
            sb.append(" ");
            sbNoConstrain.append(columns.get(i).getName());
            sbNoConstrain.append(" ");
            sb.append(columns.get(i).getType());
            columnConstrain.add(columns.get(i).getType().toString());
            columnConstrainContent.add("");
            if (globalState.getDbmsSpecificOptions().testCollate && Randomly.getBooleanWithRatherLowProbability()
                    && columns.get(i).getType().getPrimitiveDataType() == DuckDBDataType.VARCHAR) {
                sb.append(" COLLATE ");
                sb.append(getRandomCollate());
            }
            if (globalState.getDbmsSpecificOptions().testIndexes && Randomly.getBooleanWithRatherLowProbability()) {
                sb.append(" UNIQUE");
                columnConstrain.add("UNIQUE");
                columnConstrainContent.add("");
            }
            if (globalState.getDbmsSpecificOptions().testNotNullConstraints
                    && Randomly.getBooleanWithRatherLowProbability()) {
                sb.append(" NOT NULL");
                columnConstrain.add("NOT NULL");
                columnConstrainContent.add("");
            }
            if (globalState.getDbmsSpecificOptions().testCheckConstraints
                    && Randomly.getBooleanWithRatherLowProbability()) {
                String checkStr = DuckDBToStringVisitor.asString(gen.generateExpression(""));
                sb.append(" CHECK(");
                sb.append(checkStr);
                DuckDBErrors.addExpressionErrors(errors);
                sb.append(")");
                columnConstrain.add("CHECK");
                columnConstrainContent.add(checkStr);
            }
            if (Randomly.getBoolean() && globalState.getDbmsSpecificOptions().testDefaultValues) {
                String defaultStr = DuckDBToStringVisitor.asString(gen.generateConstant());
                sb.append(" DEFAULT(");
                sb.append(defaultStr);
                sb.append(")");
                columnConstrain.add("DEFAULT");
                columnConstrainContent.add(defaultStr);
            }
            constrainList.add(columnConstrain);
            constrainContent.add(columnConstrainContent);
        }
        if (globalState.getDbmsSpecificOptions().testIndexes && Randomly.getBoolean()) {
            errors.add("Invalid type for index");
            List<DuckDBColumn> primaryKeyColumns = Randomly.nonEmptySubset(columns);
            String pc = primaryKeyColumns.stream().map(c -> c.getName()).collect(Collectors.joining(", "));
            sb.append(", PRIMARY KEY(");
            sb.append(pc);
            sb.append(")");
            sbNoConstrain.append(", PRIMARY KEY(");
            sbNoConstrain.append(pc);
            sbNoConstrain.append(")");
        }
        sb.append(")");
        sbNoConstrain.append(")");
        //sb.append(")");
        return new SQLQueryAdapter(sb.toString(), errors, true);
    }

    public static String getRandomCollate() {
        return Randomly.fromOptions("NOCASE", "NOACCENT", "NOACCENT.NOCASE", "C", "POSIX");
    }

    private static List<DuckDBColumn> getNewColumns() {
        List<DuckDBColumn> columns = new ArrayList<>();
        for (int i = 0; i < Randomly.smallNumber() + 1; i++) {
            String columnName = String.format("c%d", i);
            DuckDBCompositeDataType columnType = DuckDBCompositeDataType.getRandomWithoutNull();
            columns.add(new DuckDBColumn(columnName, columnType, false, false));
        }
        return columns;
    }

}
