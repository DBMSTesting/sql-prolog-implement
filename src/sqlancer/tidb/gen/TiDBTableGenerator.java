package sqlancer.tidb.gen;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
//import java.util.stream.Collectors;

import sqlancer.IgnoreMeException;
import sqlancer.Randomly;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.mysql.gen.Constrain;
import sqlancer.tidb.TiDBExpressionGenerator;
import sqlancer.tidb.TiDBProvider.TiDBGlobalState;
import sqlancer.tidb.TiDBSchema.TiDBColumn;
import sqlancer.tidb.TiDBSchema.TiDBCompositeDataType;
import sqlancer.tidb.TiDBSchema.TiDBDataType;
import sqlancer.tidb.TiDBSchema.TiDBTable;
import sqlancer.tidb.visitor.TiDBVisitor;

public class TiDBTableGenerator {

    private boolean allowPrimaryKey;
    private final List<TiDBColumn> columns = new ArrayList<>();
    private boolean primaryKeyAsTableConstraints;
    private final ExpectedErrors errors = new ExpectedErrors();

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

    public static SQLQueryAdapter createRandomTableStatement(TiDBGlobalState globalState) throws SQLException {
        if (globalState.getSchema().getDatabaseTables().size() > globalState.getDbmsSpecificOptions().maxNumTables) {
            throw new IgnoreMeException();
        }
        return new TiDBTableGenerator().getQuery(globalState);
    }

    public SQLQueryAdapter getQuery(TiDBGlobalState globalState) throws SQLException {
        errors.add("Information schema is changed during the execution of the statement");
        String tableName = globalState.getSchema().getFreeTableName();
        int nrColumns = Randomly.smallNumber() + 1;
        allowPrimaryKey = Randomly.getBoolean();
        primaryKeyAsTableConstraints = allowPrimaryKey && Randomly.getBoolean();
        for (int i = 0; i < nrColumns; i++) {
            TiDBColumn fakeColumn = new TiDBColumn("c" + i, null, false, false, false);
            columns.add(fakeColumn);
        }
        TiDBExpressionGenerator gen = new TiDBExpressionGenerator(globalState).setColumns(columns);

        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(tableName);
        table = tableName;
        sbNoConstrain.append(tableName);

        if (Randomly.getBoolean() && globalState.getSchema().getDatabaseTables().size() > 0) {
            sb.append(" LIKE ");
            sbNoConstrain.append(" LIKE ");
            TiDBTable otherTable = globalState.getSchema().getRandomTable();
            sb.append(otherTable.getName());
            sbNoConstrain.append(otherTable.getName());
        } else {
            createNewTable(gen, sb);
        }
        return new SQLQueryAdapter(sb.toString(), errors, true);
    }

    private void createNewTable(TiDBExpressionGenerator gen, StringBuilder sb) {
        sb.append("(");
        sbNoConstrain.append("(");
        for (int i = 0; i < columns.size(); i++) {
            List<String> columnConstrain = new ArrayList<>();
            List<String> columnConstrainContent = new ArrayList<>();
            if (i != 0) {
                sb.append(", ");
                sbNoConstrain.append(", ");
            }
            sb.append(columns.get(i).getName());
            sb.append(" ");
            sbNoConstrain.append(columns.get(i).getName());
            sbNoConstrain.append(" ");
            columnsList.add(columns.get(i).getName());
            TiDBCompositeDataType type;
            type = TiDBCompositeDataType.getRandom();
            appendType(sb, type,columnConstrain,columnConstrainContent);
            sb.append(" ");
            sbNoConstrain.append(" ");
            boolean isGeneratedColumn = Randomly.getBooleanWithRatherLowProbability();
            if (isGeneratedColumn) {
                String o = TiDBVisitor.asString(gen.generateExpression(""));
                String p = Randomly.fromOptions("STORED", "VIRTUAL");
                sb.append(" AS (");
                sb.append(o);
                sb.append(") ");
                sb.append(p);
                sb.append(" ");
                sbNoConstrain.append(" AS (");
                sbNoConstrain.append(o);
                sbNoConstrain.append(") ");
                sbNoConstrain.append(p);
                sbNoConstrain.append(" ");
                errors.add("Generated column can refer only to generated columns defined prior to it");
                errors.add(
                        "'Defining a virtual generated column as primary key' is not supported for generated columns.");
                errors.add("contains a disallowed function.");
                errors.add("cannot refer to auto-increment column");
            }
            if (Randomly.getBooleanWithRatherLowProbability()) {
                String checkStr = TiDBVisitor.asString(gen.generateExpression(""));
                sb.append("CHECK (");
                sb.append(checkStr);
                sb.append(") ");
                columnConstrain.add("CHECK");
                columnConstrainContent.add(checkStr);
            }
            if (Randomly.getBooleanWithRatherLowProbability()) {
                sb.append("NOT NULL ");
                columnConstrain.add("NOT NULL");
                columnConstrainContent.add("");
            }
            if (Randomly.getBoolean() && type.getPrimitiveDataType().canHaveDefault() && !isGeneratedColumn) {
                sb.append("DEFAULT ");
                sb.append(TiDBVisitor.asString(gen.generateConstant(type.getPrimitiveDataType())));
                sb.append(" ");
                errors.add("Invalid default value");
                errors.add(
                        "All parts of a PRIMARY KEY must be NOT NULL; if you need NULL in a key, use UNIQUE instead");
            }
            if (type.getPrimitiveDataType() == TiDBDataType.INT && Randomly.getBooleanWithRatherLowProbability()
                    && !isGeneratedColumn) {
                sb.append(" AUTO_INCREMENT ");
                errors.add("there can be only one auto column and it must be defined as a key");
            }
            if (Randomly.getBooleanWithRatherLowProbability() && canUseAsUnique(type)) {
                sb.append("UNIQUE ");
                columnConstrain.add("UNIQUE");
                columnConstrainContent.add("");
            }
            if (Randomly.getBooleanWithRatherLowProbability() && allowPrimaryKey && !primaryKeyAsTableConstraints
                    && canUseAsUnique(type) && !isGeneratedColumn) {
                sb.append("PRIMARY KEY ");
                columnConstrain.add("PRIMARY KEY");
                columnConstrainContent.add("");
                allowPrimaryKey = false;
            }
            constrainList.add(columnConstrain);
            constrainContent.add(columnConstrainContent);
        }
//        if (primaryKeyAsTableConstraints) {
//            sb.append(", PRIMARY KEY(");
//            sb.append(
//                    Randomly.nonEmptySubset(columns).stream().map(c -> c.getName()).collect(Collectors.joining(", ")));
//            sb.append(")");
//            // TODO: do nto include blob/text columns here
//            errors.add(" used in key specification without a key length");
//        }
        sb.append(")");
        sb.append(" AUTO_ID_CACHE 100 ");
        sbNoConstrain.append(")");
        sbNoConstrain.append(" AUTO_ID_CACHE 100 ");
        if (Randomly.getBooleanWithRatherLowProbability()) {
            String r = TiDBVisitor.asString(gen.generateExpression(""));
            long u = Randomly.getNotCachedInteger(1, 100);
            sb.append("PARTITION BY HASH(");
            sb.append(r);
            sb.append(") ");
            sb.append("PARTITIONS ");
            sb.append(u);
            sbNoConstrain.append("PARTITION BY HASH(");
            sbNoConstrain.append(r);
            sbNoConstrain.append(") ");
            sbNoConstrain.append("PARTITIONS ");
            sbNoConstrain.append(u);
            errors.add(
                    "Constant, random or timezone-dependent expressions in (sub)partitioning function are not allowed");
            errors.add("This partition function is not allowed");
            errors.add("A PRIMARY KEY must include all columns in the table's partitioning function");
            errors.add("A UNIQUE INDEX must include all columns in the table's partitioning function");
            errors.add("is of a not allowed type for this type of partitioning");
            errors.add("The PARTITION function returns the wrong type");
        }

    }

    private boolean canUseAsUnique(TiDBCompositeDataType type) {
        return type.getPrimitiveDataType() != TiDBDataType.TEXT && type.getPrimitiveDataType() != TiDBDataType.BLOB;
    }

    private void appendType(StringBuilder sb, TiDBCompositeDataType type,List<String> columnConstrain,List<String> columnConstrainContent) {
        sb.append(type.toString());
        columnConstrain.add(type.toString());
        columnConstrainContent.add("");

        appendSpecifiers(sb, type.getPrimitiveDataType());
        appendSizeSpecifiers(sb, type.getPrimitiveDataType());
    }

    private void appendSizeSpecifiers(StringBuilder sb, TiDBDataType type) {
        if (type.isNumeric() && Randomly.getBoolean()) {
            sb.append(" UNSIGNED");
            sbNoConstrain.append(" UNSIGNED");
        }
        if (type.isNumeric() && Randomly.getBoolean()) {
            sb.append(" ZEROFILL");
            sbNoConstrain.append(" ZEROFILL");
        }
    }

    static void appendSpecifiers(StringBuilder sb, TiDBDataType type) {
        if (type == TiDBDataType.TEXT || type == TiDBDataType.BLOB) {
            long i = Randomly.getNotCachedInteger(1, 500);
            sb.append("(");
            sb.append(i);
            sb.append(")");
            sbNoConstrain.append("(");
            sbNoConstrain.append(i);
            sbNoConstrain.append(")");
        }
    }
}
