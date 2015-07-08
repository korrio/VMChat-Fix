package co.aquario.chatapp.picker;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import co.aquario.chatapp.R;

/**
 * Created by Mac on 7/8/15.
 */
public class LocationPickerActivity extends AppCompatActivity {

    private MenuItem menuShareItem;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setTitle(getString(R.string.share_location));
        //Intent intent = new Intent();
        //this.setResult(-1, intent);
        //this.finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_share, menu);
        this.menuShareItem = menu.findItem(R.id.action_share);
        this.menuShareItem.setEnabled(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 16908332) {
            super.onBackPressed();
            return true;
        } else if(item.getItemId() == R.id.action_share) {
            Intent intent = new Intent();
            this.setResult(-1, intent);
            this.finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
