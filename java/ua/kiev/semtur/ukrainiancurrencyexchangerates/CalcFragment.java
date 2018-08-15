package ua.kiev.semtur.ukrainiancurrencyexchangerates;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by SemTur on 30.09.2017.
 */

public class CalcFragment extends Fragment {
    private static final String ARG_CURRENCY_CODE = "currency_code";
    private static final String SAVED_ADAPTER_LIST = "current_adapter_list";
    private static final String SAVED_OPERATION_TYPE = "operation_type";
    private static final String SAVED_CURRENCY_SUM = "currency_sum";
    private static final int REQUEST_DATA_SORT = 1;

    private EditText mEditTextCurrencySum;
    private Spinner mSpinnerCurrencyCode;
    private Spinner mSpinnerOperationType;
    private Button mButtonCalculate;
    private RecyclerView mRecyclerView;
    private CalcAdapter mCalcAdapter;
    private CERData mCERData = CERData.getInstance();
    private String mOperationType;
    private int mCurrencySum;
    private String mCurrencyCode;
    private Context mContext;

    public static CalcFragment newInstance(String currencyCode) {
        Bundle args = new Bundle();
        args.putString(ARG_CURRENCY_CODE, currencyCode);
        CalcFragment fragment = new CalcFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency_calc, container, false);

        mEditTextCurrencySum = view.findViewById(R.id.editText_currency_sum);
        mSpinnerCurrencyCode = view.findViewById(R.id.spinner_currency_code);
        mSpinnerOperationType = view.findViewById(R.id.spinner_operation_type);
        mButtonCalculate = view.findViewById(R.id.button_calculate);

        mButtonCalculate.setVisibility(View.INVISIBLE);

        mEditTextCurrencySum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEditTextCurrencySum.getText().length() == 0) {
                    mButtonCalculate.setVisibility(View.INVISIBLE);
                } else {
                    mCurrencySum = Integer.parseInt(mEditTextCurrencySum.getText().toString());
                    mButtonCalculate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mButtonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getViewParams();
                mCalcAdapter = new CalcAdapter();
                mRecyclerView.setAdapter(mCalcAdapter);
                getActivity().invalidateOptionsMenu();
            }
        });
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadInstanceState(savedInstanceState);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getViewParams();
        if (mCalcAdapter != null) {
            outState.putSerializable(SAVED_ADAPTER_LIST, mCalcAdapter.mOrganizations);
        }
        outState.putString(SAVED_OPERATION_TYPE, mOperationType);
        outState.putString(ARG_CURRENCY_CODE, mCurrencyCode);
        outState.putInt(SAVED_CURRENCY_SUM, mCurrencySum);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DATA_SORT && resultCode == Activity.RESULT_OK) {
            String sortBy  = data.getStringExtra("sortBy");
            String sortDirection  = data.getStringExtra("sortDirection");
            if (sortBy.equals("exchange_rate")) {
                getViewParams();
                sortBy = mOperationType;
            }
            mCERData.sort(mCalcAdapter.mOrganizations, mCurrencyCode, sortBy, sortDirection, getClass());
            mCalcAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CERPreferences.setCalcOperationType(mContext, mOperationType);
        CERPreferences.setCalcCurrencySum(mContext, mCurrencySum);
        CERPreferences.setCurrencyCode(mContext, mCurrencyCode);
    }

    public boolean isCalcDataNotDisplayed() {
        return mCalcAdapter == null || mCalcAdapter.getItemCount() == 0;
    }

    private void optimizeOrgNameScreenSize(TextView textView) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams params = textView.getLayoutParams();
        params.width = (int) (0.55 * displayMetrics.widthPixels);
        textView.setLayoutParams(params);
    }

    private void loadInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mOperationType = CERPreferences.getCalcOperationType(mContext);
            mCurrencySum = CERPreferences.getCalcCurrencySum(mContext);
            mCurrencyCode = CERPreferences.getCurrencyCode(mContext);
        } else {
            ArrayList<Organization> organizations = (ArrayList<Organization>) savedInstanceState.getSerializable(SAVED_ADAPTER_LIST);
            if (organizations != null) {
                mCalcAdapter = new CalcAdapter();
                mCalcAdapter.mOrganizations = organizations;
                int size = mCalcAdapter.getItemCount();
                mRecyclerView.setAdapter(mCalcAdapter);
            }
            mOperationType = savedInstanceState.getString(SAVED_OPERATION_TYPE);
            mCurrencySum = savedInstanceState.getInt(SAVED_CURRENCY_SUM);
            mCurrencyCode = savedInstanceState.getString(ARG_CURRENCY_CODE);
        }
        if (mOperationType.equals(OperationType.sSale)) {
            mSpinnerOperationType.setSelection(0);
        } else {
            mSpinnerOperationType.setSelection(1);
        }
        mEditTextCurrencySum.setText(String.valueOf(mCurrencySum));

        String currencyCode = getArguments().getString(ARG_CURRENCY_CODE);
        if (currencyCode != null && !currencyCode.isEmpty()) {
            mCurrencyCode = currencyCode;
        }
        setSpinnerCurrencyCodeSelection();
    }

    private void setSpinnerCurrencyCodeSelection() {
        switch (mCurrencyCode) {
            case CurrencyCode.sUSD:
                mSpinnerCurrencyCode.setSelection(0);
                break;
            case CurrencyCode.sEUR:
                mSpinnerCurrencyCode.setSelection(1);
                break;
            case CurrencyCode.sGBP:
                mSpinnerCurrencyCode.setSelection(2);
                break;
            case CurrencyCode.sPLN:
                mSpinnerCurrencyCode.setSelection(3);
                break;
            case CurrencyCode.sRUB:
                mSpinnerCurrencyCode.setSelection(4);
                break;
        }
    }

    private void getViewParams() {
        if (mSpinnerOperationType.getSelectedItemPosition() == 0) {
            mOperationType = OperationType.sSale;
        } else {
            mOperationType = OperationType.sBuy;
        }
        mCurrencyCode = mSpinnerCurrencyCode.getSelectedItem().toString();
    }

    private class CalcHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewOrgName;
        private TextView mTextViewCurrencySum;
        private Organization mOrganization;

        public CalcHolder(View itemView) {
            super(itemView);
            mTextViewOrgName = itemView.findViewById(R.id.textView_bank_name);
            mTextViewCurrencySum = itemView.findViewById(R.id.textView_currency_sum);

            optimizeOrgNameScreenSize(mTextViewOrgName);

            mTextViewOrgName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String orgId = mOrganization.getId();
                    if (orgId != null && !orgId.isEmpty()) {
                        Intent intent = MainActivity.newIntent(getActivity(), MainActivity.sActivityOrgInfoMode, null, orgId);
                        startActivity(intent);
                    }
                }
            });
        }

        public void bindBankCER(Organization organization) {
            mOrganization = organization;
            mTextViewOrgName.setTextColor(Color.GRAY);
            mTextViewCurrencySum.setTextColor(Color.GRAY);
            mTextViewCurrencySum.setTypeface(null, Typeface.NORMAL);
            mTextViewOrgName.setText(organization.getName());
            Currency currency = organization.getCurrency(mCurrencyCode);
            double currencySum;
            if (mOperationType.equals(OperationType.sBuy)) {
                currencySum = mCurrencySum * currency.getBuy();
                if (mCERData.isMaxBuyValue(currency.getBuy(), mCurrencyCode)) {
                    mTextViewCurrencySum.setTextColor(Color.RED);
                    mTextViewCurrencySum.setTypeface(null, Typeface.BOLD);
                }
            } else {
                currencySum = mCurrencySum * currency.getSale();
                if (mCERData.isMinSaleValue(currency.getSale(), mCurrencyCode)) {
                    mTextViewCurrencySum.setTextColor(Color.BLUE);
                    mTextViewCurrencySum.setTypeface(null, Typeface.BOLD);
                }
            }
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            mTextViewCurrencySum.setText(decimalFormat.format(currencySum) + " грн");
            if (organization.getId().equals("nbu")) {
                mTextViewOrgName.setTextColor(Color.DKGRAY);
                mTextViewCurrencySum.setTextColor(Color.DKGRAY);
                mTextViewCurrencySum.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    private class CalcAdapter extends RecyclerView.Adapter<CalcHolder> {
        private ArrayList<Organization> mOrganizations = mCERData.getOrganizations(mCurrencyCode);;

        @Override
        public CalcHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.view_item_currency_calculator, parent, false);
            return new CalcHolder(view);
        }

        @Override
        public void onBindViewHolder(CalcHolder holder, int position) {
            holder.bindBankCER(mOrganizations.get(position));
        }

        @Override
        public int getItemCount() {
            return mOrganizations.size();
        }
    }
}
