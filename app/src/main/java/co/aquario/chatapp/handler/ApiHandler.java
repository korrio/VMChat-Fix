package co.aquario.chatapp.handler;

import android.content.Context;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import co.aquario.chatapp.event.request.ConversationEvent;
import co.aquario.chatapp.event.request.SomeEvent;
import co.aquario.chatapp.event.response.ConversationEventSuccess;
import co.aquario.chatapp.event.response.FailedEvent;
import co.aquario.chatapp.event.response.SuccessEvent;
import co.aquario.chatapp.model.ConversationOneToOne;
import co.aquario.chatapp.model.SomeData;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class ApiHandler {

    private Context context;
    private ApiService api;
    private ApiBus apiBus;

    public ApiHandler(Context context, ApiService api,
                      ApiBus apiBus) {

        this.context = context;
        this.api = api;
        this.apiBus = apiBus;
    }

    public void registerForEvents() {
        apiBus.register(this);
    }

    @Subscribe public void onSomeEvent(SomeEvent event) {
        Log.e("HEY2!", "SomeEvent");

        Map<String, String> options = new HashMap<String, String>();
        options.put("key1", event.getVar1());
        options.put("key2", Integer.toString(event.getVar2()));


        api.getRandomImage2(options,new Callback<SomeData>() {
            @Override
            public void success(SomeData randomImage, Response response) {
                apiBus.post(new SuccessEvent(randomImage));
            }

            @Override
            public void failure(RetrofitError error) {
                apiBus.post(new FailedEvent());
            }
        });
    }

    @Subscribe public void onGetConversationId(ConversationEvent event) {

        Map<String,Integer> opt = new HashMap<>();
        opt.put("userId",event.userId);
        opt.put("partnerId",event.partnerId);

        String contentType = "application/json";


        api.getConversationId(event.userId,event.partnerId, new Callback<ConversationOneToOne>() {
            @Override
            public void success(ConversationOneToOne conversationOneToOne, Response response) {
                Log.e("conversationOneToOne",conversationOneToOne.id + "");
                apiBus.post(new ConversationEventSuccess(conversationOneToOne.id));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });



    }
}
