package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by SemTur on 02.10.2017.
 */

public class CERFragment extends Fragment {

    private static final String SAVED_ADAPTER_LIST = "adapter_list";
    private static final String SAVED_CURRENCY_CODE = "currency_code";
    static  final int REQUEST_DATA_SORT = 0;

    private CERData mCERData = CERData.getInstance();
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private String mCurrencyCode;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCurrencyCode = CERPreferences.getCurrencyCode(getActivity());
        View view = inflater.inflate(R.layout.fragment_currency_exchange_rate, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
        if (savedInstanceState != null) {
            mCurrencyCode = savedInstanceState.getString(SAVED_CURRENCY_CODE, CurrencyCode.sUSD);
            mAdapter.mOrganizations = (ArrayList<Organization>) savedInstanceState.getSerializable(SAVED_ADAPTER_LIST);
            mAdapter.notifyDataSetChanged();
        }

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) getActivity()).updateCERData();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_ADAPTER_LIST, mAdapter.mOrganizations);
        outState.putString(SAVED_CURRENCY_CODE, mCurrencyCode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DATA_SORT && resultCode == Activity.RESULT_OK) {
            String sortBy  = data.getStringExtra("sortBy");
            String sortDirection  = data.getStringExtra("sortDirection");
            mCERData.sort(mAdapter.mOrganizations, mCurrencyCode, sortBy, sortDirection, getClass());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CERPreferences.setCurrencyCode(getActivity(), mCurrencyCode);
    }
    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        mCurrencyCode = currencyCode;
        mAdapter.update();
    }

    private class Holder extends RecyclerView.ViewHolder {
        private TextView mTextViewOrgName;
        private TextView mTextViewCurrencyBuy;
        private TextView mTextViewCurrencySale;
        private Organization mOrganization;


        public Holder(View itemView) {
            super(itemView);
            mTextViewOrgName = itemView.findViewById(R.id.textView_bank_name);
            mTextViewCurrencyBuy = itemView.findViewById(R.id.textView_currency_buy);
            mTextViewCurrencySale = itemView.findViewById(R.id.textView_currency_sale);

            optimizeOrgNameScreenSize(mTextViewOrgName);

            mTextViewOrgName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String orgId = mOrganization.getId();
                    if (orgId != null && !orgId.isEmpty()) {
                        if (isTablet()) {
                            FragmentManager fragmentManager = getFragmentManager();
                            Fragment fragment = OrgInfoFragment.newInstance(orgId);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container_org_info, fragment)
                                    .commit();
                        } else {
                            Intent intent = MainActivity.newIntent(getActivity(), MainActivity.sActivityOrgInfoMode, null, orgId);
                            startActivity(intent);
                        }
                    }
                }
            });
        }

        public void bindBank(Organization organization) {
            mOrganization = organization;
            mTextViewOrgName.setTextColor(Color.GRAY);
            mTextViewCurrencyBuy.setTextColor(Color.GRAY);
            mTextViewCurrencyBuy.setTypeface(null, Typeface.NORMAL);
            mTextViewCurrencySale.setTextColor(Color.GRAY);
            mTextViewCurrencySale.setTypeface(null, Typeface.NORMAL);
            String orgName = organization.getName();
            Currency currency = organization.getCurrency(mCurrencyCode);
            mTextViewOrgName.setText(orgName);
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            mTextViewCurrencyBuy.setText(decimalFormat.format(currency.getBuy()));
            mTextViewCurrencySale.setText(decimalFormat.format(currency.getSale()));
            if (orgName.equals(getString(R.string.summary)) || orgName.equals(getString(R.string.nbu_title))) {
                mTextViewOrgName.setTextColor(Color.DKGRAY);
                mTextViewCurrencyBuy.setTextColor(Color.DKGRAY);
                mTextViewCurrencySale.setTextColor(Color.DKGRAY);
                return;
            } else if (orgName.equals(getString(R.string.optimal))) {
                mTextViewOrgName.setTextColor(Color.DKGRAY);
            }
            if (mCERData.isMaxBuyValue(currency.getBuy(), mCurrencyCode)) {
                mTextViewCurrencyBuy.setTextColor(Color.RED);
                mTextViewCurrencyBuy.setTypeface(null, Typeface.BOLD);
            }
            if (mCERData.isMinSaleValue(currency.getSale(), mCurrencyCode)) {
                mTextViewCurrencySale.setTextColor(Color.BLUE);
                mTextViewCurrencySale.setTypeface(null, Typeface.BOLD);
            }

        }

        private void optimizeOrgNameScreenSize(TextView textView) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            ViewGroup.LayoutParams params = textView.getLayoutParams();
            int coef = 1;
            if (isTablet()) {
                coef = 2;
            }
            params.width = (int) (displayMetrics.widthPixels / coef * 0.5);
            textView.setLayoutParams(params);
        }

        private boolean isTablet() {
            return getActivity().findViewById(R.id.fragment_container_org_info) != null;
        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder> {
        ArrayList<Organization> mOrganizations;

        public Adapter() {
            getCERData();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.view_item_currency_exchange_rate, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bindBank(mOrganizations.get(position));
        }

        @Override
        public int getItemCount() {
            return mOrganizations.size();
        }

        public void update() {
            getCERData();
            notifyDataSetChanged();
        }

        private void getCERData() {
            mOrganizations = mCERData.getOrganizations(mCurrencyCode, getString(R.string.optimal), getString(R.string.summary));
        }
    }
}
