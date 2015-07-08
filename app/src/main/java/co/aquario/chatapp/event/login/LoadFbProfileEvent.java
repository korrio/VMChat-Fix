package co.aquario.chatapp.event.login;


import co.aquario.chatapp.model.login.FbProfile;

public class LoadFbProfileEvent {

    public FbProfile profile;

    public String facebookToken;

    public LoadFbProfileEvent(FbProfile profile,String facebookToken) {
        this.profile = profile;
        this.facebookToken = facebookToken;
    }
}
