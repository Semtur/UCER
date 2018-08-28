package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by SemTur on 02.10.2017.
 */

public class CERDownloadService extends IntentService {
    static final String ACTION_SEND_CER_DATA = "ua.kiev.semtur.ukrainiancurrencyexchangerates.SEND_CER_DATA";
    static final String PERM_CASH_EXCHANGE_RATES = "ua.kiev.semtur.ukrainiancurrencyexchangerates.CASH_EXCHANGE_RATES";
    static final String EXTRA_IS_DATA_DOWNLOADED = "ua.kiev.semtur.ukrainiancurrencyexchangerates.IS_DATA_DOWNLOADED";

    private static final String sFinanceUaUa = "http://resources.finance.ua/ua/public/currency-cash.json";
    private static final String sFinanceUaRu = "http://resources.finance.ua/ru/public/currency-cash.json";
    private static final String sNBUExchangeRates = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?date=";

    // Поля для відображення повідомленнь на екрані гаджета
    private Handler mHandlerPostMessage;
    private CharSequence mToastText;
    private Runnable mRunnablePostMessage = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), mToastText, Toast.LENGTH_SHORT).show();
        }
    };

    private CERData mCERData;

    public CERDownloadService() {
        super("ua.kiev.semtur.ukrainiancurrencyexchangerates.downloadservice");
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, CERDownloadService.class);
        return intent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mCERData = CERData.getInstance();
        mHandlerPostMessage = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAnConnected()) {
            mToastText = getText(R.string.network_issues);
            mHandlerPostMessage.post(mRunnablePostMessage);
            sendMessage(false);
            return;
        }
        try {
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMMdd");
            String date = sdf.format(java.lang.System.currentTimeMillis());
            byte[] data = getCurrencyExchangeRatesData(sNBUExchangeRates + date + "&json");
            String jsonString = new String(data);
            parseJsonNBU(jsonString);
            String appLang = Locale.getDefault().getLanguage();
            if (appLang.equals("ru")) {
                data = getCurrencyExchangeRatesData(sFinanceUaRu);
            } else {
                data = getCurrencyExchangeRatesData(sFinanceUaUa);
            }
            jsonString = new String(data);
            parseJson(jsonString);
            sendMessage(true);
        } catch (IOException e) {
            mToastText = e.toString();
            mHandlerPostMessage.post(mRunnablePostMessage);
            sendMessage(false);
            return;
        } catch (JSONException e) {
            mToastText = e.toString();
            mHandlerPostMessage.post(mRunnablePostMessage);
        }
    }

    private byte[] getCurrencyExchangeRatesData(String s) throws IOException {
        URL url = new URL(s);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + s);
            }
            int readBytes;
            byte[] buffer = new byte[1024];
            while ((readBytes = in.read(buffer)) > 0) {
                out.write(buffer, 0, readBytes);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private void parseJsonNBU(String jsonString) throws JSONException {
        Organization nbu = new Organization();
        nbu.setId("nbu");
        nbu.setName(getString(R.string.nbu_title));
        nbu.setOrgType(0);
        nbu.setRegion(getString(R.string.nbu_region));
        nbu.setCity(getString(R.string.nbu_city));
        nbu.setAddress(getString(R.string.nbu_address));
        nbu.setPhone(getString(R.string.nbu_phone));
        nbu.setLink("https://bank.gov.ua/control/uk/curmetal/detail/currency?period=daily");
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String currencyCode = jsonObject.getString("cc");
            switch (currencyCode) {
                case CurrencyCode.sUSD:
                case CurrencyCode.sEUR:
                case CurrencyCode.sGBP:
                case CurrencyCode.sPLN:
                case CurrencyCode.sRUB:
                    double rate = jsonObject.getDouble("rate");
                    Currency currency = new Currency(currencyCode, rate, rate);
                    nbu.addCurrency(currency);
                    break;
            }
        }
        mCERData.addOrganization("nbu", nbu);
    }

    private void parseJson(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        mCERData.setDate(jsonObject.getString("date"));
        JSONArray jsonArrayOrgs = jsonObject.getJSONArray("organizations");
        for (int i = 0; i < jsonArrayOrgs.length(); i++) {
            JSONObject jsonObjectOrg = jsonArrayOrgs.getJSONObject(i);

            Organization organization = new Organization();
            organization.setId(jsonObjectOrg.getString("id"));
            organization.setOrgType(jsonObjectOrg.getInt("orgType"));
            organization.setName(jsonObjectOrg.getString("title"));
            organization.setRegion(jsonObjectOrg.getString("regionId"));
            organization.setCity(jsonObjectOrg.getString("cityId"));
            organization.setAddress(jsonObjectOrg.getString("address"));
            organization.setPhone(jsonObjectOrg.getString("phone"));
            organization.setLink(getOrgLinkFromJson(jsonObjectOrg));

            JSONObject jsonObjectCurrencies = jsonObjectOrg.getJSONObject("currencies");
            organization.addCurrency(getCurrencyFromJson(jsonObjectCurrencies, CurrencyCode.sUSD));
            organization.addCurrency(getCurrencyFromJson(jsonObjectCurrencies, CurrencyCode.sEUR));
            organization.addCurrency(getCurrencyFromJson(jsonObjectCurrencies, CurrencyCode.sGBP));
            organization.addCurrency(getCurrencyFromJson(jsonObjectCurrencies, CurrencyCode.sPLN));
            organization.addCurrency(getCurrencyFromJson(jsonObjectCurrencies, CurrencyCode.sRUB));

            mCERData.addOrganization(organization.getId(), organization);
        }
        getRegionsOrCitesFromJson(jsonObject.getJSONObject("regions"), "regions");
        getRegionsOrCitesFromJson(jsonObject.getJSONObject("cities"), "cities");
    }

    private String getOrgLinkFromJson(JSONObject jsonObjectOrg) throws JSONException {
        StringBuilder orgLink = new StringBuilder(jsonObjectOrg.getString("link"));
        orgLink.delete(orgLink.length() - 5, orgLink.length());
        return orgLink.toString();
    }


    private Currency getCurrencyFromJson(JSONObject jsonObject, String currencyCode) throws JSONException {
        if (jsonObject.has(currencyCode)) {
            JSONObject jsonCurrency = jsonObject.getJSONObject(currencyCode);
            double buy = jsonCurrency.getDouble("ask");
            double sale = jsonCurrency.getDouble("bid");
            return new Currency(currencyCode, buy, sale);
        }
        return null;
    }

    private boolean isNetworkAvailableAnConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

    private void sendMessage(boolean isDownloadFinishedSuccessful) {
        Intent intent = new Intent(ACTION_SEND_CER_DATA);
        intent.putExtra(EXTRA_IS_DATA_DOWNLOADED, isDownloadFinishedSuccessful);
        sendBroadcast(intent, PERM_CASH_EXCHANGE_RATES);
    }

    private void getRegionsOrCitesFromJson(JSONObject jsonObject, String regionsOrCities) throws JSONException {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String data = jsonObject.getString(key);
            if (regionsOrCities.equals("regions")) {
                mCERData.addRegion(key, data);
            } else if (regionsOrCities.equals("cities")) {
                mCERData.addCity(key, data);
            }
        }
    }
}
