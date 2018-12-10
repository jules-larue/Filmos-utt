package com.example.jules.mymovies.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.example.jules.mymovies.R;

public class ConnectionProblemDialog extends AlertDialog {

    private Context mContext;

    public ConnectionProblemDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public void show() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);

        dialogBuilder.setTitle(R.string.dialog_connection_problem_title);
        dialogBuilder.setMessage(R.string.dialog_connection_problem_message);
        dialogBuilder.setPositiveButton(R.string.dialog_connection_problem_ok_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.show();
    }
}
