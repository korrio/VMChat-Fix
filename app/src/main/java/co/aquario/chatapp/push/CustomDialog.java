package co.aquario.chatapp.push;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import co.aquario.chatapp.R;


public class CustomDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Button btChats, btFreeCall, btVideoCall;
	private TextView textUserName;
	private String friendName;
	private String friendId;
	private String avatar;
	private ImageView imgProfile;

	Context context;

	public CustomDialog(Context context, String frienduserName,
                        String friendID, String avatar) {
		super(context, R.style.Dialog);
		this.friendName = frienduserName;
		this.friendId = friendID;
		this.avatar = avatar;
		this.context = context;

		setContentView(R.layout.custom_dialog);
		initDialog();

		// show();

	}

	void initDialog() {

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(this.getWindow().getAttributes());
		lp.width = 700;
		lp.height = 950;
		this.getWindow().setAttributes(lp);

		setTitle(friendName);
		// getWindow().setLayout(400, 600);
		btChats = (Button) findViewById(R.id.buttonChats);
		btFreeCall = (Button) findViewById(R.id.buttonFreeCall);
		btVideoCall = (Button) findViewById(R.id.buttonVideoCall);

		btChats.setOnClickListener(this);
		btFreeCall.setOnClickListener(this);
		btVideoCall.setOnClickListener(this);
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		dismiss();
		super.cancel();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Toast.makeText(getContext(), friendId + " : " + friendName,
                Toast.LENGTH_LONG).show();
		if (view == btChats) {
		} else if (view == btFreeCall) {
			} else if (view == btVideoCall) {

		

		}
	}

}
