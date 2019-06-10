package ke.co.scedar.db.fragment_schema;

public class Field {

    private String name;
    private ForeignKeyMetadata foreignKeyMetadata;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ForeignKeyMetadata getForeignKeyMetadata() {
        return foreignKeyMetadata;
    }

    public void setForeignKeyMetadata(ForeignKeyMetadata foreignKeyMetadata) {
        this.foreignKeyMetadata = foreignKeyMetadata;
    }
}
