package co.aquario.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import co.aquario.chatapp.event.ActivityResultEvent;
import co.aquario.chatapp.fragment.ChatWidgetFragment;
import co.aquario.chatapp.handler.ApiBus;


public class ChatActivity extends AppCompatActivity {

    int userId = 6,partnerId = 3082;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        if(getIntent().getExtras() != null) {
            userId = getIntent().getExtras().getInt("USER_ID_1");
            partnerId = getIntent().getExtras().getInt("USER_ID_2");
        }

        Bundle data = new Bundle();
        data.putInt("USER_ID_1", userId);
        data.putInt("USER_ID_2",partnerId);

        ChatWidgetFragment fragment = new ChatWidgetFragment();
        fragment.setArguments(data);
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment, "CHAT_MAIN").addToBackStack(null).commit();
    }

    public static void startChatActivity(Activity mActivity, int userId,int partnetId) {
        Intent i = new Intent(mActivity,ChatActivity.class);
        i.putExtra("USER_ID_1",userId);
        i.putExtra("USER_ID_2",partnetId);
        mActivity.startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ApiBus.getInstance().postQueue(
                new ActivityResultEvent(requestCode, resultCode, data));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_leave:
                // do whatever
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
