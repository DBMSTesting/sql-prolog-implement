package sqlancer.mysql.gen;

import java.util.*;
import java.util.stream.Collectors;

import sqlancer.IgnoreMeException;
import sqlancer.Randomly;
import sqlancer.common.DBMSCommon;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.mysql.MySQLBugs;
import sqlancer.mysql.MySQLGlobalState;
import sqlancer.mysql.MySQLSchema;
import sqlancer.mysql.MySQLSchema.MySQLDataType;
import sqlancer.mysql.MySQLSchema.MySQLTable.MySQLEngine;
import sqlancer.mysql.MySQLVisitor;
import sqlancer.mysql.ast.MySQLExpression;

public class MySQLTableGenerator {

    private final StringBuilder sb = new StringBuilder();
    private final boolean allowPrimaryKey;
    private boolean setPrimaryKey;
    private final String tableName;
    private final Randomly r;
    private int columnId;
    private boolean tableHasNullableColumn;
    private MySQLEngine engine;
    private int keysSpecified;
    private final List<String> columns = new ArrayList<>();
    private final MySQLSchema schema;
    private final MySQLGlobalState globalState;

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
    public MySQLTableGenerator(MySQLGlobalState globalState, String tableName) {
        this.tableName = tableName;
        this.r = globalState.getRandomly();
        this.schema = globalState.getSchema();
        allowPrimaryKey = Randomly.getBoolean();
        this.globalState = globalState;
    }

    public static SQLQueryAdapter generate(MySQLGlobalState globalState, String tableName) {
        return new MySQLTableGenerator(globalState, tableName).create();
    }

    private SQLQueryAdapter create() {
        ExpectedErrors errors = new ExpectedErrors();


        sb.append("CREATE");
        sbNoConstrain.append("CREATE");
        // TODO support temporary tables in the schema
        sb.append(" TABLE");
        sbNoConstrain.append(" TABLE");
        if (Randomly.getBoolean()) {
            sb.append(" IF NOT EXISTS");
            sbNoConstrain.append(" IF NOT EXISTS");
        }
        sb.append(" ");
        sb.append(tableName);
        sbNoConstrain.append(" ");
        sbNoConstrain.append(tableName);
        table = tableName;
        if (Randomly.getBoolean() && !schema.getDatabaseTables().isEmpty()) {
            sb.append(" LIKE ");
            String str = schema.getRandomTable().getName();
            sb.append(str);
            sbNoConstrain.append(" LIKE ");
            sbNoConstrain.append(str);
            return new SQLQueryAdapter(sb.toString(), true);
        } else {
            sb.append("(");
            sbNoConstrain.append("(");
            for (int i = 0; i < 1 + Randomly.smallNumber(); i++) {
                if (i != 0) {
                    sb.append(", ");
                    sbNoConstrain.append(", ");
                }
                appendColumn();
            }
            sb.append(")");
            sb.append(" ");
            sbNoConstrain.append(")");
            sbNoConstrain.append(" ");
            appendTableOptions();
            appendPartitionOptions();
            if ((tableHasNullableColumn || setPrimaryKey) && engine == MySQLEngine.CSV) {
                if (true) { // TODO
                    // results in an error
                    throw new IgnoreMeException();
                }
            } else if ((tableHasNullableColumn || keysSpecified > 1) && engine == MySQLEngine.ARCHIVE) {
                errors.add("Too many keys specified; max 1 keys allowed");
                errors.add("Table handler doesn't support NULL in given index");
                addCommonErrors(errors);
                return new SQLQueryAdapter(sb.toString(), errors, true);
            }
            addCommonErrors(errors);
            return new SQLQueryAdapter(sb.toString(), errors, true);
        }

    }

    private void addCommonErrors(ExpectedErrors list) {
        list.add("The storage engine for the table doesn't support");
        list.add("doesn't have this option");
        list.add("must include all columns");
        list.add("not allowed type for this type of partitioning");
        list.add("doesn't support BLOB/TEXT columns");
        list.add("A BLOB field is not allowed in partition function");
        list.add("Too many keys specified; max 1 keys allowed");
        list.add("The total length of the partitioning fields is too large");
        list.add("Got error -1 - 'Unknown error -1' from storage engine");
    }

    private enum PartitionOptions {
        HASH, KEY
    }

    private void appendPartitionOptions() {
        if (engine != MySQLEngine.INNO_DB) {
            return;
        }
        if (Randomly.getBoolean()) {
            return;
        }
        sb.append(" PARTITION BY");
        sbNoConstrain.append(" PARTITION BY");
        switch (Randomly.fromOptions(PartitionOptions.values())) {
        case HASH:
            if (Randomly.getBoolean()) {
                sb.append(" LINEAR");
                sbNoConstrain.append(" LINEAR");
            }
            sb.append(" HASH(");
            sbNoConstrain.append(" HASH(");
            // TODO: consider arbitrary expressions
            // MySQLExpression expr =
            // MySQLRandomExpressionGenerator.generateRandomExpression(Collections.emptyList(),
            // null, r);
            // sb.append(MySQLVisitor.asString(expr));
            String str = Randomly.fromList(columns);
            sb.append(str);
            sb.append(")");
            sbNoConstrain.append(str);
            sbNoConstrain.append(")");
            break;
        case KEY:
            if (Randomly.getBoolean()) {
                sb.append(" LINEAR");
                sbNoConstrain.append(" LINEAR");
            }
            sb.append(" KEY");
            sbNoConstrain.append(" KEY");
            if (Randomly.getBoolean()) {
                int str1=Randomly.fromOptions(1, 2);
                sb.append(" ALGORITHM=");
                sb.append(str1);
                sbNoConstrain.append(" ALGORITHM=");
                sbNoConstrain.append(str1);
            }
            String str2 =  Randomly.nonEmptySubset(columns).stream().collect(Collectors.joining(", "));
            sb.append(" (");
            sb.append(str2);
            sb.append(")");
            sbNoConstrain.append(" (");
            sbNoConstrain.append(str2);
            sbNoConstrain.append(")");
            break;
        default:
            throw new AssertionError();
        }
    }

    private enum TableOptions {
        AUTO_INCREMENT, AVG_ROW_LENGTH, CHECKSUM, COMPRESSION, DELAY_KEY_WRITE, /* ENCRYPTION, */ ENGINE, INSERT_METHOD,
        KEY_BLOCK_SIZE, MAX_ROWS, MIN_ROWS, PACK_KEYS, STATS_AUTO_RECALC, STATS_PERSISTENT, STATS_SAMPLE_PAGES;

        public static List<TableOptions> getRandomTableOptions() {
            List<TableOptions> options;
            // try to ensure that usually, only a few of these options are generated
            if (Randomly.getBooleanWithSmallProbability()) {
                options = Randomly.subset(TableOptions.values());
            } else {
                if (Randomly.getBoolean()) {
                    options = Collections.emptyList();
                } else {
                    options = Randomly.nonEmptySubset(Arrays.asList(TableOptions.values()), Randomly.smallNumber());
                }
            }
            return options;
        }
    }

    private void appendTableOptions() {
        List<TableOptions> tableOptions = TableOptions.getRandomTableOptions();
        int i = 0;
        for (TableOptions o : tableOptions) {
            if (i++ != 0) {
                sb.append(", ");
            }
            switch (o) {
            case AUTO_INCREMENT:
                long str = r.getPositiveInteger();
                sb.append("AUTO_INCREMENT = ");
                sb.append(str);
                sbNoConstrain.append("AUTO_INCREMENT = ");
                sbNoConstrain.append(str);
                break;
            case AVG_ROW_LENGTH:
                //long str1 = r.getPositiveInteger();
                Random random1 = new Random();
                long str1 = random1.nextLong() & 0xFFFFFFFFL;
                sb.append("AVG_ROW_LENGTH = ");
                sb.append(str1);
                sbNoConstrain.append("AVG_ROW_LENGTH = ");
                sbNoConstrain.append(str1);
                break;
            case CHECKSUM:
                sb.append("CHECKSUM = 1");
                sbNoConstrain.append("CHECKSUM = 1");
                break;
            case COMPRESSION:
                String str2 = Randomly.fromOptions("ZLIB", "LZ4", "NONE");
                sb.append("COMPRESSION = '");
                sb.append(str2);
                sb.append("'");
                sbNoConstrain.append("COMPRESSION = '");
                sbNoConstrain.append(str2);
                sbNoConstrain.append("'");
                break;
            case DELAY_KEY_WRITE:
                int str3 = Randomly.fromOptions(0, 1);
                sb.append("DELAY_KEY_WRITE = ");
                sb.append(str3);
                sbNoConstrain.append("DELAY_KEY_WRITE = ");
                sbNoConstrain.append(str3);
                break;
            case ENGINE:
                // FEDERATED: java.sql.SQLSyntaxErrorException: Unknown storage engine
                // 'FEDERATED'
                // "NDB": java.sql.SQLSyntaxErrorException: Unknown storage engine 'NDB'
                // "EXAMPLE": java.sql.SQLSyntaxErrorException: Unknown storage engine 'EXAMPLE'
                // "MERGE": java.sql.SQLException: Table 't0' is read only
                String fromOptions = Randomly.fromOptions("InnoDB", "MyISAM", "MEMORY", "HEAP", "CSV", "ARCHIVE");
                this.engine = MySQLEngine.get(fromOptions);
                sb.append("ENGINE = ");
                sb.append(fromOptions);
                sbNoConstrain.append("ENGINE = ");
                sbNoConstrain.append(fromOptions);
                break;
            // case ENCRYPTION:
            // sb.append("ENCRYPTION = '");
            // sb.append(Randomly.fromOptions("Y", "N"));
            // sb.append("'");
            // break;
            case INSERT_METHOD:
                String str4 = Randomly.fromOptions("NO", "FIRST", "LAST");
                sb.append("INSERT_METHOD = ");
                sb.append(str4);
                sbNoConstrain.append("INSERT_METHOD = ");
                sbNoConstrain.append(str4);
                break;
            case KEY_BLOCK_SIZE:
                //long str5 = r.getPositiveInteger();
                Random random = new Random();
                long str5 = random.nextLong() & 0xFFFF;
                sb.append("KEY_BLOCK_SIZE = ");
                sb.append(str5);
                sbNoConstrain.append("KEY_BLOCK_SIZE = ");
                sbNoConstrain.append(str5);
                break;
            case MAX_ROWS:
                long str6 = r.getLong(0, Long.MAX_VALUE);
                sb.append("MAX_ROWS = ");
                sb.append(str6);
                sbNoConstrain.append("MAX_ROWS = ");
                sbNoConstrain.append(str6);
                break;
            case MIN_ROWS:
                long str7 = r.getLong(1, Long.MAX_VALUE);
                sb.append("MIN_ROWS = ");
                sb.append(str7);
                sbNoConstrain.append("MIN_ROWS = ");
                sbNoConstrain.append(str7);
                break;
            case PACK_KEYS:
                String str8 = Randomly.fromOptions("1", "0", "DEFAULT");
                sb.append("PACK_KEYS = ");
                sb.append(str8);
                sbNoConstrain.append("PACK_KEYS = ");
                sbNoConstrain.append(str8);
                break;
            case STATS_AUTO_RECALC:
                String str9 = Randomly.fromOptions("1", "0", "DEFAULT");
                sb.append("STATS_AUTO_RECALC = ");
                sb.append(str9);
                sbNoConstrain.append("STATS_AUTO_RECALC = ");
                sbNoConstrain.append(str9);
                break;
            case STATS_PERSISTENT:
                String str10 = Randomly.fromOptions("1", "0", "DEFAULT");
                sb.append("STATS_PERSISTENT = ");
                sb.append(str10);
                sbNoConstrain.append("STATS_PERSISTENT = ");
                sbNoConstrain.append(str10);
                break;
            case STATS_SAMPLE_PAGES:
                int str11 = r.getInteger(1, Short.MAX_VALUE);
                sb.append("STATS_SAMPLE_PAGES = ");
                sb.append(str11);
                sbNoConstrain.append("STATS_SAMPLE_PAGES = ");
                sbNoConstrain.append(str11);
                break;
            default:
                throw new AssertionError(o);
            }
        }
    }

    private void appendColumn() {
        String columnName = DBMSCommon.createColumnName(columnId);
        //System.out.println("hhhhhhhhhhhh"+columnName);
        columns.add(columnName);
        columnsList.add(columnName);
        sb.append(columnName);
        sbNoConstrain.append(columnName);
        List<String> constrainOfConlumn = new ArrayList<>();
        List<String> constrainContentOfConlumn = new ArrayList<>();
        appendColumnDefinition(constrainOfConlumn,constrainContentOfConlumn,columnName);
        constrainList.add(constrainOfConlumn);
        constrainContent.add(constrainContentOfConlumn);
        columnId++;
    }

    private enum ColumnOptions {
        NULL_OR_NOT_NULL, UNIQUE, COMMENT, COLUMN_FORMAT, STORAGE, PRIMARY_KEY,CHECK,DEFAULT
    }

    private void appendColumnDefinition(List<String> constrainOfConlumn,List<String> constrainContentOfConlumn,String columnName) {
        sb.append(" ");
        sbNoConstrain.append(" ");
        MySQLDataType randomType = MySQLDataType.getRandom(globalState);
        boolean isTextType = randomType == MySQLDataType.VARCHAR;
        appendTypeString(randomType,constrainOfConlumn,constrainContentOfConlumn);
        sb.append(" ");
        sbNoConstrain.append(" ");
        boolean isNull = false;
        boolean columnHasPrimaryKey = false;

        List<ColumnOptions> columnOptions = Randomly.subset(ColumnOptions.values());
        if (!columnOptions.contains(ColumnOptions.NULL_OR_NOT_NULL)) {
            tableHasNullableColumn = true;
        }
        if (isTextType) {
            // TODO: restriction due to the limited key length
            columnOptions.remove(ColumnOptions.PRIMARY_KEY);
            columnOptions.remove(ColumnOptions.UNIQUE);
        }
        for (ColumnOptions o : columnOptions) {
            sb.append(" ");
            sbNoConstrain.append(" ");


            switch (o) {
            case NULL_OR_NOT_NULL:
                // PRIMARY KEYs cannot be NULL
                if (!columnHasPrimaryKey) {
                    if (Randomly.getBoolean()) {
                        sb.append("NULL");
                        constrainOfConlumn.add("NULL");
                        constrainContentOfConlumn.add(null);
                    }
                    tableHasNullableColumn = true;
                    isNull = true;
                } else {
                    sb.append("NOT NULL");
                    constrainOfConlumn.add("NOT NULL");
                    constrainContentOfConlumn.add(null);
                }
                break;
            case UNIQUE:
                sb.append("UNIQUE");
                constrainOfConlumn.add("UNIQUE");
                constrainContentOfConlumn.add(null);

                keysSpecified++;
                if (Randomly.getBoolean()) {
                    sb.append(" KEY");
                }
                break;
            case COMMENT:
                // TODO: generate randomly
                sb.append(String.format("COMMENT '%s' ", "asdf"));
                sbNoConstrain.append(String.format("COMMENT '%s' ", "asdf"));
                break;
            case COLUMN_FORMAT:
                String str5=Randomly.fromOptions("FIXED", "DYNAMIC", "DEFAULT");
                sb.append("COLUMN_FORMAT ");
                sb.append(str5);
                sbNoConstrain.append("COLUMN_FORMAT ");
                sbNoConstrain.append(str5);
                break;
            case STORAGE:
                String str6 = Randomly.fromOptions("DISK", "MEMORY");
                sb.append("STORAGE ");
                sb.append(str6);
                sbNoConstrain.append("STORAGE ");
                sbNoConstrain.append(str6);
                break;
            case PRIMARY_KEY:
                // PRIMARY KEYs cannot be NULL
                if (allowPrimaryKey && !setPrimaryKey && !isNull) {
                    sb.append("PRIMARY KEY");
                    constrainOfConlumn.add("PRIMARY KEY");
                    setPrimaryKey = true;
                    columnHasPrimaryKey = true;
                }
                break;
            case CHECK:
                sb.append("CHECK");
                //加入check条件

                MySQLSchema.MySQLColumn c = new MySQLSchema.MySQLColumn(columnName, MySQLDataType.fromValue(columnDataType), false, 1);
                List<MySQLSchema.MySQLColumn> columns = new ArrayList<MySQLSchema.MySQLColumn>();
                columns.add(c);
                //System.out.println("check列："+columns);
                MySQLExpressionGenerator joinGen = new MySQLExpressionGenerator(globalState).setColumns(columns);
                MySQLExpression mySQLExpression = joinGen.generateExpression("NO SUBQUERY");
                String expression = MySQLVisitor.subQueryAsString(mySQLExpression);
                //sbNoConstrain.append("CHECK");
                constrainOfConlumn.add("CHECK");
                constrainContentOfConlumn.add(expression);
                sb.append("("+expression+") ");
                break;
            case DEFAULT:
                if(columnDataType.contains("TEXT")){
                    break;
                }else{
                    sb.append("DEFAULT");
                    //加入默认值

                    String columnDefault = getDefault(columnDataType);

                    constrainOfConlumn.add("DEFAULT");
                    constrainContentOfConlumn.add(columnDefault);
                    sb.append(" "+columnDefault+" ");
                    break;
                }

            default:
                throw new AssertionError();
            }
        }

    }

    private String getDefault(String columnDataType){
        switch (columnDataType){
            case "INT":
                return Randomly.getIntegerType();
                //break;
            case "VARCHAR":
                return Randomly.getText();
                //break;
            case "FLOAT":
                Random random = new Random();
                float randomFloat = random.nextFloat();
                return Float.toString(randomFloat);
                //return Randomly.getDoublePrecision();
                //break;
            case "DOUBLE":
                return Randomly.getDoublePrecision();
                //break;
            case "DECIMAL":
                return Randomly.getDoublePrecision();
                //break;

            case "TINYINT":
                Random random1 = new Random();
                byte randomTinyInt = (byte) random1.nextInt(256);
                return String.valueOf(randomTinyInt-128);
                //return Randomly.getSmallint();
                //break;
            case "SMALLINT":
                Random random2 = new Random();
                short randomSmallInt = (short) random2.nextInt();
                return String.valueOf(randomSmallInt);
                //return Randomly.getSmallint();
                //break;
            case "MEDIUMINT":
                Random random3 = new Random();
                int randomMediumInt = random3.nextInt() & 0xFFFFFF;
                return String.valueOf(randomMediumInt-0x800000);
                //return Randomly.getIntegerType();
                //break;
            case "BIGINT":
                Random random4 = new Random();
                long randomBigInt = random4.nextLong();
                return String.valueOf(randomBigInt);
                //return Randomly.getBigint();
                //break;
            case "TINYTEXT":
                return Randomly.getText();
                //break;
            case "TEXT":
                return Randomly.getText();
                //break;
            case "MEDIUMTEXT":
                return Randomly.getText();
                //break;
            case "LONGTEXT":
                return Randomly.getText();
                //break;
//            case "UNSIGNED":break;
//            case "ZEROFILL":break;
            default:
                throw new AssertionError();
        }
    }

    private void appendTypeString(MySQLDataType randomType,List<String>constrainType,List<String> content) {
        sbNoConstrain.append("TEXT");
        StringBuilder typeContent = new StringBuilder("");
        switch (randomType) {
        case DECIMAL:
            sb.append("DECIMAL");
            columnDataType = "DECIMAL";
            constrainType.add("DECIMAL");
            //sbNoConstrain.append("DECIMAL");
            //optionallyAddPrecisionAndScale(sb,typeContent);
            break;
        case INT:
            String str = Randomly.fromOptions("TINYINT", "SMALLINT", "MEDIUMINT", "INT", "BIGINT");
            sb.append(str);
            columnDataType = str;
            constrainType.add(str);
            //sbNoConstrain.append(str);
//            if (Randomly.getBoolean()) {
//                long str1 = Randomly.getNotCachedInteger(0, 255);
//                sb.append("(");
//                sb.append(str1); // Display width out of range for column 'c0' (max =
//                                                                 // 255)
//                sb.append(")");
//                typeContent.append("("+str1+")");
//                //sbNoConstrain.append("(");
//                //sbNoConstrain.append(str1); // Display width out of range for column 'c0' (max =
//                // 255)
//                //sbNoConstrain.append(")");
//            }
            break;
        case VARCHAR:
            String str2 = Randomly.fromOptions("VARCHAR(500)", "TINYTEXT", "TEXT", "MEDIUMTEXT", "LONGTEXT");
            sb.append(str2);
            if(str2.equals("VARCHAR(500)")){
                constrainType.add("VARCHAR");
                typeContent.append("500");
                columnDataType = "VARCHAR";
            }else{
                constrainType.add(str2);
                columnDataType = str2;
            }
            //sbNoConstrain.append(str2);
            break;
        case FLOAT:
            sb.append("FLOAT");
            columnDataType = "FLOAT";
            //sbNoConstrain.append("FLOAT");
            constrainType.add("FLOAT");
            //optionallyAddPrecisionAndScale(sb,typeContent);
            break;
        case DOUBLE:
            String str3 = Randomly.fromOptions("DOUBLE", "FLOAT");
            sb.append(str3);
            //sbNoConstrain.append(str3);
            constrainType.add(str3);
            columnDataType = str3;
            //optionallyAddPrecisionAndScale(sb,typeContent);
            break;
        default:
            throw new AssertionError();
        }
        if (randomType.isNumeric()) {
            if (Randomly.getBoolean() && randomType != MySQLDataType.INT && !MySQLBugs.bug99127) {
                sb.append(" UNSIGNED");
//                constrainType.add("UNSIGNED");
//                columnDataType = "UNSIGNED";
                sbNoConstrain.append(" UNSIGNED");
            }
            if (!globalState.usesPQS() && Randomly.getBoolean()) {
                sb.append(" ZEROFILL");
//                constrainType.add("ZEROFILL");
//                columnDataType = "ZEROFILL";
                sbNoConstrain.append(" ZEROFILL");
            }
        }
        content.add(typeContent.toString());
    }

    public static void optionallyAddPrecisionAndScale(StringBuilder sb,StringBuilder typeContent) {
        if (Randomly.getBoolean() && !MySQLBugs.bug99183) {
            sb.append("(");
            typeContent.append("(");
            // The maximum number of digits (M) for DECIMAL is 65
            long m = Randomly.getNotCachedInteger(1, 65);
            sb.append(m);
            sb.append(", ");
            typeContent.append(m);
            typeContent.append(", ");
            // The maximum number of supported decimals (D) is 30
            long nCandidate = Randomly.getNotCachedInteger(1, 30);
            // For float(M,D), double(M,D) or decimal(M,D), M must be >= D (column 'c0').
            long n = Math.min(nCandidate, m);
            sb.append(n);
            sb.append(")");
            typeContent.append(n);
            typeContent.append(")");
        }
    }

}
