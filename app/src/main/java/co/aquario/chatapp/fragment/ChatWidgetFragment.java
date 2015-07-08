package co.aquario.chatapp.fragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.text.MessageFormat;
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
import co.aquario.chatapp.picker.LocationPickerIntent;
import co.aquario.chatapp.util.ChatUtil;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;


public class ChatWidgetFragment extends BaseFragment {

    List<Message> listMessages = new ArrayList<>();
    List<String> optionName = new ArrayList<>();

    private MessageInputToolBox box;
    private ListView listView;
    private MessageAdapter adapter;

    private int mUserId = 6;
    private String mUsername = "FOOBAR";
    private String mAvatarPath = "https://www.vdomax.com/photos/2015/04/pr8af_108899_c04356ab5e9726bb6e650e5b9cc17cbc.jpg";
    private int mPartnerId = 3082;
    private int mCid = 1751;

    public boolean isConnected = false;

    public View rootView;

    private String socketUrl = "https://chat.vdomax.com:1314";
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
        rootView = view;
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

        adapter = new MessageAdapter(getActivity(), listMessages);

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

    private void attemptSendMessageType0(int messageType,String theMessage)  {
        if (null == mUsername) return;
        if (!mSocket.connected()) return;

        //mTyping = false;

        String message = theMessage;
        JSONObject jObj = new JSONObject();
        JSONObject jObj2 = new JSONObject();

        Log.e("SendMessage","yung");

        try {


        if(messageType == 1) {
            jObj2.put("tattooUrl", message);
            jObj.put("message","::tt0101::");
        } else {
            jObj.put("message",message);
        }


        jObj.put("senderId" , mUserId);
        jObj.put("conversationId" , mCid);
        jObj.put("messageType" , messageType);
        jObj.put("data", jObj2);

            Log.e("SendMessage",jObj.toString(4));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("SendMessage","laew");


        mSocket.emit("SendMessage", jObj);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Don't forget to check requestCode before continuing your job
        List<String> photos = null;
        if (requestCode == 100) {
            if (data != null) {
                photos =
                        data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                for(int i = 0 ; i < photos.size() ; i++) {
                    String dataJson = "{'imageUri':'"+photos.get(i)+"'}";
                    Message message = new Message(Message.MSG_TYPE_PHOTO, Message.MSG_STATE_SUCCESS, "aaa", "https://www.vdomax.com/photos/2015/04/pr8af_108899_c04356ab5e9726bb6e650e5b9cc17cbc_thumb.jpg", "", "", "",dataJson, false, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

                    listMessages.add(message);
                }
            }
        } else if(requestCode == 200 || requestCode == 201) {

            Uri mFileURI = data.getData();

            String vdoThumb = ChatUtil.getThumbnailPathForLocalFile(getActivity(),mFileURI);

            String dataJson = "{'imageUrl':'"+vdoThumb+"'}";

            Log.e("thumbthumb",vdoThumb);

            ContentResolver cR = getActivity().getContentResolver();
            String mime = cR.getType(mFileURI);

            Message message = new Message(Message.MSG_TYPE_CLIP, Message.MSG_STATE_SUCCESS, "", "https://www.vdomax.com/photos/2015/04/pr8af_108899_c04356ab5e9726bb6e650e5b9cc17cbc_thumb.jpg", "", "", "",dataJson, false, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

            listMessages.add(message);


        } else if(requestCode == 300) {

        } else if(requestCode == 400) {

        } else if(requestCode == 500) {

        }else if(requestCode == 600) {
            String mapImage = "https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=600x300&maptype=roadmap&markers=color:blue%7Clabel:S%7C40.702147,-74.015794";

            String dataJson = "{'imageUrl':'"+mapImage+"'}";
            Message message = new Message(Message.MSG_TYPE_LOCATION, Message.MSG_STATE_SUCCESS, "", "https://www.vdomax.com/photos/2015/04/pr8af_108899_c04356ab5e9726bb6e650e5b9cc17cbc_thumb.jpg", "", "", "",dataJson, false, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

            listMessages.add(message);
        }

        adapter.notifyDataSetChanged();
        listView.setSelection(listView.getBottom());
    }

    LocationManager mLocManager;
    LocationListener mLocListener;
    GpsStatus.Listener gpsStatusListener;
    Location lastLocation;
    long lastFix;
    boolean hasFix;

    public void viewLocation(){
        if (lastLocation == null){
            return;
        }

        String uri = formatLocation("geo:{0},{1}?q={0},{1}", lastLocation);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.view_via)));
    }

    private String formatLocation(String s, Location l){
        // Hack to get around MessageFormat precision weirdness
        return MessageFormat.format(s, "" + l.getLatitude(), "" + l.getLongitude(), "" + l.getAccuracy());
    }

    public void statusCheck()
    {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();

        }


    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private static final int RESULT_PICK_VIDEO = 200;
    private static final int RESULT_VIDEO_CAP = 201;

    public void pickFile() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, RESULT_PICK_VIDEO);
    }

    public void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, RESULT_VIDEO_CAP);
    }

    private void initMessageInputToolBox(){

        box.setOnOperationListener(new OnOperationListener() {

            @Override
            public void send(String content) {

                System.out.println("===============" + content);

                Message message = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", content,"{}", true, true, new Date());
                adapter.getData().add(message);
                listView.setSelection(listView.getBottom());

                attemptSendMessageType0(Message.MSG_TYPE_TEXT,content);

                //Just demo
                //createReplayMsg(message);


            }

            @Override
            public void selectedFace(String content)  {

                System.out.println("===============" + content);
                Message message = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS, "Tomcat", "avatar", "Jerry", "avatar", content,"{'tattooUrl':'"+content+"'}", true, true, new Date());
                adapter.getData().add(message);
                listView.setSelection(listView.getBottom());

                attemptSendMessageType0(Message.MSG_TYPE_FACE,content);

                //Just demo
                //createReplayMsg(message);
            }


            @Override
            public void selectedFunction(int index) {

                System.out.println("===============" + index);

                switch (index) {
                    case 0:
                        //Snackbar.make(rootView,optionName.get(index) , Snackbar.LENGTH_SHORT).show();
                        PhotoPickerIntent intent = new PhotoPickerIntent(getActivity());
                        intent.setPhotoCount(9);
                        intent.setShowCamera(true);
                        getActivity().startActivityForResult(intent, 100);
                        break;
                    case 1:
                        pickFile();
// Wordpress Media Picker
                        //MediaSourceDeviceVideos fragment = new MediaSourceDeviceVideos();
                        //getSupportFragmentManager().beginTransaction().add(R.id.container, fragment, "CHAT_MAIN").addToBackStack(null).commit();

                        break;
                    case 6:
                        statusCheck();
                        LocationPickerIntent intent6 = new LocationPickerIntent(getActivity());
                        getActivity().startActivityForResult(intent6, 600);

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

        optionName.add(0,getString(R.string.action_photo));
        optionName.add(1,getString(R.string.action_video));
        optionName.add(2,getString(R.string.action_voice));
        optionName.add(3,getString(R.string.action_contact));
        optionName.add(4,getString(R.string.action_music));
        optionName.add(5,getString(R.string.action_youtube));
        optionName.add(6,getString(R.string.action_location));

        List<Option> functionData = new ArrayList<Option>();

        Option choosePhoto = new Option(getActivity(), getString(R.string.action_photo), R.drawable.action_camera);
        Option chooseVideo = new Option(getActivity(), getString(R.string.action_video), R.drawable.action_clip);
        Option recordVoice = new Option(getActivity(), getString(R.string.action_voice), R.drawable.tt0501);
        Option shareContact = new Option(getActivity(), getString(R.string.action_contact), R.drawable.tt0501);
        Option shareMusic = new Option(getActivity(), getString(R.string.action_music), R.drawable.action_music);
        Option shareYoutube = new Option(getActivity(), getString(R.string.action_youtube), R.drawable.tt0501);
        Option shareLocation = new Option(getActivity(), getString(R.string.action_location), R.drawable.action_location);
        //Option shareMusic = new Option(getActivity(), "Contact", R.drawable.tt0501);
        functionData.add(choosePhoto);
        functionData.add(chooseVideo);
        functionData.add(recordVoice);
        functionData.add(shareContact);
        functionData.add(shareMusic);
        functionData.add(shareYoutube);
        functionData.add(shareLocation);
        //functionData.add(shareMusic);

        box.setFunctionData(functionData);
    }

    private void loadHistory(List<ChatMessage> jsonMessages){
        Collections.reverse(jsonMessages);
        for(int i = 0 ; i < jsonMessages.size() ; i ++) {
            ChatMessage m = jsonMessages.get(i);
            Message message = null;
            boolean isSend = false;
            if(m.senderId == mUserId)
                isSend = true;
            if(m.messageType == 0) {

                // 0 = normal message
                message = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.message,m.data, isSend, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
                //addMessage(m.messageType,m.senderId,m.sender.username + "(type:"+m.messageType+")",m.message,m.data);

            } else if(m.messageType == 1) {

                // 1 = tattoo
                message = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.message,m.data, isSend, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

                //addMessage(m.messageType,m.senderId,m.sender.username + "(type:"+m.messageType+")",m.data,m.data);
            } else if(m.messageType == 2) {

                // 2 = image
                message = new Message(Message.MSG_TYPE_PHOTO, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.message,m.data, isSend, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

            } else if(m.messageType == 3){

                // 3 = youtube link
                message = new Message(Message.MSG_TYPE_CLIP, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.data,m.data, isSend, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
            } else if(m.messageType == 31) {

                // 31 = youtube object
                message = new Message(Message.MSG_TYPE_YOUTUBE_OBJ, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.data,m.data, isSend, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
            } else if(m.messageType == 32) {

                // 32 = soundcloud object
                message = new Message(Message.MSG_TYPE_SOUNDCLOUD_OBJ, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.data,m.data, isSend, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
            } else if(m.messageType == 4) {

                // 4 = audio call
                message = new Message(Message.MSG_TYPE_AUDIO_CALL, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.data,m.data, isSend, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
            } else if(m.messageType == 5) {

                // 5 = video call
                message = new Message(Message.MSG_TYPE_VIDEO_CALL, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.data,m.data, isSend, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

            } else if(m.messageType == 6) {

                // 6 = location

                message = new Message(Message.MSG_TYPE_LOCATION, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.data,m.data, isSend, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

            } else if(m.messageType == 7) {

                // 7 = contact

            } else if(m.messageType == 8) {

                // 8 = voice message

            }

            listMessages.add(message);

        }




        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setSelection(listView.getBottom());

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

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Default Signature Fail", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            if(getActivity() == null)
                return;

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
                        listMessages.add(msgObj);
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
