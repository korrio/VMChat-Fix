package co.aquario.chatapp.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.aquario.chatapp.model.BaseModel;

/**
 * Created by Mac on 2/14/15.
 */
public class FbProfile extends BaseModel {
    @Expose
    public String id;
    @Expose
    public String bio;

    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("middle_name")
    @Expose
    public String middleName;
    @SerializedName("last_name")
    @Expose
    public String lastName;

    @Expose
    public String link;
    @Expose
    public String locale;

    @Expose
    public int timezone;
    @SerializedName("updated_time")
    @Expose
    public String updated;


}
