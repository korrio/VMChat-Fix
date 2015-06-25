package co.aquario.chatapp.event.response;

import co.aquario.chatapp.model.SomeData;

/**
 * Created by Mac on 3/2/15.
 */
public class SuccessEvent {
    private SomeData someData;

    public SuccessEvent(SomeData someData) {
        this.someData = someData;
    }

    public SomeData getSomeResponse() {
        return someData;
    }
}
