package co.aquario.chatapp.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Mac on 6/25/15.
 */
public class ChatUser {
    @Expose
    public int senderId;
    @Expose
    public String username;
    @Expose
    public String avatar;
    @Expose
    public String extension;

    public String getAvatarPath() {
        return "https://www.vdomax.com/" + avatar + "." + extension;
    }
}
