package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

public class CERDialogOnShowListener implements DialogInterface.OnShowListener {
    private Context mContext;

    public CERDialogOnShowListener(Context context) {
        mContext = context;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
        int color = R.color.colorAccent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            positiveButton.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        } else {
            positiveButton.setTextColor(mContext.getResources().getColor(R.color.colorAccent, null));
        }

    }
}
