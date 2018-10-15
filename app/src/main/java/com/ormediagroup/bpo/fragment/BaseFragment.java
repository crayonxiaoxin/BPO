package com.ormediagroup.bpo.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Lau on 2018/9/26.
 */

public class BaseFragment extends Fragment {
    protected Activity mActivity;
    public String TAG = "ORM";
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }
}
