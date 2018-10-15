package com.ormediagroup.bpo.fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.ormediagroup.bpo.R;
import com.ormediagroup.bpo.fragment.BaseFragment;
import com.ormediagroup.bpo.lau.SketchpadView;
import com.ormediagroup.bpo.network.JSONResponse;

import org.json.JSONObject;

/**
 * Created by Lau on 2018/10/11.
 */

public class SketchpadFragment extends BaseFragment {
    private View view;
    private SketchpadView sketchpad;
    private Button clear, save, open;

    private String SUBMIT_URL = "http://120.79.165.149/hkreaga2/test/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sketchpad, null);
        toLandscape();
        initView();
        return view;
    }

    private void initView() {
        sketchpad = view.findViewById(R.id.sketchpad);
        sketchpad.setPenColor(Color.BLACK);
        clear = view.findViewById(R.id.clear);
        save = view.findViewById(R.id.save);
        open = view.findViewById(R.id.open);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sketchpad.clear();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = sketchpad.getBitmap();

                // save to local
//                Bitmap bitmap = sketchpad.cropBitmap(0,0,200,100);
//                String path = sketchpad.save(bitmap);
//                if (path != null) {
//                    sketchpad.scanFile(mActivity, path);
//                    Log.i(TAG, "onClick: filepath = " + path);
//                }

                // save to server
                String encodeImage = sketchpad.getEncodeImage(bitmap);
                Log.i(TAG, "onClick: encodeImage" + encodeImage);
                new JSONResponse(mActivity, SUBMIT_URL, "file=" + encodeImage, new JSONResponse.onComplete() {
                    @Override
                    public void onComplete(JSONObject json) {
                        Log.i(TAG, "onComplete: upload json = " + json);
                    }
                });
                Toast.makeText(mActivity, "保存成功", Toast.LENGTH_SHORT).show();

            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mActivity.startActivityForResult(intent, 1);
            }
        });
    }

    private void toLandscape() {
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void toPortrait() {
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onBackPressed() {
        toPortrait();
        setOnSketchpadFragmentListener sosfl = (setOnSketchpadFragmentListener) mActivity;
        if (sosfl != null) {
            sosfl.showBottomBar();
        }
        return true;
    }

    public interface setOnSketchpadFragmentListener {
        void showBottomBar();
    }
}
