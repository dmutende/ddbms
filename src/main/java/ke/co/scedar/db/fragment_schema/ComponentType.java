package ke.co.scedar.db.fragment_schema;

public enum ComponentType {

    Site ("site"),
    Table ("table"),
    Field ("field");

    String value;

    ComponentType(String value){
        this.value = value;
    }

    public String value(){
        return value;
    }
}
