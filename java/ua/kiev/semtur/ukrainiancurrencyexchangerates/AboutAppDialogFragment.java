package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by SemTur on 09.09.2017.
 */

public class AboutAppDialogFragment extends DialogFragment implements View.OnClickListener {
    static final String TAG = "ua.kiev.semtur.ukrainiancurrencyexchangerates.aboutappdialogfragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_fragment_about_app, null, false);

        TextView textRateApp = view.findViewById(R.id.text_rate_app);
        TextView textNBUCERSource = view.findViewById(R.id.text_nbu_cer_link);
        TextView textCashCERSource = view.findViewById(R.id.text_cash_cer_link);
        TextView textGetPro = view.findViewById(R.id.text_get_pro);
        TextView textNoAds = view.findViewById(R.id.text_no_ads);
        textRateApp.setOnClickListener(this);
        textNBUCERSource.setOnClickListener(this);
        textCashCERSource.setOnClickListener(this);
        textGetPro.setOnClickListener(this);
        textNoAds.setOnClickListener(this);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.close, null)
                .create();
        dialog.setOnShowListener(new CERDialogOnShowListener(getActivity()));
        return dialog;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);;
        switch (view.getId()) {
            case R.id.text_rate_app:
                intent.setData(Uri.parse("market://details?id=" + getActivity().getPackageName()));
                break;
            case R.id.text_nbu_cer_link:
                intent.setData(Uri.parse("https://www.bank.gov.ua"));
                break;
            case R.id.text_cash_cer_link:
                intent.setData(Uri.parse("https://finance.ua/ua/currency"));
                break;
            case R.id.text_get_pro:
            case R.id.text_no_ads:
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=ua.kiev.semtur.ukrainiancurrencyexchangeratesnoad"));
                break;
        }
        startActivity(intent);
    }
}
