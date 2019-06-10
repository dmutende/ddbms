package ke.co.scedar.translator;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.util.ArrayList;
import java.util.List;

public class SqlTranslator {

    private EDbVendor targetVendor;

    private String checkResult;

    private TGSqlParser sqlParser;

    private List<IdentifierCheckResult> identifierCheckResults = new ArrayList<>();
    private List<DataTypeCheckResult> dataTypeCheckResults = new ArrayList<>();
    private List<FunctionCheckResult> functionCheckResults = new ArrayList<>();
    private List<KeywordCheckResult> keywordCheckResults = new ArrayList<>();

    public SqlTranslator(String sql, EDbVendor sourceVendor,
                         EDbVendor targetVendor) {
        this.targetVendor = targetVendor;
        sqlParser = new TGSqlParser(sourceVendor);
        sqlParser.sqltext = sql;
        checkSQL(sqlParser);
        convertAndTranslate();
    }

    private void convertAndTranslate() {
        getCheckResult(true);

        boolean convertJoin = false;
        for (int i = 0; i < keywordCheckResults.size(); i++) {
            KeywordCheckResult result = keywordCheckResults.get(i);
            if (result.getTreeNode() instanceof TSelectSqlStatement) {
                result.translate();
                convertJoin = true;
            }
        }
        if (convertJoin) {
            sqlParser.sqltext = getSqlText();
            identifierCheckResults.clear();
            dataTypeCheckResults.clear();
            functionCheckResults.clear();
            keywordCheckResults.clear();

            checkSQL(sqlParser);
        }

        FunctionCheckResult result = null;
        while ((result = getFunctionTranslate()) != null) {
            result.translate();
            sqlParser.sqltext = getSqlText();
            identifierCheckResults.clear();
            dataTypeCheckResults.clear();
            functionCheckResults.clear();
            keywordCheckResults.clear();

            checkSQL(sqlParser);
        }

        translate(sqlParser);
    }

    public TGSqlParser getSqlParser(){
        return sqlParser;
    }

    public String getTranslateResult( )
    {
        if ( sqlParser != null && sqlParser.getErrorCount( ) == 0 )
        {
            return getSqlText( );
        }
        return null;
    }

    private String getCheckResult(boolean force) {
        if (checkResult != null && !force) {
            return checkResult;
        }

        if (sqlParser != null && sqlParser.getErrorCount() == 0) {
            StringBuffer buffer = new StringBuffer();
            int totalIdentifier = 0;
            int translateIdentifier = 0;
            if (identifierCheckResults.size() > 0) {
                for (int i = 0; i < identifierCheckResults.size(); i++) {
                    IdentifierCheckResult result = identifierCheckResults.get(i);
                    totalIdentifier++;
                    if (result.canTranslate())
                        translateIdentifier++;
                    buffer.append("Identifier "
                            + result.getOriginalText()
                            + " need to be translated.\r\n");
                    buffer.append("Location: "
                            + result.getOriginalLineNo()
                            + ", "
                            + result.getOriginalColumnNo()
                            + "\r\n");
                    buffer.append("Transltion reason: "
                            + result.getTranslationInfo()
                            + "\r\n");
                    if (result.canTranslate()) {
                        buffer.append("Tool can translate: "
                                + (result.canTranslate() ? "Yes" : "No")
                                + "\r\n");
                        buffer.append("Translation result: "
                                + result.getTranslateResult()
                                + "\r\n");
                    } else {
                        buffer.append("Tool can translate: "
                                + (result.canTranslate() ? "Yes" : "No")
                                + "\r\n");
                    }

                    buffer.append("\r\n");
                }
            }

            int totalKeyword = 0;
            int translateKeyword = 0;
            if (keywordCheckResults.size() > 0) {
                if (identifierCheckResults.size() > 0)
                    buffer.append("\r\n");
                for (int i = 0; i < keywordCheckResults.size(); i++) {
                    KeywordCheckResult result = keywordCheckResults.get(i);
                    totalKeyword++;
                    if (result.canTranslate())
                        translateKeyword++;
                    if (result.getTreeNode() instanceof TCustomSqlStatement) {
                        buffer.append("Statement ")
                                .append(result.getOriginalTreeNodeText())
                                .append(" need to be translated.\r\n");
                        buffer.append("Keyword Location: "
                                + result.getOriginalLineNo()
                                + ", "
                                + result.getOriginalColumnNo()
                                + "\r\n");

                    } else {
                        buffer.append((isKeyword(result.getToken()) ? "Keyword "
                                : "Keyword ")
                                + (result.getOriginalTreeNodeText() != null ? result.getOriginalTreeNodeText()
                                : result.getOriginalText())
                                + " need to be translated.\r\n");

                        buffer.append("Location: "
                                + result.getOriginalLineNo()
                                + ", "
                                + result.getOriginalColumnNo()
                                + "\r\n");

                    }

                    buffer.append("Transltion reason: "
                            + result.getTranslationInfo()
                            + "\r\n");
                    if (result.canTranslate()) {
                        buffer.append("Tool can translate: "
                                + (result.canTranslate() ? "Yes" : "No")
                                + "\r\n");
                        buffer.append("Translation result: "
                                + result.getTranslateResult()
                                + "\r\n");
                    } else {
                        buffer.append("Tool can translate: "
                                + (result.canTranslate() ? "Yes" : "No")
                                + "\r\n");
                    }

                    buffer.append("\r\n");
                }
            }

            int totalDataType = 0;
            int translateDataType = 0;

            if (dataTypeCheckResults.size() > 0) {
                if (keywordCheckResults.size() > 0)
                    buffer.append("\r\n");
                for (int i = 0; i < dataTypeCheckResults.size(); i++) {
                    DataTypeCheckResult result = dataTypeCheckResults.get(i);
                    totalDataType++;
                    if (result.canTranslate())
                        translateDataType++;

                    buffer.append("Data type "
                            + result.getOriginalText()
                            + " need to be translated.\r\n");
                    buffer.append("Location: "
                            + result.getOriginalLineNo()
                            + ", "
                            + result.getOriginalColumnNo()
                            + "\r\n");
                    if (result.canTranslate()) {
                        buffer.append("Tool can translate: "
                                + (result.canTranslate() ? "Yes" : "No")
                                + "\r\n");
                        String translate = result.getTranslateResult();
                        if (translate != null) {
                            buffer.append("Translation result: "
                                    + result.getTranslateResult()
                                    + "\r\n");
                        }
                    } else {
                        buffer.append("Tool can translate: "
                                + (result.canTranslate() ? "Yes" : "No")
                                + "\r\n");
                    }

                    buffer.append("\r\n");
                }
            }

            int totalFunction = 0;
            int translateFunction = 0;

            if (functionCheckResults.size() > 0) {
                if (dataTypeCheckResults.size() > 0)
                    buffer.append("\r\n");
                for (int i = 0; i < functionCheckResults.size(); i++) {
                    FunctionCheckResult result = functionCheckResults.get(i);
                    totalFunction++;
                    if (result.canTranslate())
                        translateFunction++;

                    buffer.append("Function "
                            + result.getOriginalText()
                            + " need to be translated.\r\n");
                    buffer.append("Location: "
                            + result.getOriginalLineNo()
                            + ", "
                            + result.getOriginalColumnNo()
                            + "\r\n");
                    if (result.canTranslate()) {
                        buffer.append("Tool can translate: "
                                + (result.canTranslate() ? "Yes" : "No")
                                + "\r\n");
                        String translate = result.getTranslateResult();
                        if (translate != null) {
                            buffer.append("Translation result: "
                                    + result.getTranslateResult()
                                    + "\r\n");
                        }
                    } else {
                        buffer.append("Tool can translate: "
                                + (result.canTranslate() ? "Yes" : "No")
                                + "\r\n");
                    }

                    buffer.append("\r\n");

                }
            }

            StringBuffer totalInfo = new StringBuffer();
            if (sqlParser.getSqlfilename() != null) {
                totalInfo.append("Check the input sql file "
                        + sqlParser.getSqlfilename()
                        + "\r\n");
            }
            totalInfo.append("Found "
                    + totalIdentifier
                    + " identifier"
                    + (totalIdentifier > 1 ? "s" : "")
                    + " need to be translated, "
                    + translateIdentifier
                    + " identifier"
                    + (translateIdentifier > 1 ? "s" : "")
                    + " had been translated by tool "
                    + (totalIdentifier - translateIdentifier)
                    + " identifier"
                    + ((totalIdentifier - translateIdentifier) > 1 ? "s"
                    : "")
                    + " need to be translated by handy.\r\n");

            totalInfo.append("Found "
                    + totalKeyword
                    + " keyword"
                    + (totalKeyword > 1 ? "s" : "")
                    + " need to be translated, "
                    + translateKeyword
                    + " keyword"
                    + (translateKeyword > 1 ? "s" : "")
                    + " had been translated by tool "
                    + (totalKeyword - translateKeyword)
                    + " keyword"
                    + ((totalKeyword - translateKeyword) > 1 ? "s" : "")
                    + " need to be translated by handy.\r\n");

            totalInfo.append("Found "
                    + totalDataType
                    + " data type"
                    + (totalDataType > 1 ? "s" : "")
                    + " need to be translated, "
                    + translateDataType
                    + " data type"
                    + (translateDataType > 1 ? "s" : "")
                    + " had been translated by tool "
                    + (totalDataType - translateDataType)
                    + " data type"
                    + ((totalDataType - translateDataType) > 1 ? "s" : "")
                    + " need to be translated by handy.\r\n");

            totalInfo.append("Found "
                    + totalFunction
                    + " function"
                    + (totalFunction > 1 ? "s" : "")
                    + " need to be translated, "
                    + translateFunction
                    + " function"
                    + (translateDataType > 1 ? "s" : "")
                    + " had been translated by tool "
                    + (totalFunction - translateFunction)
                    + " function"
                    + ((totalFunction - translateFunction) > 1 ? "s" : "")
                    + " need to be translated by handy.\r\n\r\n");

            buffer.insert(0, totalInfo);

            checkResult = buffer.toString();
            return checkResult;
        }
        checkResult = null;
        return checkResult;
    }

    private String getSqlText() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < sqlParser.sourcetokenlist.size(); i++) {
            buffer.append(sqlParser.sourcetokenlist.get(i).toString());
        }
        return buffer.toString();
    }

    private FunctionCheckResult getFunctionTranslate() {
        for (int i = 0; i < functionCheckResults.size(); i++) {
            FunctionCheckResult result = functionCheckResults.get(i);
            if (result.canTranslate())
                return result;
        }
        return null;
    }


    private void checkSQL(TGSqlParser sqlparser) {
        int ret = sqlparser.parse();

        if (ret != 0) {
            System.err.println(sqlparser.getErrormessage());
        } else {
            TSourceTokenList tokenList = sqlparser.sourcetokenlist;
            checkIdentifier(tokenList);
            checkDataType(tokenList);
            checkFunction(tokenList);
        }

    }

    private void checkIdentifier(TSourceTokenList tokenList) {
        for (int i = 0; i < tokenList.size(); i++) {
            TSourceToken token = tokenList.get(i);
            if (token.tokentype == ETokenType.ttidentifier
                    || IdentifierChecker.isQuotedIdentifier(token)) {
                IdentifierCheckResult result = IdentifierChecker.checkIdentifier(token,
                        targetVendor);
                if (result.needTranslate()) {
                    identifierCheckResults.add(result);
                }
            } else if (isKeyword(token)) {
                KeywordCheckResult result = KeywordChecker.checkKeyword(token,
                        targetVendor);
                if (result != null) {
                    keywordCheckResults.add(result);
                }
            }
        }
    }

    private void checkFunction(TSourceTokenList tokenList) {
        for (int i = 0; i < tokenList.size(); i++) {
            TSourceToken token = tokenList.get(i);
            if (token.getDbObjType() == TObjectName.ttobjFunctionName) {
                TParseTreeNodeList list = token.getNodesStartFromThisToken();
                for (int j = 0; j < list.size(); j++) {
                    TParseTreeNode node = (TParseTreeNode) list.getElement(j);
                    if (node instanceof TFunctionCall) {
                        FunctionCheckResult result = FunctionChecker.checkFunction((TFunctionCall) node,
                                targetVendor);
                        if (result != null) {
                            functionCheckResults.add(result);
                        }
                    }
                }
            }
        }
    }

    private void checkDataType(TSourceTokenList tokenList) {
        for (int i = 0; i < tokenList.size(); i++) {
            TSourceToken token = tokenList.get(i);
            if (token.tokencode == TBaseType.rrw_create
                    && token.stmt instanceof TCreateTableSqlStatement) {
                TCreateTableSqlStatement stmt = (TCreateTableSqlStatement) token.stmt;
                for (int j = 0; j < stmt.getColumnList().size(); j++) {
                    TColumnDefinition column = stmt.getColumnList()
                            .getColumn(j);
                    TTypeName dataType = column.getDatatype();
                    DataTypeCheckResult result = DataTypeChecker.checkDataType(dataType,
                            targetVendor);
                    if (result != null) {
                        dataTypeCheckResults.add(result);
                    }
                }
            }
        }
    }

    private boolean isKeyword(TSourceToken token) {
        return token.tokentype == ETokenType.ttkeyword;
    }

    private void translate(TGSqlParser sqlParser) {
        if (sqlParser != null && sqlParser.getErrorCount() == 0) {
            for (int i = 0; i < identifierCheckResults.size(); i++) {
                IdentifierCheckResult result = identifierCheckResults.get(i);
                result.translate();
            }

            for (int i = 0; i < keywordCheckResults.size(); i++) {
                KeywordCheckResult result = keywordCheckResults.get(i);
                result.translate();
            }

            for (int i = 0; i < dataTypeCheckResults.size(); i++) {
                DataTypeCheckResult result = dataTypeCheckResults.get(i);
                result.translate();
            }

            for (int i = 0; i < functionCheckResults.size(); i++) {
                FunctionCheckResult result = functionCheckResults.get(i);
                result.translate();
            }
        }
    }
}
