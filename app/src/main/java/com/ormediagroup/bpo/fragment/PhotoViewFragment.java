package com.ormediagroup.bpo.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.ormediagroup.bpo.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Lau on 2018/9/26.
 */

public class PhotoViewFragment extends BaseFragment {
    private View view;
    private PhotoView photoView;

    public static final PhotoViewFragment newInstance(String url) {
        PhotoViewFragment f = new PhotoViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_photoview, null);
        initView();
        initData();
        return view;
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.i("ORM", "initData: image url " + bundle.getString("url"));
            Picasso.get().load(bundle.getString("url")).into(photoView);
        }
    }

    private void initView() {
        photoView = view.findViewById(R.id.photoView);
    }

    @Override
    public boolean onBackPressed() {
        setOnPhotoViewFragmentListener sopvfl = (setOnPhotoViewFragmentListener) mActivity;
        if (sopvfl != null) {
            sopvfl.showBottomBar();
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    public interface setOnPhotoViewFragmentListener {
        void showBottomBar();
    }
}
