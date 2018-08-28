package ua.kiev.semtur.ukrainiancurrencyexchangerates;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by SemTur on 04.11.2017.
 */

public class StartScreenFragment extends Fragment {
    private static final String ARG_DATA_DOWNLOADING_STATE = "Data_downloading_state";
    private ProgressBar mProgressBar;
    private TextView mViewStartText;
    private Button mButtonRepeat;

    public static Fragment newInstance(boolean isDataDownloading) {
        Fragment fragment = new StartScreenFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_DATA_DOWNLOADING_STATE, isDataDownloading);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_screen, container, false);
        mProgressBar = view.findViewById(R.id.progressBar);
        mViewStartText = view.findViewById(R.id.textView);
        mButtonRepeat = view.findViewById(R.id.button_repeat);
        mButtonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               MainActivity mainActivity = (MainActivity) getActivity();
               mainActivity.updateCERData();
            }
        });
        boolean isDataDownloading = getArguments().getBoolean(ARG_DATA_DOWNLOADING_STATE);
        if (!isDataDownloading) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mViewStartText.setText(R.string.network_issues);
            mButtonRepeat.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
