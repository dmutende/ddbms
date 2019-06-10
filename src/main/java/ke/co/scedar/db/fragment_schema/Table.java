package ke.co.scedar.db.fragment_schema;

import java.util.List;

public class Table {

    private String name;
    private boolean isFragmented;
    private FragmentationMetadata fragmentationMetadata;
    private List<Field> fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsFragmented() {
        return isFragmented;
    }

    public void setFragmented(boolean fragmented) {
        isFragmented = fragmented;
    }

    public FragmentationMetadata getFragmentationMetadata() {
        return fragmentationMetadata;
    }

    public void setFragmentationMetadata(FragmentationMetadata fragmentationMetadata) {
        this.fragmentationMetadata = fragmentationMetadata;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
