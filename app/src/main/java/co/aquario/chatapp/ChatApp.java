package co.aquario.chatapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

import co.aquario.chatapp.handler.ApiBus;
import co.aquario.chatapp.handler.ApiHandler;
import co.aquario.chatapp.handler.ApiService;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;


public class ChatApp extends Application {
    private static final String ENDPOINT = "https://chat.vdomax.com:1313/api";

    //private static final String ENDPOINT = "http://wallsplash.lanora.io";
    private ApiHandler chatApiHandler;

    @Override public void onCreate() {
        super.onCreate();
        chatApiHandler = new ApiHandler(this, buildApi(),
                ApiBus.getInstance());
        chatApiHandler.registerForEvents();

        Parse.initialize(this);

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        ParseFacebookUtils.initialize(this);
    }

    ApiService buildApi() {
        return new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setEndpoint(ENDPOINT)

                .setRequestInterceptor(new RequestInterceptor() {
                    @Override public void intercept(RequestFacade request) {
                        //request.addQueryParam("p1", "var1");
                        //request.addQueryParam("p2", "");
                    }
                })

                .build()
                .create(ApiService.class);
    }
}
