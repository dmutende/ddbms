package ke.co.scedar.utils.columns;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TGroupByItemList;
import gudusoft.gsqlparser.nodes.TOrderBy;

public class ColumnInClause{
        public ColumnInClause(){}

        public void printColumns(TExpression expression, TCustomSqlStatement statement){
            System.out.println("Referenced utils.columns:");
            ColumnVisitor cv = new ColumnVisitor(statement);
            expression.postOrderTraverse(cv);
        }

        public void printColumns(TGroupByItemList list, TCustomSqlStatement statement){
            System.out.println("Referenced utils.columns:");
            ColumnExprVisitor gbv = new ColumnExprVisitor(statement);
            list.acceptChildren(gbv);
        }

        public void printColumns(TOrderBy orderBy, TCustomSqlStatement statement){
            System.out.println("Referenced utils.columns:");
            ColumnExprVisitor obv = new ColumnExprVisitor(statement);
            orderBy.acceptChildren(obv);
        }
    }