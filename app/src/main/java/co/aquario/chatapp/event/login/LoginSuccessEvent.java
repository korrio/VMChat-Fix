package co.aquario.chatapp.event.login;

import co.aquario.chatapp.model.login.LoginData;

public class LoginSuccessEvent {
    private LoginData loginData;

    public LoginSuccessEvent(LoginData loginData) {
        this.loginData = loginData;
    }

    public LoginData getLoginData() {
        return loginData;
    }
}
