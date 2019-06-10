package ke.co.scedar.utils.columns;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.*;

public class ColumnVisitor implements IExpressionVisitor {

        TCustomSqlStatement statement = null;

        public ColumnVisitor(TCustomSqlStatement statement) {
            this.statement = statement;
        }

        String getColumnWithBaseTable(TObjectName objectName){
            String ret = "";
            TTable table = null;
            boolean  find = false;
            TCustomSqlStatement lcStmt = statement;

            while ((lcStmt != null) && (!find)){
                for(int i=0;i<lcStmt.tables.size();i++){
                    table = lcStmt.tables.getTable(i);
                    for(int j=0;j<table.getLinkedColumns().size();j++){
                        if (objectName == table.getLinkedColumns().getObjectName(j)){
                            if(table.isBaseTable()){
                                ret =  table.getTableName()+"."+objectName.getColumnNameOnly();
                            }else{
                                //derived table
                                if (table.getAliasClause() != null){
                                    ret =  table.getAliasClause().toString()+"."+objectName.getColumnNameOnly();
                                }else {
                                    ret =  objectName.getColumnNameOnly();
                                }

                                ret += "(column in derived table)";
                            }
                            find = true;
                            break;
                        }
                    }
                }
                if(!find){
                    lcStmt = lcStmt.getParentStmt();
                }
            }

            return  ret;
        }

        public boolean exprVisit(TParseTreeNode pNode, boolean isLeafNode){
            TExpression expr = (TExpression)pNode;
            switch ((expr.getExpressionType())){
                case simple_object_name_t:
                    TObjectName obj = expr.getObjectOperand();

                    System.out.println("Column: "+obj.getColumnNameOnly());
                    System.out.println("Value: "+expr.getConstantOperand());

                    if (obj.getObjectType() != TObjectName.ttobjNotAObject){
//                        System.out.println(getColumnWithBaseTable(obj));
                        System.out.println(obj.getColumnNameOnly());
                        if (statement.dbvendor == EDbVendor.dbvteradata){
                            // this maybe a column alias
                            //for(int i=0;i<statement.)
                        }
                        // System.out.print(statement.dbvendor);
                    }
                    break;
                case function_t:
                    ColumnExprVisitor fcv = new ColumnExprVisitor(statement);
                    expr.getFunctionCall().acceptChildren(fcv);
                    break;
                case case_t:
                    TCaseExpression caseExpression = expr.getCaseExpression();
                    ColumnExprVisitor cv = new ColumnExprVisitor(statement);
                    caseExpression.acceptChildren(cv);

                    break;
            }
            return  true;
        }

    }