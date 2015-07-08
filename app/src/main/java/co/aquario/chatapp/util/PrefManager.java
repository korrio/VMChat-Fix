package co.aquario.chatapp.util;

import android.content.SharedPreferences;

import com.tale.prettysharedpreferences.BooleanEditor;
import com.tale.prettysharedpreferences.DoubleEditor;
import com.tale.prettysharedpreferences.FloatEditor;
import com.tale.prettysharedpreferences.IntegerEditor;
import com.tale.prettysharedpreferences.LongEditor;
import com.tale.prettysharedpreferences.PrettySharedPreferences;
import com.tale.prettysharedpreferences.StringEditor;

/**
 * Created by Mac on 7/8/15.
 */
public class PrefManager extends PrettySharedPreferences<PrefManager> {

    public PrefManager(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }

    public StringEditor<PrefManager> name() {
        return getStringEditor("name");
    }

    public StringEditor<PrefManager> username() {
        return getStringEditor("username");
    }

    public StringEditor<PrefManager> userId() {
        return getStringEditor("userId");
    }

    public StringEditor<PrefManager> token() {
        return getStringEditor("token");
    }

    public StringEditor<PrefManager> cover() {
        return getStringEditor("cover");
    }

    public StringEditor<PrefManager> avatar() {
        return getStringEditor("avatar");
    }

    public StringEditor<PrefManager> fbToken() {
        return getStringEditor("fbToken");
    }

    public StringEditor<PrefManager> fbId() {
        return getStringEditor("fbId");
    }

    public BooleanEditor<PrefManager> isLogin() {
        return getBooleanEditor("isLogin");
    }

    public LongEditor<PrefManager> resumePosition() {
        return getLongEditor("resume_position");
    }

    public StringEditor<PrefManager> stringValue() {
        return getStringEditor("stringValue");
    }

    public BooleanEditor<PrefManager> booleanValue() {
        return getBooleanEditor("booleanValue");
    }
    public IntegerEditor<PrefManager> integerValue() {
        return getIntegerEditor("integerValue");
    }



    public LongEditor<PrefManager> longValue() {
        return getLongEditor("longValue");
    }

    public FloatEditor<PrefManager> floatValue() {
        return getFloatEditor("floatValue");
    }

    public DoubleEditor<PrefManager> doubleValue() {
        return getDoubleEditor("doubleValue");
    }

    public StringEditor<PrefManager> string(String key) {
        return getStringEditor(key);
    }
}
