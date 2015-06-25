package co.aquario.chatapp.handler;

import com.squareup.otto.Bus;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class ApiBus extends Bus {

    private static ApiBus singleton;

    public static ApiBus getInstance() {
        if (singleton == null) {
            singleton = new ApiBus();
        }
        return singleton;
    }
}
