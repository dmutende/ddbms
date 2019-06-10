package ke.co.scedar.utils;

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParseTreeNode;

import java.util.LinkedList;
import java.util.List;

public class ExpressionEvaluator implements IExpressionVisitor {

    private TExpression condition;
    private List<String> columns = new LinkedList<>();
    private List<String> operators = new LinkedList<>();
    private List<String> values = new LinkedList<>();
    private List<String> tokenBeforeExpression = new LinkedList<>();

    public ExpressionEvaluator(TExpression expr) {
        this.condition = expr;
        this.condition.inOrderTraverse(this);
    }

    boolean isCompareCondition(EExpressionType t) {
        return ((t == EExpressionType.simple_comparison_t)
                || (t == EExpressionType.group_comparison_t) || (t == EExpressionType.in_t));
    }

    public boolean exprVisit(TParseTreeNode pnode, boolean pIsLeafNode) {
        TExpression lcexpr = (TExpression) pnode;
        if (isCompareCondition(lcexpr.getExpressionType())) {
            TExpression leftExpr = lcexpr.getLeftOperand();

            columns.add(leftExpr.toString());

            if (lcexpr.getComparisonOperator() != null) {
                operators.add(lcexpr.getComparisonOperator().astext);
            }

            TSourceToken sourceToken = lcexpr.getAndOrTokenBeforeExpr();
            tokenBeforeExpression.add((sourceToken != null) ? sourceToken.astext : "FIRST_EXPR");

            values.add(lcexpr.getRightOperand().toString());

        }
        return true;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<String> getOperators() {
        return operators;
    }

    public List<String> getValues() {
        return values;
    }

    public List<String> getTokenBeforeExpression() {
        return tokenBeforeExpression;
    }
}
