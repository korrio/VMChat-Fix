package co.aquario.chatapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.jialin.chat.Message;
import com.jialin.chat.MessageAdapter;
import com.jialin.chat.MessageInputToolBox;
import com.jialin.chat.OnOperationListener;
import com.jialin.chat.Option;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLContext;

import co.aquario.chatapp.R;
import co.aquario.chatapp.event.request.ConversationEvent;
import co.aquario.chatapp.event.request.HistoryEvent;
import co.aquario.chatapp.event.response.ConversationEventSuccess;
import co.aquario.chatapp.event.response.HistoryEventSuccess;
import co.aquario.chatapp.handler.ApiBus;
import co.aquario.chatapp.model.ChatMessage;


public class ChatWidgetFragment extends BaseFragment {

    List<Message> mMessages = new ArrayList<>();


    private MessageInputToolBox box;
    private ListView listView;
    private MessageAdapter adapter;

    private int mUserId = 6;
    private String mUsername = "FOOBAR";
    private String mAvatarPath = "https://www.vdomax.com/photos/2015/04/pr8af_108899_c04356ab5e9726bb6e650e5b9cc17cbc.jpg";
    private int mPartnerId = 3082;
    private int mCid = 1751;

    public boolean isConnected = false;

    private String socketUrl = "https://chat.vdomax.com:1313";

    private Socket mSocket;
    {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, null, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        IO.setDefaultSSLContext(sc);
        IO.Options opts = new IO.Options();
        opts.secure = true;
        opts.sslContext = sc;
        try {
            mSocket = IO.socket(socketUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Fragment newInstance(int userId, int partnerId) {
        ChatWidgetFragment mFragment = new ChatWidgetFragment();
        Bundle mBundle = new Bundle();
        mBundle.putInt("USER_ID_1", userId);
        mBundle.putInt("USER_ID_2", partnerId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_widget, container, false);
        listView = (ListView) view.findViewById(R.id.messageListview);
        box = (MessageInputToolBox) view.findViewById(R.id.messageInputToolBox);

        initMessageInputToolBox();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getArguments() != null) {
            mUserId = getArguments().getInt("USER_ID_1");
            mPartnerId = getArguments().getInt("USER_ID_2");
        }

        getActivity().setTitle(mUserId + ":" + mPartnerId);
        ApiBus.getInstance().post(new ConversationEvent(mUserId, mPartnerId));
    }

    @Subscribe
    public void onGetConversation(ConversationEventSuccess event) {
        mCid = event.mCid;
       // Log.e("HEY555", mCid + "");
        //addLog(getResources().getString(R.string.message_welcome));

        getActivity().setTitle(mUserId + ":" + mPartnerId + " in " + mCid);
        ApiBus.getInstance().post(new HistoryEvent(mCid,20,1));

    }

    @Subscribe
    public void onGetHistory(HistoryEventSuccess event) {
        Log.e("HEY666",event.content.size() + "");
        loadHistory(event.content);
        initConnect();
    }

    public void initConnect() {
        if(!isConnected) {
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    //mSocket.emit("OnlineUser");
                    JSONObject jObj = new JSONObject();
                    try {
                        jObj.put("userId", mUserId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mSocket.emit("Authenticate", jObj);
                    //addUser(mUsername); //username
                }
            });
        }


        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("Authenticate:Success", onAuthSuccess);
        mSocket.on("Authenticate:Failure", onAuthFailure);
        mSocket.on("JoinRoomSuccess", onUserJoined);
        //mSocket.on("JoinRoomFailure", null);

        mSocket.on("SendMessage", onSendMessage);
        mSocket.on("LeaveRoom", onUserLeft);
        mSocket.on("Typing", onTyping);
        mSocket.on("StopTyping", onStopTyping);
        //mSocket.on("Read",null);
        //mSocket.on("login" , onLogin);
        mSocket.on("OnlineUser", onOnlineUser);
        mSocket.connect();
    }

    private void initMessageInputToolBox(){

        box.setOnOperationListener(new OnOperationListener() {

            @Override
            public void send(String content) {

                System.out.println("===============" + content);

                Message message = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", content,"{}", true, true, new Date());
                adapter.getData().add(message);
                listView.setSelection(listView.getBottom());

                //Just demo
                //createReplayMsg(message);
            }

            @Override
            public void selectedFace(String content) {

                System.out.println("===============" + content);
                Message message = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS, "Tomcat", "avatar", "Jerry", "avatar", content,"{'tattooUrl':'"+content+"'}", true, true, new Date());
                adapter.getData().add(message);
                listView.setSelection(listView.getBottom());

                //Just demo
                //createReplayMsg(message);
            }


            @Override
            public void selectedFuncation(int index) {

                System.out.println("===============" + index);

                switch (index) {
                    case 0:
                        //do some thing
                        break;
                    case 1:
                        //do some thing
                        break;

                    default:
                        break;
                }

            }

        });

        // Add tattoo
        ArrayList<String> faceNameList5 = new ArrayList<>();
        for(int x = 1; x <= 10; x++){
            faceNameList5.add("https://www.vdomax.com/themes/vdomax1.1/emoticons/tt05/tt05" + String.format("%02d", x) + ".png");
        }

        ArrayList<String> faceNameList4 = new ArrayList<>();
        for(int x = 1; x <= 10; x++){
            faceNameList4.add("https://www.vdomax.com/themes/vdomax1.1/emoticons/tt04/tt04" + String.format("%02d", x) + ".png");
        }

        ArrayList<String> faceNameList3 = new ArrayList<>();
        for(int x = 1; x <= 10; x++){
            faceNameList3.add("https://www.vdomax.com/themes/vdomax1.1/emoticons/tt03/tt03" + String.format("%02d", x) + ".png");
        }

        ArrayList<String> faceNameList2 = new ArrayList<>();
        for(int x = 1; x <= 10; x++){
            faceNameList2.add("https://www.vdomax.com/themes/vdomax1.1/emoticons/tt02/tt02" + String.format("%02d", x) + ".png");
        }

        ArrayList<String> faceNameList1 = new ArrayList<>();
        for(int x = 1; x <= 10; x++){
            faceNameList1.add("https://www.vdomax.com/themes/vdomax1.1/emoticons/tt01/tt01" + String.format("%02d", x) + ".png");
        }

        Map<Integer, ArrayList<String>> faceData = new HashMap<>();
        faceData.put(R.drawable.tt0101, faceNameList1);
        faceData.put(R.drawable.tt0201, faceNameList2);
        faceData.put(R.drawable.tt0301, faceNameList3);
        faceData.put(R.drawable.tt0403, faceNameList4);
        faceData.put(R.drawable.tt0501, faceNameList5);
        box.setFaceData(faceData);


        List<Option> functionData = new ArrayList<Option>();
        for(int x = 0; x < 5; x++){
            Option takePhotoOption = new Option(getActivity(), "Take", R.drawable.tt0501);
            Option galleryOption = new Option(getActivity(), "Gallery", R.drawable.tt0501);
            functionData.add(galleryOption);
            functionData.add(takePhotoOption);
        }
        box.setFunctionData(functionData);
    }



    private void loadHistory(List<ChatMessage> jsonMessages){


        Collections.reverse(jsonMessages);
        for(int i = 0 ; i < jsonMessages.size() ; i ++) {
            ChatMessage m = jsonMessages.get(i);
            Message message;
            boolean isSend = false;
            if(m.senderId == mUserId)
                isSend = true;
            if(m.messageType == 0) {
                message = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.message,m.data, isSend, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
                //addMessage(m.messageType,m.senderId,m.sender.username + "(type:"+m.messageType+")",m.message,m.data);

            } else if(m.messageType == 1) {
                message = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.message,m.data, isSend, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

                //addMessage(m.messageType,m.senderId,m.sender.username + "(type:"+m.messageType+")",m.data,m.data);
            } else {
                message = new Message(Message.MSG_TYPE_PHOTO, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.message,m.data, isSend, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

            }

            mMessages.add(message);




        }

        //create Data
//        Message message = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "Hi", false, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
//        Message message1 = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "Hello World", true, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24)* 8));
//        Message message2 = new Message(Message.MSG_TYPE_PHOTO, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "device_2014_08_21_215311", false, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 7));
//        Message message3 = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "Haha", true, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 7));
//        Message message4 = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "big3", false, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 7));
//        Message message5 = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "big2", true, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 6));
//        Message message6 = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_FAIL, "Tom", "avatar", "Jerry", "avatar", "test send fail", true, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 6));
//        Message message7 = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SENDING, "Tom", "avatar", "Jerry", "avatar", "test sending", true, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 6));


//        messages.add(message);
//        messages.add(message1);
//        messages.add(message2);
//        messages.add(message3);
//        messages.add(message4);
//        messages.add(message5);
//        messages.add(message6);
//        messages.add(message7);

        adapter = new MessageAdapter(getActivity(), mMessages);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                box.hide();
                return false;
            }
        });

    }

    private void createReplayMsg(Message message){

        final Message reMessage = new Message(message.getType(), 1, "Tom", "avatar", "Jerry", "avatar",
                message.getType() == 0 ? "Re:" + message.getContent() : message.getContent(),"{}" ,
                false, true, new Date()
        );
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * (new Random().nextInt(3) +1));
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            adapter.getData().add(reMessage);
                            listView.setSelection(listView.getBottom());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*
* EMITTER
*/
    private Emitter.Listener onAuthSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject jObj = new JSONObject();
            isConnected = true;

            try {
                jObj.put("conversationId" , mCid);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.emit("JoinRoom",jObj);
        }
    };

    private Emitter.Listener onAuthFailure = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onSendMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e("onSendMessage",args.toString());

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    String dataJson;
                    int messageType;
                    int senderId;

                    try {
                        //data.getString("time");
                        Log.e("JSON",data.toString(4));
                        username = mUsername;
                        senderId = data.optInt("senderId");
                        message = data.optString("message");
                        messageType = data.optInt("messageType");
                        dataJson = data.optString("data");


                        if(messageType != 0) {
                            message = message.concat("(" + data.optJSONObject("data").toString(4) + ")");
                        }


                    } catch (JSONException e) {
                        return;
                    }
                    //removeTyping(username);
                    if(mUserId != senderId) {
                        Message msgObj = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, username, mAvatarPath, "", "", message,"{}", false, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
                        mMessages.add(msgObj);
                    }

                        //addMessage(messageType,senderId,username, message,dataJson);
                }
            });
        }
    };

    private Emitter.Listener onOnlineUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            JSONObject data = (JSONObject) args[0];
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];

//                    try {
//                        Toast.makeText(getActivity(),data.toString(4),Toast.LENGTH_LONG).show();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                    //addLog(getResources().getString(R.string.message_user_joined, username));
                    //addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {


            //Log.e("6666",args.toString());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),"เข้าแล้ว",Toast.LENGTH_SHORT).show();
                    //addLog(getResources().getString(R.string.message_user_joined, username));
                    //addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }

                    //addLog(getResources().getString(R.string.message_user_left, username));
                    //addParticipantsLog(numUsers);
                    //removeTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        return;
                    }
                    //addTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        return;
                    }
                    //removeTyping(username);
                }
            });
        }
    };
    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(getActivity() , "onLogin" , Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            //if (!mTyping) return;

            //mTyping = false;
            mSocket.emit("stop typing");
        }
    };
}
