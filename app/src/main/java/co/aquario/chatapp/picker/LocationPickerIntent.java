package co.aquario.chatapp.picker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Mac on 7/8/15.
 */
public class LocationPickerIntent extends Intent {
    private LocationPickerIntent() {
    }

    private LocationPickerIntent(Intent o) {
        super(o);
    }

    private LocationPickerIntent(String action) {
        super(action);
    }

    private LocationPickerIntent(String action, Uri uri) {
        super(action, uri);
    }

    private LocationPickerIntent(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    public LocationPickerIntent(Context packageContext) {
        super(packageContext, LocationPickerActivity.class);
    }
}
