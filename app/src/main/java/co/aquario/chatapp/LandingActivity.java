package co.aquario.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.ui.ParseLoginBuilder;
import com.txusballesteros.bubbles.BubblesManager;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.chatapp.push.ManagePush;


public class LandingActivity extends AppCompatActivity {

    private ParseUser currentUser;

    private Activity mActivity;

    private BubblesManager bubblesManager;

    @InjectView(R.id.userId)
    EditText userIdTv;

    @InjectView(R.id.partnerId)
    EditText partnerIdTv;

    @InjectView(R.id.join_button)
    Button joinButton;

    @OnClick(R.id.join_button)
    void onClick() {
        if(!userIdTv.getText().toString().equals("") && !partnerIdTv.getText().toString().equals("")) {
            ChatActivity.startChatActivity(mActivity,Integer.parseInt(userIdTv.getText().toString()),Integer.parseInt(partnerIdTv.getText().toString()));
        }

        //Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.login_button)
    void onClickLogin() {
        ParseUser.logOut();
        currentUser = null;
        showProfileLoggedOut();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //bubblesManager.recycle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.inject(this);

        mActivity = this;

//        bubblesManager = new BubblesManager.Builder(this)
//               // .setTrashLayout(R.layout.bubble_trash_layout)
//                .build();
//        bubblesManager.initialize();
//
//        BubbleLayout bubbleView = (BubbleLayout) LayoutInflater
//                .from(LandingActivity.this).inflate(R.layout.bubble_layout, null);
//        bubblesManager.addBubble(bubbleView, 60, 20);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        PushService.subscribe(getApplication(), "VMCHATENGINE", ManagePush.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("user_id", 6);
                installation.saveInBackground();
            }
        }).start();


        if (currentUser != null) {
            ChatActivity.startChatActivity(mActivity, Integer.parseInt(userIdTv.getText().toString()), Integer.parseInt(partnerIdTv.getText().toString()));


        } else {
            ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                    LandingActivity.this);
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
        }
    }

    public static final String LOG_TAG = "555";

    private void debugLog(String message) {
        if (Parse.getLogLevel() <= Parse.LOG_LEVEL_DEBUG &&
                Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, message);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setResult(resultCode);
        Log.e("asdf555",requestCode + " " +requestCode);

        if(resultCode == RESULT_OK) {
            ParseUser user = ParseUser.getCurrentUser();
            try {
                String email = user.fetch().getEmail();
                String username = user.fetch().getUsername();
                user.setPassword("asdffdsa");
                user.fetch().getInt("user_id");

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
//

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
        userIdTv.setText("6");
        partnerIdTv.setText("3082");
        ChatActivity.startChatActivity(mActivity,Integer.parseInt(userIdTv.getText().toString()),Integer.parseInt(partnerIdTv.getText().toString()));

    }

    /**
     * Show a message asking the user to log in, toggle login/logout button text.
     */
    private void showProfileLoggedOut() {

    }


}
