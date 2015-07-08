package co.aquario.chatapp.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mac on 3/3/15.
 */
public class LoginData {
    @Expose
    public String status;
    @Expose
    public String token;
    @SerializedName("api_token")
    @Expose
    public String apiToken;
    @Expose
    public UserProfile user;
}
