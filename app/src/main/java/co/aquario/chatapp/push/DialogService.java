package co.aquario.chatapp.push;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class DialogService extends Service {
	Context mContext = this;
	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent arg0) {
		return true;
	}

	public class LocalBinder extends Binder {
		public DialogService getService() {
			return DialogService.this;
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	public void createDialogIn(final long time) {
		Runnable myRun = new Runnable(){
			public void run() {
				try {
					synchronized(this){
						wait(time);//WAIT 1 MINUTE THEN SEND NOTIFICATION
						//THIS IS ALL JUST FOR TESTING
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log.v("DialogService", "SENDING NOTIFICATION");
				SuperDialog.createDialog(SuperDialog.DIALOG_ERROR, mContext);
			}
		};
		Thread T = new Thread(myRun);
		T.start();
	}
}
