package co.aquario.chatapp.event.response;

import java.util.List;

import co.aquario.chatapp.model.ChatMessage;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class HistoryDataResponse {

    public List<ChatMessage> content;
    public HistoryDataResponse(List<ChatMessage> content) {
        this.content = content;
    }

}
