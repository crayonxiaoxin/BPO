package com.ormediagroup.bpo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ormediagroup.bpo.R;

/**
 * Created by Lau on 2018/10/12.
 */

public class TestUploadFragment extends BaseFragment {
    private View view;
    private Button testUpload;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test_upload,null);
        initView();
        initData();
        return view;
    }

    private void initView() {
        testUpload = view.findViewById(R.id.testUpload);
    }


    private void initData() {
        testUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnTestUploadFragmentListener sotufl = (setOnTestUploadFragmentListener) mActivity;
                if (sotufl!=null){
                    sotufl.toSketchpadFragment();
                }
            }
        });
    }

    public interface setOnTestUploadFragmentListener{
        void toSketchpadFragment();
    }
}
