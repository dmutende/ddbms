package ke.co.scedar.api.payloads;

import ke.co.scedar.utils.security.ScedarUID;

public class ExecuteQueryPayload {

    private String id = ScedarUID.generateUid(20);
    private String query;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "ExecuteQueryPayload{" +
                "id='" + id + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}
