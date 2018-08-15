package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OrgInfoFragment extends Fragment implements View.OnClickListener {
    static final String TAG = "OrgInfoFragment";
    private static final String ARG_ORG_ID = "orgId";

    private CERData mCERData = CERData.getInstance();
    private Organization mOrganization;
    private ImageView mImageViewOrgLogo;
    private TextView mTextViewOrgName;
    private TextView mTextViewOrgType;
    private TextView mTextViewRegion;
    private TextView mTextViewCity;
    private TextView mTextViewOrgAddress;
    private TextView mTextViewOrgPhone;
    private Button mButtonShowInTheInternet;
    private Button mButtonShowOnTheMap;
    private int mOrgType;

    public static Fragment newInstance(String orgId) {
        Fragment fragment = new OrgInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORG_ID, orgId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_info, container, false);

        mImageViewOrgLogo = view.findViewById(R.id.imageView_org_logo);

        mTextViewOrgName = view.findViewById(R.id.textView_bank_name);
        mTextViewOrgType = view.findViewById(R.id.textView_org_type);
        mTextViewRegion = view.findViewById(R.id.textView_region);
        mTextViewCity = view.findViewById(R.id.textView_city);
        mTextViewOrgAddress = view.findViewById(R.id.textView_bank_address);
        mTextViewOrgPhone = view.findViewById(R.id.textView_bank_phone);

        //встановлення макситальної довжини TextView
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        double coef = calcCoefScreenInchecs(displayMetrics);
        mTextViewOrgName.setMaxWidth((int) (displayMetrics.widthPixels / coef * 0.7));
        mTextViewOrgAddress.setMaxWidth((int) (displayMetrics.widthPixels / coef * 0.85));

        mButtonShowInTheInternet = view.findViewById(R.id.button_show_in_the_Internet);
        mButtonShowOnTheMap = view.findViewById(R.id.button_show_on_the_map);
        mButtonShowInTheInternet.setOnClickListener(this);
        mButtonShowOnTheMap.setOnClickListener(this);

        String orgId = getArguments().getString(ARG_ORG_ID);
        mOrganization = mCERData.getOrganization(orgId);

        OrgLogosManager manager = new OrgLogosManager(getActivity());
        Bitmap bitmap = manager.loadOrgLogo(mOrganization.getId(), mOrganization.getOrgType());
        if (bitmap.getHeight() == bitmap.getWidth()) {
            mImageViewOrgLogo.getLayoutParams().width = mImageViewOrgLogo.getLayoutParams().height;
            mImageViewOrgLogo.requestLayout();
        }
        mImageViewOrgLogo.setImageBitmap(bitmap);

        mTextViewOrgName.setText(mOrganization.getName());
        mTextViewOrgAddress.setText(mOrganization.getAddress());
        mTextViewOrgPhone.setText(mOrganization.getPhone());

        mOrgType = mOrganization.getOrgType();
        if (mOrgType == 0) {
            mTextViewOrgType.setText(R.string.nbu_title);
            mTextViewRegion.setText(mOrganization.getRegion());
            mTextViewCity.setText(mOrganization.getCity());
        } else {
            mTextViewRegion.setText(mCERData.getRegion(mOrganization.getRegion()));
            mTextViewCity.setText(mCERData.getCity(mOrganization.getCity()));
            if (mOrgType == 1) {
                mTextViewOrgType.setText(R.string.org_type_bank);
            } else if (mOrgType == 2) {
                mTextViewOrgType.setText(R.string.org_type_exchanger);
            }
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_show_in_the_Internet:
                Uri link = Uri.parse(mOrganization.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
                break;
            case R.id.button_show_on_the_map:
                StringBuilder searchSB = new StringBuilder("geo:0,0?q=");
                if (mOrgType == 1) {
                    searchSB.append(mOrganization.getName());
                } else {
                    searchSB.append(mCERData.getCity(mOrganization.getCity()))
                            .append('+')
                            .append(mOrganization.getAddress());
                }
                String geoUri = searchSB.toString().replaceAll(" ", "+");
                Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(geoIntent);
                break;
        }
    }

    private double calcCoefScreenInchecs(DisplayMetrics dm) {
        if (getActivity().findViewById(R.id.fragment_container_org_info) == null) {
            return 1;
        } else {
            double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
            double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
            double screenInches = Math.sqrt(x + y);
            if (screenInches >= 8) {
                return 2;
            } else {
                return 2.5;
            }
        }
    }
}
