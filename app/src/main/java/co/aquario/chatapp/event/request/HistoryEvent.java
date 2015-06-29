package co.aquario.chatapp.event.request;

/**
 * Created by Mac on 6/25/15.
 */
public class HistoryEvent {

    public int cid;
    public int size;
    public int page;

    public HistoryEvent(int cid,int size, int page) {
        this.cid = cid;
        this.size = size;
        this.page = page;
    }
}
