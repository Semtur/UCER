package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by semtur on 17.01.2018.
 */

public class HelpDialogFragment extends DialogFragment {
    static final String TAG = "ua.kiev.semtur.ukrainiancurrencyexchangerates.helpdialogfragment";

    static final void checkFirstAppStart(AppCompatActivity activity) {
         if (CERPreferences.isFirstAppStart(activity)) {
             DialogFragment fragment = new HelpDialogFragment();
             fragment.show(activity.getSupportFragmentManager(), TAG);
         }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_fragment_help, null, false);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Activity activity = getActivity();
                        if (CERPreferences.isFirstAppStart(activity)) {
                            CERPreferences.setFirstAppStart(activity, false);
                        }
                    }
                })
                .create();
        dialog.setOnShowListener(new CERDialogOnShowListener(getActivity()));
        return dialog;
    }
}
