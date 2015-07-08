package co.aquario.chatapp.event.login;

import co.aquario.chatapp.model.login.UserProfile;

/**
 * Created by Mac on 3/3/15.
 */
public class UpdateProfileEvent {

    private UserProfile user;

    public UpdateProfileEvent(UserProfile user) {
        this.user = user;
    }

    public UserProfile getUserProfile() {
        return user;
    }

}
