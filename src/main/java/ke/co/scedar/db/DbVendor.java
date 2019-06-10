package ke.co.scedar.db;

public enum DbVendor {

    MicrosoftSQL("MicrosoftSQL"),
    MySQL("MySQL"),
    PostgresSQL("PostgresSQL"),
    Oracle("Oracle"),
    MicrosoftAccess("MicrosoftAccess");

    private String value;

    DbVendor(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}