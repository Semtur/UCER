package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by SemTur on 09.09.2017.
 */

public class CERPreferences {
    private static final String PREF_IS_FIRST_APP_START = "is_first_app_start";
    private static final String PREF_RATE_APP_REMINDER_DATE = "rate_app_reminder_date";
    private static final String PREF_APP_THEME = "app_theme";
    private static final String PREF_CURRENCY_CODE = "currency_code";
    private static final String PREF_CALC_OPERATION_TYPE = "calc_operation_type";
    private static final String PREF_CALC_CURRENCY_SUM = "calc_currency_sum";
    private static final String PREF_APP_VERSION = "app_version";

    public static boolean isFirstAppStart(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_FIRST_APP_START, true);
    }

    public static void setFirstAppStart(Context context, boolean firstAppStart) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_FIRST_APP_START, firstAppStart)
                .apply();
    }

    public static long getRateAppReminderDate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(PREF_RATE_APP_REMINDER_DATE, 0);
    }

    public static void setRateAppReminderDate(Context context, long date) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(PREF_RATE_APP_REMINDER_DATE, date)
                .apply();
    }

    public static int getAppTheme(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PREF_APP_THEME, R.style.AppTheme);
    }

    public static void setAppTheme(Context context, int appTheme) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_APP_THEME, appTheme)
                .apply();
    }

    public static String getCurrencyCode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_CURRENCY_CODE, CurrencyCode.sUSD);
    }

    public static void setCurrencyCode(Context context, String currencyCode) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_CURRENCY_CODE, currencyCode)
                .apply();
    }

    public static String getCalcOperationType(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_CALC_OPERATION_TYPE, OperationType.sSale);
    }

    public static void setCalcOperationType(Context context, String operationType) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_CALC_OPERATION_TYPE, operationType)
                .apply();
    }

    public static int getCalcCurrencySum(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PREF_CALC_CURRENCY_SUM, 1);
    }

    public static void setCalcCurrencySum(Context context, int currencySum) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_CALC_CURRENCY_SUM, currencySum)
                .apply();
    }

    public static String getAppVersion(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_APP_VERSION, "");
    }

    public static void setAppVersion(Context context, String s) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_APP_VERSION, s)
                .apply();
    }
}
