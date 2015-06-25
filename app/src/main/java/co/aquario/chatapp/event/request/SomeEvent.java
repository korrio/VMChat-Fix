package co.aquario.chatapp.event.request;

/**
 * Created by Mac on 3/2/15.
 */
public class SomeEvent {
    private String var1;
    private int var2;

    public SomeEvent(String var1, int var2) {
        this.var1 = var1;
        this.var2 = var2;
    }

    public String getVar1() {
        return var1;
    }

    public int getVar2() {
        return var2;
    }
}
