package co.aquario.chatapp.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Mac on 6/25/15.
 */
public class ChatMessage {
    //    "content": [
//    {
//        "id": 2609,
//            "conversationId": 345,
//            "senderId": 6,
//            "message": "fasdfasd",
//            "messageType": 0,
//            "data": {
//        "message": "fasdfasd"
//    },
//        "readCount": 0,
//            "ipAddress": null,
//            "createdAt": "2015-06-25T03:41:38.000Z",
//            "sender": {
//        "senderId": 6,
//                "id": 6,
//                "username": "korrio",
//                "avatar": "photos/2015/04/pr8af_108899_c04356ab5e9726bb6e650e5b9cc17cbc",
//                "extension": "jpg"
//    }
//    },

    @Expose
    public int id;
    @Expose
    public int conversationId;
    @Expose
    public int senderId;
    @Expose
    public String message;
    @Expose
    public int messageType;
    @Expose
    public String data;
    //public MessageData data;
    @Expose
    public String createdAt;
    @Expose
    public ChatUser sender;
}
