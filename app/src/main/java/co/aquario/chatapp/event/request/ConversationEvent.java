package co.aquario.chatapp.event.request;

/**
 * Created by Mac on 3/2/15.
 */
public class ConversationEvent {
    public int userId;
    public int partnerId;

    public ConversationEvent(int userId, int partnerId) {
        this.userId = userId;
        this.partnerId = partnerId;
    }

}
