package ke.co.scedar.utils;

public enum SqlStatementType {

    SelectStatement("Select Statement"),
    InsertStatement("Insert Statement"),
    UpdateStatement("Update Statement"),
    UnknownStatement("Unknown Statement");

    String value;

    SqlStatementType(String value){
        this.value = value;
    }

    public String value(){
        return value;
    }

}
