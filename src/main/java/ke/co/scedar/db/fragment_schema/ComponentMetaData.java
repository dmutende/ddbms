package ke.co.scedar.db.fragment_schema;

import java.util.List;

public class ComponentMetaData {

    private ComponentType componentType;
    private boolean hasError = false;
    private int siteId;
    private String tableName;
    private boolean isFragmented = false;
    private String fragmentationMode;
    private List<String> predicates;
    private String fragmentationField;
    private String fragmentationValue;
    private String fieldName;
    private List<String> fields;
    private boolean isForeignKey;
    private String referenceField;
    private String referenceTable;

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public boolean hasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isFragmented() {
        return isFragmented;
    }

    public void setFragmented(boolean fragmented) {
        isFragmented = fragmented;
    }

    public String getFragmentationMode() {
        return fragmentationMode;
    }

    public void setFragmentationMode(String fragmentationMode) {
        this.fragmentationMode = fragmentationMode;
    }

    public List<String> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<String> predicates) {
        this.predicates = predicates;
    }

    public String getFragmentationField() {
        return fragmentationField;
    }

    public void setFragmentationField(String fragmentationField) {
        this.fragmentationField = fragmentationField;
    }

    public String getFragmentationValue() {
        return fragmentationValue;
    }

    public void setFragmentationValue(String fragmentationValue) {
        this.fragmentationValue = fragmentationValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public boolean isForeignKey() {
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

    public boolean hasField(String fieldName){
        if(fields != null){
            for (String field : fields){
                if(field.equals(fieldName)) return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ComponentMetaData{" +
                "\n\t\tcomponentType=" + componentType +
                ", \n\t\thasError=" + hasError +
                ", \n\t\tsiteId=" + siteId +
                ", \n\t\ttableName='" + tableName + '\'' +
                ", \n\t\tisFragmented=" + isFragmented +
                ", \n\t\tfragmentationMode='" + fragmentationMode + '\'' +
                ", \n\t\tpredicates=" + predicates +
                ", \n\t\tfragmentationField='" + fragmentationField + '\'' +
                ", \n\t\tfragmentationValue='" + fragmentationValue + '\'' +
                ", \n\t\tfieldName='" + fieldName + '\'' +
                ", \n\t\tfields=" + fields +
                ", \n\t\tisForeignKey=" + isForeignKey +
                ", \n\t\treferenceField='" + referenceField + '\'' +
                ", \n\t\treferenceTable='" + referenceTable + '\'' +
                "\n\t}";
    }
}
