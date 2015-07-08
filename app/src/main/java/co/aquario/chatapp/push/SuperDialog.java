package co.aquario.chatapp.push;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


/**
 * @author Sherif
 * 
 * Use this class to create Dialogs
 * 
 * Add as much dialogs as you want
 * 
 * Finally call SuperDialog.createDialog to create a dialog from anywhere
 *
 */
public class SuperDialog extends Activity implements DialogInterface.OnCancelListener {

	//Edit these : Add as much dialogs as you want
	public final static int DIALOG_1 = 0;
	public final static int DIALOG_2 = 1;
	public final static int DIALOG_3 = 2;
	public final static int DIALOG_4 = 3;
	public final static int DIALOG_ERROR = 4;
	
	//Now edit this function
    protected Dialog onCreateDialog(int id) {
		AlertDialog dialog;
		switch(id) {
		case DIALOG_1:
			//create dialog1
			dialog = null;
		break;
		case DIALOG_2:
			//create dialog2
			dialog = null;
			break;
		case DIALOG_3:
			//create dialog3
			dialog = null;
			break;
		case DIALOG_4:
			//create dialog4
			dialog = null;
			break;
		case DIALOG_ERROR:
			//create dialog
			//dialog = new AlertDialog.Builder(this).setMessage("ERROR! This is a global dialog\n Brought to you by Sherif").setTitle("ฮะอะไรนะ").create();
			dialog = null;
			CustomDialog custom = new CustomDialog(this,
					"Mr.A Said", "21",
					"/path");
			custom.setOnCancelListener(this);
			custom.show();
			break;
		default:
			//create a default dialog
			dialog = null;
		}
		//dialog.setOnCancelListener(this);
		return dialog;
	}
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDialog(this.getIntent().getExtras().getInt("dialog"));
    }
	@Override
	public void onCancel(DialogInterface arg0) {
		// THIS IS VERY IMPORTANT TO REMOVE THE ACTIVITY WHEN THE DIALOG IS DISMISSED
		// IF NOT ADDED USER SCREEN WILL NOT RECEIVE ANY EVENTS BEFORE USER PRESSES BACK
		finish();
	}
    /**
     * @author Sherif
     * @param dialog The dialog you want to show
     * @param context The context from which you are calling
     * 
     * this function will create a global dialog for you
     * the dialog will appear no matter which activity or screen is showing
     */
    public static void createDialog(int dialog, Context context){
    	Intent myIntent = new Intent(context, SuperDialog.class);
    	Bundle bundle = new Bundle();
    	bundle.putInt("dialog", dialog);
    	myIntent.putExtras(bundle);
    	myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(myIntent);
    }
}