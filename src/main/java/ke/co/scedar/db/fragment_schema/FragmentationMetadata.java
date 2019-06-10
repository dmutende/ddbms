package ke.co.scedar.db.fragment_schema;

import java.util.List;

public class FragmentationMetadata {

    private String fragmentationMode;
    private List<String> predicates;

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
}
