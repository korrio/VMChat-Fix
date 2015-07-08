package co.aquario.chatapp.model.login;

import com.google.gson.annotations.Expose;

import co.aquario.chatapp.model.BaseModel;


public class UserProfile extends BaseModel {
    @Expose
    public String id;
    @Expose
    public String name;
    //@SerializedName("avatar_url")
    @Expose
    public String avatar;
    //@SerializedName("cover_url")
    @Expose
    public String cover;
    @Expose
    public String username;
    @Expose
    public String email;

}
