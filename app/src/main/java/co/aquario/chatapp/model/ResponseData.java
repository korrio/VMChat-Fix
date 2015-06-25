package co.aquario.chatapp.model;

import java.util.List;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class ResponseData {

    private List<SomeData> results;

    public ResponseData(List<SomeData> results) {
        this.results = results;
    }

    public List<SomeData> getResults() {
        return results;
    }
}
