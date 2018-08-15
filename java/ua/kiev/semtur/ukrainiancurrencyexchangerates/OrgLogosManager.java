package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by SemTur on 28.12.2017.
 */

public class OrgLogosManager {
    private static final String ORG_LOGOS_FOLDER = "org_logos";
    private Context mContext;
    private AssetManager mAssetManager;

    public OrgLogosManager(Context context) {
        mContext = context;
        mAssetManager = context.getAssets();
    }

    public Bitmap loadOrgLogo(String orgId, int orgType) {
        String path;
        if (orgType == 2) {
            path = ORG_LOGOS_FOLDER + "/cep.png";
        } else {
            path = ORG_LOGOS_FOLDER + "/" + orgId + ".png";
        }
        try {
            InputStream inputStream = mAssetManager.open(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            return BitmapFactory.decodeStream(inputStream, null, options);
        } catch (IOException e) {
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
