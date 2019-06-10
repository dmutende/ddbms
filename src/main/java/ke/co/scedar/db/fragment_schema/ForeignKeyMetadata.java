package ke.co.scedar.db.fragment_schema;

public class ForeignKeyMetadata {

    private boolean isForeignKey;
    private String referenceField;
    private String referenceTable;

    public boolean getIsForeignKey() {
        return isForeignKey;
    }

    public void setForeignKey(boolean foreignKey) {
        isForeignKey = foreignKey;
    }

    public String getReferenceField() {
        return referenceField;
    }

    public void setReferenceField(String referenceField) {
        this.referenceField = referenceField;
    }

    public String getReferenceTable() {
        return referenceTable;
    }

    public void setReferenceTable(String referenceTable) {
        this.referenceTable = referenceTable;
    }
}
