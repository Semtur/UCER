package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.app.Activity;
import android.support.annotation.IdRes;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by semtur on 19.01.2018.
 */

class AdLoader {
    private static final String sAppId = "ca-app-pub-7383970161935002~7955262283";

    static void loadAd(Activity activity, @IdRes int id) {
        MobileAds.initialize(activity.getApplicationContext(), sAppId);
        AdView adView = activity.findViewById(id);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
