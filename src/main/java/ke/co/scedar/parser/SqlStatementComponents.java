package ke.co.scedar.parser;

import gudusoft.gsqlparser.TSyntaxError;
import ke.co.scedar.db.QueryResultsManager;
import ke.co.scedar.db.fragment_schema.ComponentMetaData;
import ke.co.scedar.utils.JoinInfo;
import ke.co.scedar.utils.SqlStatementType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SqlStatementComponents {

    private String originalQuery;
    private SqlStatementType sqlStatementType;
    private List<String> columnsToProject = new LinkedList<>();
    private List<String> columnAliases = new LinkedList<>();
    private String tableName;
    private String tableAlias;
    private String tableFullName;
    private List<JoinInfo> joinsInfo = new LinkedList<>();
    private List<String> whereClauseColumns = new LinkedList<>();
    private List<String> whereClauseOperators = new LinkedList<>();
    private List<String> whereClauseValues = new LinkedList<>();
    private List<String> whereClauseTokenBeforeExpression = new LinkedList<>();
    private String whereConditionString;
    private boolean hasErrors = false;
    private String errorMessage;
    private int errorCount = 0;
    private List<TSyntaxError> syntaxErrors = new ArrayList<>();
    private ComponentMetaData componentMetaData;
    private String fragmentQuery;
    private QueryResultsManager queryResultsManager;

    public String getOriginalQuery() {
        return originalQuery;
    }

    public void setOriginalQuery(String originalQuery) {
        this.originalQuery = originalQuery;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public List<TSyntaxError> getSyntaxErrors() {
        return syntaxErrors;
    }

    public void setSyntaxErrors(List<TSyntaxError> syntaxErrors) {
        this.syntaxErrors = syntaxErrors;
    }

    public SqlStatementType getSqlStatementType() {
        return sqlStatementType;
    }

    public List<String> getColumnsToProject() {
        return columnsToProject;
    }

    public List<String> getColumnAliases() {
        return columnAliases;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public String getTableFullName() {
        return tableFullName;
    }

    public List<JoinInfo> getJoinsInfo() {
        return joinsInfo;
    }

    public List<String> getWhereClauseColumns() {
        return whereClauseColumns;
    }

    public List<String> getWhereClauseOperators() {
        return whereClauseOperators;
    }

    public List<String> getWhereClauseValues() {
        return whereClauseValues;
    }

    public List<String> getWhereClauseTokenBeforeExpression() {
        return whereClauseTokenBeforeExpression;
    }

    public String getWhereConditionString() {
        return whereConditionString;
    }

    public void addProjectionColumn(String column, String alias){
        columnsToProject.add(column);
        columnAliases.add((alias != null) ? alias : "NO_ALIAS");
    }

    public void addJoinInfo(JoinInfo joinInfo) {
        joinsInfo.add(joinInfo );
    }

    public void setSqlStatementType(SqlStatementType sqlStatementType) {
        this.sqlStatementType = sqlStatementType;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public void setTableFullName(String tableFullName) {
        this.tableFullName = tableFullName;
    }

    public void setWhereClauseColumns(List<String> whereClauseColumns) {
        this.whereClauseColumns = whereClauseColumns;
    }

    public void setWhereClauseOperators(List<String> whereClauseOperators) {
        this.whereClauseOperators = whereClauseOperators;
    }

    public void setWhereClauseValues(List<String> whereClauseValues) {
        this.whereClauseValues = whereClauseValues;
    }

    public void setWhereClauseTokenBeforeExpression(List<String> whereClauseTokenBeforeExpression) {
        this.whereClauseTokenBeforeExpression = whereClauseTokenBeforeExpression;
    }

    public void setWhereConditionString(String whereConditionString) {
        this.whereConditionString = whereConditionString;
    }

    public ComponentMetaData getComponentMetaData() {
        return componentMetaData;
    }

    public void setComponentMetaData(ComponentMetaData componentMetaData) {
        this.componentMetaData = componentMetaData;
    }

    public String getFragmentQuery() {
        return fragmentQuery;
    }

    public void setFragmentQuery(String fragmentQuery) {
        this.fragmentQuery = fragmentQuery;
    }

    public QueryResultsManager getQueryResultsManager() {
        return queryResultsManager;
    }

    public void setQueryResultsManager(QueryResultsManager queryResultsManager) {
        this.queryResultsManager = queryResultsManager;
    }

    @Override
    public String toString() {
        return "parser.SqlStatementComponents {" +
                "\n\tsqlStatementType=" + sqlStatementType +
                "\n\tcolumnsToProject=" + columnsToProject +
                ", \n\tcolumnAliases=" + columnAliases +
                ", \n\ttableName='" + tableName + '\'' +
                ", \n\ttableAlias='" + tableAlias + '\'' +
                ", \n\ttableFullName='" + tableFullName + '\'' +
                ", \n\tjoinsInfo=" + joinsInfo +
                ", \n\twhereClauseColumns=" + whereClauseColumns +
                ", \n\twhereClauseOperators=" + whereClauseOperators +
                ", \n\twhereClauseValues=" + whereClauseValues +
                ", \n\twhereClauseTokenBeforeExpression=" + whereClauseTokenBeforeExpression +
                ", \n\tcomponentMetaData=" + componentMetaData +
                ", \n\tfragmentQuery=" + fragmentQuery +
                ", \n\thasErrors=" + hasErrors +
                ", \n\terrorMessage=" + errorMessage +
                ", \n\terrorCount=" + errorCount +
                "\n}";
    }
}
