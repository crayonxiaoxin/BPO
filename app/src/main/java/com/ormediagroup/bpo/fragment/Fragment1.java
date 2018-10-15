package com.ormediagroup.bpo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ormediagroup.bpo.R;

/**
 * Created by Lau on 2018/9/6.
 */

public class Fragment1 extends Fragment {
    private TextView test;

    public static final Fragment1 newInstance(int position) {
        Fragment1 f = new Fragment1();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, null);
        test = view.findViewById(R.id.test);
        Bundle bundle = getArguments();
        if (bundle != null) {
            test.setText("page " + bundle.getInt("position"));
        }
        return view;
    }
}
