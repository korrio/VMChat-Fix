package co.aquario.chatapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import co.aquario.chatapp.R;


public class DialogUtils {

    private static Toast toast;

    public static void show(Context context, String message) {
        if (message == null) {
            return;
        }
        if (toast == null && context != null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        }
        if (toast != null) {
            toast.setText(message);
            toast.show();
        }
    }

    public static void showLong(Context context, String message) {
        if (message == null) {
            return;
        }
        if (toast == null && context != null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        }
        if (toast != null) {
            toast.setText(message);
            toast.show();
        }
    }

    public static Dialog createDialog(Context context, int titleId, int messageId, DialogInterface.OnClickListener positiveButtonListener,
                                      DialogInterface.OnClickListener negativeButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleId);
        builder.setMessage(messageId);
        builder.setPositiveButton(R.string.dlg_ok, positiveButtonListener);
        builder.setNegativeButton(R.string.dlg_cancel, negativeButtonListener);

        return builder.create();
    }

    public static Dialog createDialog(Context context, int titleId, View view, DialogInterface.OnClickListener positiveClickListener,
                                      DialogInterface.OnClickListener negativeClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleId);
        builder.setView(view);
        builder.setPositiveButton(R.string.dlg_ok, positiveClickListener);
        builder.setNegativeButton(R.string.dlg_cancel, negativeClickListener);

        return builder.create();
    }

    public static Dialog createDialog(Context context, String message, DialogInterface.OnClickListener positiveButtonListener,
            DialogInterface.OnClickListener negativeButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);

        builder.setPositiveButton(R.string.dlg_ok, positiveButtonListener);

        if(negativeButtonListener != null) {
            builder.setNegativeButton(R.string.dlg_cancel, negativeButtonListener);
        }

        return builder.create();
    }

    public static Dialog createSingleChoiceItemsDialog(Context context, String title, CharSequence[] itemsArray, DialogInterface.OnClickListener singleChoiceOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setSingleChoiceItems(itemsArray, -1, singleChoiceOnClickListener);
        return builder.create();
    }
}