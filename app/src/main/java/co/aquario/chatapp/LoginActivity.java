package co.aquario.chatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.chatapp.fragment.ChatFragment;
import co.aquario.chatapp.fragment.ChatWidgetFragment;


public class LoginActivity extends AppCompatActivity {

    private ParseUser currentUser;

    @InjectView(R.id.userId)
    EditText userIdTv;

    @InjectView(R.id.partnerId)
    EditText partnerIdTv;

    @InjectView(R.id.join_button)
    Button joinButton;

    @OnClick(R.id.join_button)
    void onClick() {
        if(!userIdTv.getText().toString().equals("") && !partnerIdTv.getText().toString().equals("")) {
            Bundle data = new Bundle();
            data.putInt("USER_ID_1", Integer.parseInt(userIdTv.getText().toString()));
            data.putInt("USER_ID_2",Integer.parseInt(partnerIdTv.getText().toString()));
            ChatFragment fragment = new ChatFragment();
            fragment.setArguments(data);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, "CHAT_MAIN").addToBackStack(null).commit();
        }

        //Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.login_button)
    void onClickLogin() {
//        ParseUser.logOut();
//        currentUser = null;
//        showProfileLoggedOut();

        Bundle data = new Bundle();
        data.putInt("USER_ID_1", Integer.parseInt("6"));
        data.putInt("USER_ID_2",Integer.parseInt("3082"));
        ChatWidgetFragment fragment = new ChatWidgetFragment();
        fragment.setArguments(data);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, "CHAT_WIDGET").addToBackStack(null).commit();
        /*
        if(currentUser == null) {
            ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                    LoginActivity.this);
            Intent parseLoginIntent = loginBuilder.setParseLoginEnabled(true)
                    .setParseLoginButtonText("Login")
                    .setParseSignupButtonText("Register")
                    .setParseLoginHelpText("Forgot password?")
                    .setParseLoginInvalidCredentialsToastText("You email and/or password is not correct")
                    .setParseLoginEmailAsUsername(true)
                    .setParseSignupSubmitButtonText("Submit registration")
                    .setFacebookLoginEnabled(true)
                    .setFacebookLoginButtonText("Facebook")
                    .setFacebookLoginPermissions(Arrays.asList("user_status", "read_stream"))
                    .setTwitterLoginEnabled(false)
                            //.setTwitterLoginButtontext("Twitter")
                    .build();
            startActivityForResult(parseLoginIntent, 0);
        } else {
            Bundle data = new Bundle();
            data.putInt("USER_ID_1", Integer.parseInt("6"));
            data.putInt("USER_ID_2",Integer.parseInt("3082"));
            ChatFragment fragment = new ChatFragment();
            fragment.setArguments(data);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, "CHAT_MAIN").addToBackStack(null).commit();
        }
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            showProfileLoggedIn();
        } else {
            showProfileLoggedOut();
        }
    }

    /**
     * Shows the profile of the given user.
     */
    private void showProfileLoggedIn() {

    }

    /**
     * Show a message asking the user to log in, toggle login/logout button text.
     */
    private void showProfileLoggedOut() {

    }


}
