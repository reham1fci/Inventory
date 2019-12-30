package com.max.rm.inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by Eng.Reham Mokhtar on 28/5
 */

public class Dialog {
     public static void window(String msg, Activity activity){
         final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
         alertDialog.setMessage(msg);
         alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 alertDialog.dismiss();
             }
         });
       alertDialog.show();
     }
    public static void dialogOkCancel(String message, final dialog_interface click, Activity activity){
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
       // alertDialog.setTitle("انتبه");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        click.onDialogOkClick(alertDialog);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,activity.getString(R.string.cancel) , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                click.onDialogCancelClick(alertDialog);

            }
        });

        alertDialog.show();
    }
}
