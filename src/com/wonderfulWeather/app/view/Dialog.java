package com.wonderfulWeather.app.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by kevin on 2014/12/30.
 *
 * 对话框重写
 *
 */
public class Dialog extends AlertDialog {

    AlertDialog.Builder dialog;

    public Dialog(Context context) {
      super(context);
    }

    public void Init(String title,String content,boolean cancelable,String btnOkText,String btnCancelText,DialogInterface.OnClickListener okListener,DialogInterface.OnClickListener cancelListener)
    {
        if(dialog==null)
        {
            dialog=new AlertDialog.Builder(super.getContext());
        }
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setCancelable(cancelable);
        dialog.setPositiveButton(btnOkText,okListener);
        dialog.setNegativeButton(btnCancelText,cancelListener);
    }

    public void show()
    {
        if(dialog!=null)
            dialog.show();
    }
}
