package co.aquario.chatapp.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.google.gson.Gson;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import co.aquario.chatapp.ChatApp;
import co.aquario.chatapp.LandingActivity;
import co.aquario.chatapp.R;
import co.aquario.chatapp.event.FailedNetworkEvent;
import co.aquario.chatapp.event.login.LoginEvent;
import co.aquario.chatapp.event.login.LoginFailedAuthEvent;
import co.aquario.chatapp.event.login.LoginSuccessEvent;
import co.aquario.chatapp.event.login.UpdateProfileEvent;
import co.aquario.chatapp.handler.ApiBus;
import co.aquario.chatapp.model.login.FbProfile;
import co.aquario.chatapp.model.login.LoginData;
import co.aquario.chatapp.model.login.UserProfile;
import co.aquario.chatapp.util.PrefManager;


public class LoginFragment extends BaseFragment {

    public PrefManager prefManager;
    private AQuery aq;
    //private FacebookHandle handle;
    private FbProfile profile;
    private Profile mFbProfile;
    private EditText userEt;
    private EditText passEt;

    private TextView loginBtn;
    private TextView registerBtn;

    private LinearLayout fbBtn;
    private String facebookToken;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(getActivity());
        prefManager = ChatApp.get(getActivity()).getPrefManager();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
                    "co.aquario.chatapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        userEt = (EditText) rootView.findViewById(R.id.et_user);
        passEt = (EditText) rootView.findViewById(R.id.et_pass);

        loginBtn = (TextView) rootView.findViewById(R.id.tv_login);
        registerBtn = (TextView) rootView.findViewById(R.id.tv_reg);

        fbBtn = (LinearLayout) rootView.findViewById(R.id.btn_fb);
        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authFacebook();
            }
        });

        mFbProfile = Profile.getCurrentProfile();

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(getActivity()).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                userEt.setText(possibleEmail);
            }
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userEt.getText().toString().trim().equals("") && !passEt.getText().toString().trim().equals(""))
                    ApiBus.getInstance().post(new LoginEvent(userEt.getText().toString().trim(),
                        passEt.getText().toString().trim()));
                else
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.empty_input), Toast.LENGTH_SHORT).show();

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getFragmentManager().beginTransaction().add(R.id.login_container, new RegisterFragment(),"REGISTER").addToBackStack(null).commit();
            }
        });

        return rootView;
    }

    public void authFacebook() {
        Collection<String> mPermissions = Arrays.asList("user_status", "read_stream");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(), mPermissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook! :: " + AccessToken.getCurrentAccessToken().getToken());
                    getUserDetailsFromFB();
                } else {
                    Log.d("MyApp", "User logged in through Facebook! :: " + AccessToken.getCurrentAccessToken().getToken());
                    getUserDetailsFromFB();
                }
            }
        });
    }

    private void loginVM(String fbToken) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormEncodingBuilder()
                .add("access_token", fbToken)
                .build();
        Request request = new Request.Builder()
                .url("http://api.vdomax.com/1.0/fbAuth")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ApiBus.getInstance().post(new LoginFailedAuthEvent());
        } else {
            Gson gson = new Gson();
            LoginData loginData = gson.fromJson(response.body().charStream(), LoginData.class);

            LoginSuccessEvent event = new LoginSuccessEvent(loginData);

            ChatApp.USER_TOKEN = event.getLoginData().token;
            Log.e("USER_TOKEN", ChatApp.USER_TOKEN);

            prefManager
                    .name().put(event.getLoginData().user.name)
                    .username().put(event.getLoginData().user.username)
                    .userId().put(event.getLoginData().user.id)
                    .token().put(event.getLoginData().token)
                    .cover().put(event.getLoginData().user.cover)
                    .avatar().put(event.getLoginData().user.avatar)
                    .isLogin().put(true)
                    .commit();

            Log.e("VM_PROFILE", event.getLoginData().user.toString());

            Intent main = new Intent(getActivity(),LandingActivity.class);
            getActivity().startActivity(main);

            UserProfile user = event.getLoginData().user;
            ApiBus.getInstance().post(new UpdateProfileEvent(user));

        }
    }

    private void getUserDetailsFromFB() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Gson gson = new Gson();
                        profile = gson.fromJson(response.getRawResponse(), FbProfile.class);
                        prefManager
                                .fbToken().put(facebookToken)
                                .fbId().put(profile.id).commit();

                        String myFbProfilePic = mFbProfile.getProfilePictureUri(220,220).toString();

                        prefManager
                                .fbToken().put( AccessToken.getCurrentAccessToken().getToken())
                                .fbId().put(profile.id).commit();

                        try {
                            loginVM(AccessToken.getCurrentAccessToken().getToken());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void onLoginFailedNetwork(FailedNetworkEvent event) {
        Log.e("ARAIWA", "onLoginFailedNetwork");
        prefManager.clear();
    }

    @Subscribe
    public void onLoginFailedAuth(LoginFailedAuthEvent event) {
        Log.e("ARAIWA", "onLoginFailedAuth");
        Toast.makeText(getActivity().getApplicationContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
    }


}
