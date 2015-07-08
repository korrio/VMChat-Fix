package co.aquario.chatapp.fragment;


import android.content.Intent;
import android.support.v4.app.Fragment;

import com.squareup.otto.Subscribe;

import co.aquario.chatapp.event.ActivityResultEvent;
import co.aquario.chatapp.handler.ApiBus;


public abstract class BaseFragment extends Fragment {

    @Override
    public void onResume() {
        ApiBus.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        ApiBus.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        ApiBus.getInstance().register(mActivityResultSubscriber);
    }

    @Override
    public void onStop() {
        super.onStop();
        ApiBus.getInstance().unregister(mActivityResultSubscriber);
    }

    private Object mActivityResultSubscriber = new Object() {
        @Subscribe
        public void onActivityResultReceived(ActivityResultEvent event) {
            int requestCode = event.getRequestCode();
            int resultCode = event.getResultCode();
            Intent data = event.getData();
            onActivityResult(requestCode, resultCode, data);
        }
    };




}



