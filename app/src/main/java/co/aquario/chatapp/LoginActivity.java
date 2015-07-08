package co.aquario.chatapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.google.gson.Gson;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import co.aquario.chatapp.model.login.FbProfile;
import co.aquario.chatapp.util.PrefManager;
import de.hdodenhof.circleimageview.CircleImageView;


public class LoginActivity extends AppCompatActivity {

    public PrefManager prefManager;

    CircleImageView mProfileImage;
    Button mBtnFb;
    TextView mUsername, mEmailID;
    Profile mFbProfile;
    ParseUser parseUser;
    String name = null, email = null;

    FbProfile profile;

    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_fb);

        prefManager = ChatApp.get(this).getPrefManager();

        mBtnFb = (Button) findViewById(R.id.btn_fb_login);
        mProfileImage = (CircleImageView) findViewById(R.id.profile_image);

        mUsername = (TextView) findViewById(R.id.txt_name);
        mEmailID = (TextView) findViewById(R.id.txt_email);

        mFbProfile = Profile.getCurrentProfile();

        //  Use this to test if Parse is working (by sending dummy data)
        /*ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
        testObject.get("foo");*/

        //  Use this to output your Facebook Key Hash to Logs
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
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

        mBtnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, mPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {

                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Facebook! :: " + AccessToken.getCurrentAccessToken().getToken());
                            getUserDetailsFromFB();

                            prefManager
                                    .fbToken().put( AccessToken.getCurrentAccessToken().getToken())
                                    .fbId().put(profile.id).commit();
//                            saveNewUser();
                        } else {
                            Log.d("MyApp", "User logged in through Facebook! :: " + AccessToken.getCurrentAccessToken().getToken());
                            getUserDetailsFromFB();
                        }
                    }
                });

            }
        });
    }

    private void saveNewUser() {
        parseUser = ParseUser.getCurrentUser();
        parseUser.setUsername(name);
        parseUser.setEmail(email);

//        Saving profile photo as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] data = stream.toByteArray();
        String thumbName = parseUser.getUsername().replaceAll("\\s+", "");
        final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", data);

        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                parseUser.put("profileThumb", parseFile);

                //Finally save all the user details
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(LoginActivity.this, "New user:" + name + " Signed up", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }


    private void getUserDetailsFromFB() {



        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Gson gson = new Gson();


                            profile = gson.fromJson(response.getRawResponse(), FbProfile.class);

                            email = response.getJSONObject().getString("email");
                            mEmailID.setText(email);
                            name = response.getJSONObject().getString("name");
                            mUsername.setText(name);

                            String myFbProfilePic = mFbProfile.getProfilePictureUri(220,220).toString();

                            Log.e("myimage",myFbProfilePic);
                            Log.e("myprofile",profile.firstName + " " + profile.lastName);

                            Picasso
                                    .with(getApplicationContext())
                                    .load(myFbProfilePic)
                                    .into(mProfileImage);

                            //saveNewUser();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();



        /*
        ProfilePhotoAsync profilePhotoAsync = new ProfilePhotoAsync(mFbProfile);
        profilePhotoAsync.execute();
        */

    }

    private void getUserDetailsFromParse() {
        parseUser = ParseUser.getCurrentUser();

//Fetch profile photo
        try {
            ParseFile parseFile = parseUser.getParseFile("profileThumb");
            byte[] data = parseFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mProfileImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mEmailID.setText(parseUser.getEmail());
        mUsername.setText(parseUser.getUsername());

        Toast.makeText(LoginActivity.this, "Welcome back " + mUsername.getText().toString(), Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    public static Bitmap DownloadImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("IMAGE", "Error getting bitmap", e);
        }
        return bm;
    }

}
