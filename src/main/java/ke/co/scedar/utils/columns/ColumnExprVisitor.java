package ke.co.scedar.utils.columns;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;

public class ColumnExprVisitor extends TParseTreeVisitor {

    TCustomSqlStatement statement = null;

    public ColumnExprVisitor(TCustomSqlStatement statement) {
        this.statement = statement;
    }

    public void preVisit(TExpression expression){
        ColumnVisitor cv = new ColumnVisitor(statement);
        expression.postOrderTraverse(cv);
    }
}