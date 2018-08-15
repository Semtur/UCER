package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

/**
 * Created by SemTur on 12.11.2017.
 */

public class DataSortDialogFragment extends DialogFragment {
    static final String TAG = "ua.kiev.semtur.ukrainiancurrencyexchangerates.datasortdialogfragment";

    private RadioButton mButtonSortByOrgName;
    private RadioButton mButtonSortByBuy;
    private RadioButton mButtonSortAZ;

    private Class mTargetFragmentClass;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view;
        mTargetFragmentClass = getTargetFragment().getClass();
        if (mTargetFragmentClass.equals(CERFragment.class)) {
            view = inflater.inflate(R.layout.dialog_fragment_cer_data_sort, null, false);
            mButtonSortByBuy = view.findViewById(R.id.radioButton_sort_by_buy);
        } else if (mTargetFragmentClass.equals(CalcFragment.class)) {
            view = inflater.inflate(R.layout.dialog_fragment_calc_data_sort, null, false);
        } else {
            return null;
        }

        mButtonSortByOrgName = view.findViewById(R.id.radioButton_sort_by_org_name);
        mButtonSortAZ = view.findViewById(R.id.radioButton_sort_az);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.choose_sort_settings)
                .setView(view)
                .setPositiveButton(R.string.sort, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sortBy = null;
                        String sortDirection;
                        if (mButtonSortByOrgName.isChecked()) {
                            sortBy = "org_name";
                        } else if (mTargetFragmentClass.equals(CERFragment.class)) {
                            if (mButtonSortByBuy.isChecked()) {
                                sortBy = "buy";
                            } else {
                                sortBy = "sale";
                            }
                        } else if (mTargetFragmentClass.equals(CalcFragment.class)) {
                            sortBy = "exchange_rate";
                        }
                        if (mButtonSortAZ.isChecked()) {
                            sortDirection = "az";
                        } else {
                            sortDirection = "za";
                        }
                        sendResult(Activity.RESULT_OK, sortBy, sortDirection);
                    }
                })
                .create();
        dialog.setOnShowListener(new CERDialogOnShowListener(getActivity()));
        return dialog;
    }

    private void sendResult(int resultCode, String sortBy, String sortDirection) {
        Intent intent = new Intent();
        intent.putExtra("sortBy", sortBy);
        intent.putExtra("sortDirection", sortDirection);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
