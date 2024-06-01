package sqlancer;

import java.util.ArrayList;
import java.util.List;

import sqlancer.common.query.Query;
import sqlancer.duckdb.gen.DuckDBTableGenerator;
import sqlancer.mysql.gen.Constrain;
import sqlancer.mysql.gen.MySQLTableGenerator;
import sqlancer.sqlite3.gen.ddl.SQLite3TableGenerator;
import sqlancer.tidb.gen.TiDBTableGenerator;

public class StatementExecutor<G extends GlobalState<?, ?, ?>, A extends AbstractAction<G>> {

    private final G globalState;
    private final A[] actions;
    private final ActionMapper<G, A> mapping;
    private final AfterQueryAction queryConsumer;
    public List<Query<SQLConnection>> queryList = new ArrayList<>();
    public List<Constrain> constrainList = new ArrayList<>();

    @FunctionalInterface
    public interface AfterQueryAction {
        void notify(Query<?> q) throws Exception;
    }

    @FunctionalInterface
    public interface ActionMapper<T, A> {
        int map(T globalState, A action);
    }

    public StatementExecutor(G globalState, A[] actions, ActionMapper<G, A> mapping, AfterQueryAction queryConsumer) {
        this.globalState = globalState;
        this.actions = actions.clone();
        this.mapping = mapping;
        this.queryConsumer = queryConsumer;
    }

    @SuppressWarnings("unchecked")
    public void executeStatements() throws Exception {
        Randomly r = globalState.getRandomly();
        int[] nrRemaining = new int[actions.length];
        List<A> availableActions = new ArrayList<>();
        int total = 0;
        for (int i = 0; i < actions.length; i++) {
            A action = actions[i];
            int nrPerformed = mapping.map(globalState, action);
            if (nrPerformed != 0) {
                availableActions.add(action);
            }
            nrRemaining[i] = nrPerformed;
            total += nrPerformed;
        }
        while (total != 0) {
            A nextAction = null;
            int selection = r.getInteger(0, total);
            int previousRange = 0;
            int i;
            for (i = 0; i < nrRemaining.length; i++) {
                if (previousRange <= selection && selection < previousRange + nrRemaining[i]) {
                    nextAction = actions[i];
                    break;
                } else {
                    previousRange += nrRemaining[i];
                }
            }
            assert nextAction != null;
            assert nrRemaining[i] > 0;
            nrRemaining[i]--;
            @SuppressWarnings("rawtypes")
            Query query = null;
            try {
                boolean success;
                int nrTries = 0;
                do {
                    //System.out.println(nextAction.toString());
                    if(nextAction.toString().equals("CREATE_TABLE")){
                        query = nextAction.getQuery(globalState);
                        //System.out.println("验证测试用例："+query.getQueryString());
                        queryList.add(query);
                        success = globalState.executeStatement(query);
                        //根据不同数据库调整
                        if(globalState.getClass().getSimpleName().equals("MySQLGlobalState")){
                            constrainList.add(MySQLTableGenerator.getConstrain());
                        }else if(globalState.getClass().getSimpleName().equals("SQLite3GlobalState")){
                            constrainList.add(SQLite3TableGenerator.getConstrain());
                        }else if(globalState.getClass().getSimpleName().equals("TiDBGlobalState")){
                            constrainList.add(TiDBTableGenerator.getConstrain());
                        }else if(globalState.getClass().getSimpleName().equals("DuckDBGlobalState")){
                            constrainList.add(DuckDBTableGenerator.getConstrain());
                        }
                        //constrainList.add(MySQLTableGenerator.getConstrain());
                    }else{
                        query = nextAction.getQuery(globalState);
                        queryList.add(query);
                        success = globalState.executeStatement(query);
                    }
//                    query = nextAction.getQuery(globalState);
//                    queryList.add(query);
//                    success = globalState.executeStatement(query);
                } while (nextAction.canBeRetried() && !success
                        && nrTries++ < globalState.getOptions().getNrStatementRetryCount());
            } catch (IgnoreMeException e) {

            }
            if (query != null && query.couldAffectSchema()) {
                globalState.updateSchema();
                queryConsumer.notify(query);
            }
            total--;
        }
    }
}
