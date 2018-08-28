package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    static final String sActivityCalcMode = "main_activity_calc_mode";
    static final String sActivityOrgInfoMode = "main_activity_org_info_mode";

    private static final String SAVED_IS_CER_DOWNLOAD_STARTED = "is_cer_download_started";
    private static final String SAVED_IS_CER_DATA_DOWNLOADED = "is_cer_data_downloaded";
    private static final String EXTRA_ACTIVITY_MODE = "main_activity_mode";
    private static final String EXTRA_CURRENCY_CODE = "currency_code";
    private static final String EXTRA_ORG_ID = "org_id";

    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private boolean mIsCERDownloadStarted;
    private boolean mIsCERDataDownloaded;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mIsCERDataDownloaded = intent.getBooleanExtra(CERDownloadService.EXTRA_IS_DATA_DOWNLOADED, false);
            mIsCERDownloadStarted = false;
            Fragment fragment;
            if (mIsCERDataDownloaded) {
                if (mActivityMode != null && mActivityMode.equals(sActivityCalcMode)) {
                    fragment = CalcFragment.newInstance(CERPreferences.getCurrencyCode(context));
                } else {
                    mIsCERDownloadStarted = false;
                    getSupportActionBar().setSubtitle(CERData.getInstance().getDate());
                    fragment = new CERFragment();
                }
            } else {
                fragment = StartScreenFragment.newInstance(mIsCERDataDownloaded);
            }
            mFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            invalidateOptionsMenu();
        }
    };

    private String mActivityMode;

    public static Intent newIntent(Context context, String activityMode, String currencyCode, String orgId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_MODE, activityMode);
        if (currencyCode != null) {
            intent.putExtra(EXTRA_CURRENCY_CODE, currencyCode);
        }
        if (orgId != null) {
            intent.putExtra(EXTRA_ORG_ID, orgId);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_exchange_rate);
        mActivityMode = getIntent().getStringExtra(EXTRA_ACTIVITY_MODE);

        if (savedInstanceState != null) {
            mIsCERDownloadStarted = savedInstanceState.getBoolean(SAVED_IS_CER_DOWNLOAD_STARTED, false);
            mIsCERDataDownloaded = savedInstanceState.getBoolean(SAVED_IS_CER_DATA_DOWNLOADED, false);
            mActivityMode = savedInstanceState.getString(EXTRA_ACTIVITY_MODE);
        }

        AdLoader.loadAd(this, R.id.adView_activity_cer);

        if (!mIsCERDataDownloaded) {
            updateCERData();
        } else if (mActivityMode == null) {
            HelpDialogFragment.checkFirstAppStart(this);
            getSupportActionBar().setSubtitle(CERData.getInstance().getDate());
            showFragment(CERFragment.class);
            RateAppDialogFragment.showRateAppDialog(this);
        } else if (mActivityMode.equals(sActivityCalcMode)) {
            setTitle(R.string.currency_calc);
            showFragment(CalcFragment.class);
        } else if (mActivityMode.equals(sActivityOrgInfoMode)) {
            showFragment(OrgInfoFragment.class);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(CERDownloadService.ACTION_SEND_CER_DATA);
        registerReceiver(mBroadcastReceiver, intentFilter, CERDownloadService.PERM_CASH_EXCHANGE_RATES, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem currency = menu.findItem(R.id.menu_item_currency);
        MenuItem currencyCalc = menu.findItem(R.id.menu_item_currancy_calc);
        MenuItem dataSort = menu.findItem(R.id.menu_item_data_sort);
        MenuItem updateData = menu.findItem(R.id.menu_item_update_data);

        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);

        if (mActivityMode == null) {
            if (fragment.getClass().equals(CERFragment.class)) {
                String currencyCode = ((CERFragment) fragment).getCurrencyCode();
                currency.setTitle(currencyCode);
                switch (currencyCode) {
                    case CurrencyCode.sUSD:
                        currency.setIcon(R.drawable.usd);
                        break;
                    case CurrencyCode.sEUR:
                        currency.setIcon(R.drawable.eur);
                        break;
                    case CurrencyCode.sGBP:
                        currency.setIcon(R.drawable.gbp);
                        break;
                    case CurrencyCode.sPLN:
                        currency.setIcon(R.drawable.pln);
                        break;
                    case CurrencyCode.sRUB:
                        currency.setIcon(R.drawable.rub);
                        break;
                }
            } else {
                currency.setVisible(false);
                currencyCalc.setVisible(false);
                dataSort.setVisible(false);
                return super.onCreateOptionsMenu(menu);
            }
        } else if (mActivityMode.equals(sActivityCalcMode)) {
            currency.setVisible(false);
            currencyCalc.setVisible(false);
            boolean isCalcDataNotDisplayed = fragment.getClass().equals(CalcFragment.class) && ((CalcFragment) fragment).isCalcDataNotDisplayed();
            if (isCalcDataNotDisplayed) {
                dataSort.setVisible(false);
                updateData.setVisible(false);
            }
        } else if (mActivityMode.equals(sActivityOrgInfoMode)) {
            currency.setVisible(false);
            currencyCalc.setVisible(false);
            dataSort.setVisible(false);
            updateData.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
        switch (item.getItemId()) {
            case R.id.menu_item_currency_usd:
                ((CERFragment) fragment).setCurrencyCode(CurrencyCode.sUSD);
                invalidateOptionsMenu();
                return true;
            case R.id.menu_item_currency_eur:
                ((CERFragment) fragment).setCurrencyCode(CurrencyCode.sEUR);
                invalidateOptionsMenu();
                return true;
            case R.id.menu_item_currency_gbp:
                ((CERFragment) fragment).setCurrencyCode(CurrencyCode.sGBP);
                invalidateOptionsMenu();
                return true;
            case R.id.menu_item_currency_pln:
                ((CERFragment) fragment).setCurrencyCode(CurrencyCode.sPLN);
                invalidateOptionsMenu();
                return true;
            case R.id.menu_item_currency_rub:
                ((CERFragment) fragment).setCurrencyCode(CurrencyCode.sRUB);
                invalidateOptionsMenu();
                return true;
            case R.id.menu_item_data_sort:
                int requestCode = 0;
                if (mActivityMode != null && mActivityMode.equals(MainActivity.sActivityCalcMode)) {
                    requestCode = 1;
                }
                DialogFragment dialog = new DataSortDialogFragment();
                dialog.setTargetFragment(fragment, requestCode);
                dialog.show(mFragmentManager, DataSortDialogFragment.TAG);
                return true;
            case R.id.menu_item_update_data:
                updateCERData();
                return true;
            case R.id.menu_item_currancy_calc:
                String currencyCode = ((CERFragment) fragment).getCurrencyCode();
                startActivity(MainActivity.newIntent(this, MainActivity.sActivityCalcMode, currencyCode, null));
                return true;
            case R.id.menu_item_help:
                new HelpDialogFragment().show(mFragmentManager, HelpDialogFragment.TAG);
                return true;
            case R.id.menu_item_about_app:
                new AboutAppDialogFragment().show(mFragmentManager, AboutAppDialogFragment.TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SAVED_IS_CER_DOWNLOAD_STARTED, mIsCERDownloadStarted);
        outState.putBoolean(SAVED_IS_CER_DATA_DOWNLOADED, mIsCERDataDownloaded);
        if (mActivityMode != null) {
            outState.putString(EXTRA_ACTIVITY_MODE, mActivityMode);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }

    protected void updateCERData() {
        if (mIsCERDownloadStarted) {
            return;
        }
        Intent intent = CERDownloadService.newIntent(this);
        startService(intent);
        mIsCERDownloadStarted = true;
        mIsCERDataDownloaded = false;
        showFragment(StartScreenFragment.class);
    }

    private void showFragment(Class fClass) {
        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment != null && fClass.equals(fragment.getClass())) {
            return;
        }
        if (fClass.equals(StartScreenFragment.class)) {
            fragment = StartScreenFragment.newInstance(mIsCERDownloadStarted);
        }
        if (fClass.equals(CERFragment.class)) {
            fragment = new CERFragment();
        }
        if (fClass.equals(CalcFragment.class)) {
            String currencyCode = getIntent().getStringExtra(EXTRA_CURRENCY_CODE);
            fragment = CalcFragment.newInstance(currencyCode);
        }
        if (fClass.equals(OrgInfoFragment.class)) {
            String orgId = getIntent().getStringExtra(EXTRA_ORG_ID);
            fragment = OrgInfoFragment.newInstance(orgId);
        }
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
