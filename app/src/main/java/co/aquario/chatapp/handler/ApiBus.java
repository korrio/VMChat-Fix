package co.aquario.chatapp.handler;

import android.os.Handler;
import android.os.Looper;

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

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void postQueue(final Object obj) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ApiBus.getInstance().post(obj);
            }
        });
    }
}
