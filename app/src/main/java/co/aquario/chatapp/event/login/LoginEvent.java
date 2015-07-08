package co.aquario.chatapp.event.login;

/**
 * Created by Mac on 3/2/15.
 */
public class LoginEvent {
    private String username;
    private String password;

    public LoginEvent(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
