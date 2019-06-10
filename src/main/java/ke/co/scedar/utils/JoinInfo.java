package ke.co.scedar.utils;

import java.util.List;

public class JoinInfo {
    private String joinType;
    private String joinTableName;
    private String joinTableAlias;
    private String joinTableFullName;
    private String joinConditionString;
    private List<String> joinColumns;
    private List<String> joinOperators;
    private List<String> joinValues;
    private List<String> joinTokenBeforeExpression;

    public JoinInfo(String joinType, String joinTableName, String joinTableAlias, String joinTableFullName) {
        this.joinType = joinType;
        this.joinTableName = joinTableName;
        this.joinTableAlias = joinTableAlias;
        this.joinTableFullName = joinTableFullName;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getJoinTableName() {
        return joinTableName;
    }

    public void setJoinTableName(String joinTableName) {
        this.joinTableName = joinTableName;
    }

    public String getJoinTableAlias() {
        return joinTableAlias;
    }

    public void setJoinTableAlias(String joinTableAlias) {
        this.joinTableAlias = joinTableAlias;
    }

    public String getJoinTableFullName() {
        return joinTableFullName;
    }

    public void setJoinTableFullName(String joinTableFullName) {
        this.joinTableFullName = joinTableFullName;
    }

    public String getJoinConditionString() {
        return joinConditionString;
    }

    public void setJoinConditionString(String joinConditionString) {
        this.joinConditionString = joinConditionString;
    }

    public List<String> getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(List<String> joinColumns) {
        this.joinColumns = joinColumns;
    }

    public List<String> getJoinOperators() {
        return joinOperators;
    }

    public void setJoinOperators(List<String> joinOperators) {
        this.joinOperators = joinOperators;
    }

    public List<String> getJoinValues() {
        return joinValues;
    }

    public void setJoinValues(List<String> joinValues) {
        this.joinValues = joinValues;
    }

    public List<String> getJoinTokenBeforeExpression() {
        return joinTokenBeforeExpression;
    }

    public void setJoinTokenBeforeExpression(List<String> joinTokenBeforeExpression) {
        this.joinTokenBeforeExpression = joinTokenBeforeExpression;
    }

    @Override
    public String toString() {
        return "\n\tJoinInfo{" +
                "\n\t\tjoinType='" + joinType + '\'' +
                ", \n\t\tjoinTableName='" + joinTableName + '\'' +
                ", \n\t\tjoinTableAlias='" + joinTableAlias + '\'' +
                ", \n\t\tjoinTableFullName='" + joinTableFullName + '\'' +
                ", \n\t\tjoinConditionString='" + joinConditionString + '\'' +
                ", \n\t\tjoinColumns=" + joinColumns +
                ", \n\t\tjoinOperators=" + joinOperators +
                ", \n\t\tjoinValues=" + joinValues +
                ", \n\t\tjoinTokenBeforeExpression=" + joinTokenBeforeExpression +
                "\n\t}";
    }
}