package co.aquario.chatapp.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.parse.ParseUser;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLContext;

import co.aquario.chatapp.LandingActivity;
import co.aquario.chatapp.R;
import co.aquario.chatapp.adapter.MessageAdapter;
import co.aquario.chatapp.event.request.ConversationEvent;
import co.aquario.chatapp.event.request.HistoryEvent;
import co.aquario.chatapp.event.response.ConversationEventSuccess;
import co.aquario.chatapp.event.response.HistoryEventSuccess;
import co.aquario.chatapp.handler.ApiBus;
import co.aquario.chatapp.model.ChatMessage;
import co.aquario.chatapp.model.Message;

/**
 * A chat fragment containing messages view and input form.
 */
public class ChatFragment extends BaseFragment {
    private String socketUrl = "https://chat.vdomax.com:1314";
    private static final int REQUEST_LOGIN = 0;
    private static final int TYPING_TIMER_LENGTH = 600;

    private RecyclerView mMessagesView;
    private EditText mInputMessageView;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private int mUserId = 5145;
    private int mPartnerId = 3082;
    private int mCid = 1751;

    private String mUsername = "";
    public boolean isConnected = false;

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

    public ChatFragment() {
    }

    public static Fragment newInstance(int userId, int partnerId) {
        ChatFragment mFragment = new ChatFragment();
        Bundle mBundle = new Bundle();
        mBundle.putInt("USER_ID_1", userId);
        mBundle.putInt("USER_ID_2", partnerId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new MessageAdapter(activity, mMessages);
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
        ApiBus.getInstance().post(new ConversationEvent(mUserId,mPartnerId));
    }

    @Subscribe
    public void onGetConversation(ConversationEventSuccess event) {
        mCid = event.mCid;
        Log.e("HEY555",mCid + "");
        addLog(getResources().getString(R.string.message_welcome));

        getActivity().setTitle(mUserId + ":" + mPartnerId + " in " + mCid);
        ApiBus.getInstance().post(new HistoryEvent(mCid,20,1));

    }

    @Subscribe
    public void onGetHistory(HistoryEventSuccess event) {
        Log.e("HEY666",event.content.size() + "");
        loadHistory(event.content);
        initConnect();
    }

    public void loadHistory(List<ChatMessage> messages) {
        Collections.reverse(messages);
        for(int i = 0 ; i < messages.size() ; i ++) {
            ChatMessage m = messages.get(i);
            if(m.messageType == 0)
                addMessage(m.messageType,m.senderId,m.sender.username + "(type:"+m.messageType+")",m.message,m.data);
            else
                addMessage(m.messageType,m.senderId,m.sender.username + "(type:"+m.messageType+")",m.data,m.data);
        }

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
                    addUser(mUsername); //username
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isConnected = false;
        mSocket.emit("disconnect");
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("SendMessage", onSendMessage);
        mSocket.off("JoinRoom:Success", onUserJoined);
        mSocket.off("LeaveRoom", onUserLeft);
        mSocket.off("Typing", onTyping);
        mSocket.off("StopTyping", onStopTyping);
    }

    @Override
    public void onPause() {
        super.onPause();
        isConnected = false;
        mSocket.emit("disconnect");
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("SendMessage", onSendMessage);
        mSocket.off("JoinRoom:Success", onUserJoined);
        mSocket.off("LeaveRoom", onUserLeft);
        mSocket.off("Typing", onTyping);
        mSocket.off("StopTyping", onStopTyping);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);

        mInputMessageView = (EditText) view.findViewById(R.id.inputMsg);
        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.btnSend || id == EditorInfo.IME_NULL) {
                    attemptSendMessageType0();
                    return true;
                }
                return false;
            }
        });
        mInputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == mUsername) return;
                if (!mSocket.connected()) return;

                if (!mTyping) {
                    mTyping = true;

                    JSONObject jObj = new JSONObject();
                    try {
                        //jObj.put("userId" , mUserId);
                        jObj.put("conversation_id" , mCid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mSocket.emit("Typing",jObj);
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Button sendButton = (Button) view.findViewById(R.id.btnSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSendMessageType0();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            getActivity().finish();
            return;
        }

        mUsername = data.getStringExtra("username");
        int numUsers = data.getIntExtra("numUsers", 1);

        addParticipantsLog(numUsers);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_leave:
                leave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void addUser(String userName){
        mSocket.emit("add user", userName);
    }
    private void addLog(String message) {
        mMessages.add(new Message.Builder(Message.TYPE_LOG)
                .message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addParticipantsLog(int numUsers) {
        addLog(getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
    }

    private void addMessage(int messageType, int userId, String username, String message, String data) {

        if(userId == mUserId)
            mMessages.add(new Message.Builder(Message.TYPE_RIGHT)
                    .messageType(messageType)
                    .username(username)
                    .message(message)
                    .data(data)
                    .build());
        else
            mMessages.add(new Message.Builder(Message.TYPE_LEFT)
                    .messageType(messageType)
                    .username(username)
                    .message(message)
                    .data(data)
                    .build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addTyping(String username) {
        mMessages.add(new Message.Builder(Message.TYPE_LOG)
                .username(username).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void removeTyping(String username) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_LOG && message.getUsername().equals(username)) {
                mMessages.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }

    private void attemptSendMessageType0() {
        if (null == mUsername) return;
        if (!mSocket.connected()) return;

        mTyping = false;

        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }

        mInputMessageView.setText("");

        addMessage(0,mUserId,mUsername, message,"{}");

        JSONObject jObj = new JSONObject();
        JSONObject jObj2 = new JSONObject();

        try {
            jObj2.put("message",message);
            jObj.put("message",message);
            jObj.put("senderId" , mUserId);
            jObj.put("conversationId" , mCid);
            jObj.put("messageType" , 0);
            jObj.put("data" , jObj2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("SendMessage", jObj);
    }

    private void startSignIn() {
        mUsername = null;
        Intent intent = new Intent(getActivity(), LandingActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void leave() {
        mUsername = null;
        mSocket.disconnect();
        mSocket.connect();
        startSignIn();
        ParseUser.logOut();
    }

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
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


                        Log.e("logloglog",mUserId + " " + senderId);
                        if(mUserId != senderId)
                            addMessage(messageType,senderId,username, message,dataJson);


                    } catch (JSONException e) {
                        addLog(e.getMessage().toString());
                        //return;
                    }
                    //removeTyping(username);

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

                    addLog(getResources().getString(R.string.message_user_left, username));
                    addParticipantsLog(numUsers);
                    removeTyping(username);
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
                    addTyping(username);
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
                    removeTyping(username);
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
            if (!mTyping) return;

            mTyping = false;
            mSocket.emit("stop typing");
        }
    };
}

