package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by SemTur on 14.09.2017.
 */

public class RateAppDialogFragment extends DialogFragment {
    static final long sDelayedReminderTime = 1000 * 3600 * 24 * 5; // Delayed reminder time is seven days (in milliseconds)

    public static void showRateAppDialog(AppCompatActivity activity) {
        long reminderDate = CERPreferences.getRateAppReminderDate(activity);
        if (reminderDate == -1) {
            String curAppVer = activity.getString(R.string.app_version);
            String appVer = CERPreferences.getAppVersion(activity);
            if (curAppVer.equals(appVer)) {
                return;
            } else {
                reminderDate = System.currentTimeMillis() + RateAppDialogFragment.sDelayedReminderTime;
                CERPreferences.setRateAppReminderDate(activity, reminderDate);
            }
        } else if (reminderDate == 0) {
            reminderDate = System.currentTimeMillis() + RateAppDialogFragment.sDelayedReminderTime;
            CERPreferences.setRateAppReminderDate(activity, reminderDate);
            return;
        } else if (reminderDate <= System.currentTimeMillis()) {
            FragmentManager fm = activity.getSupportFragmentManager();
            RateAppDialogFragment dialog = new RateAppDialogFragment();
            dialog.show(fm, "RateAppDialog");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_fragment_rate_app, null, false);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.please_rate)
                .setView(view)
                .setPositiveButton(R.string.rate_it_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CERPreferences.setRateAppReminderDate(getActivity(), -1);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=" + getActivity().getPackageName()));
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.remind_me_later, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        long  reminderDate = System.currentTimeMillis() + sDelayedReminderTime;
                        CERPreferences.setRateAppReminderDate(getActivity(), reminderDate);
                    }
                })
                .setNeutralButton(R.string.never_remind, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CERPreferences.setRateAppReminderDate(getActivity(), -1);
                        dismiss();
                    }
                })
                .create();

        Activity a = getActivity();
        CERPreferences.setAppVersion(a, a.getString(R.string.app_version));

        return dialog;
    }
}
