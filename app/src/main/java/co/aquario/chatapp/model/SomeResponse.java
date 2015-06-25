package co.aquario.chatapp.model;

/**
 * Created by matthewlogan on 9/3/14.
 */
/*
public class SomeResponse {

    private ResponseData responseData;

    public SomeResponse(ResponseData responseData) {
        this.responseData = responseData;
    }

    public ResponseData getResponseData() {
        return responseData;
    }
}
*/

public class SomeResponse {

    private SomeData randomImageData;

    public SomeResponse(SomeData randomImageData) {
        this.randomImageData = randomImageData;
    }

    public SomeData getSomeData() {
        return randomImageData;
    }
}
