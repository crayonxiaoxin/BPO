package com.ormediagroup.bpo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ormediagroup.bpo.adapter.MyFragmentPagerAdapter;
import com.ormediagroup.bpo.fragment.BaseFragment;
import com.ormediagroup.bpo.fragment.CompanyFilesFragment;
import com.ormediagroup.bpo.fragment.CompanyFragment;
import com.ormediagroup.bpo.fragment.Fragment1;
import com.ormediagroup.bpo.fragment.LoginFragment;
import com.ormediagroup.bpo.fragment.PDFViewFragment;
import com.ormediagroup.bpo.fragment.PhotoViewFragment;
import com.ormediagroup.bpo.fragment.SettingsFragment;
import com.ormediagroup.bpo.fragment.SketchpadFragment;
import com.ormediagroup.bpo.fragment.TestUploadFragment;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CompanyFragment.setOnCompanyFragmentListener, CompanyFilesFragment.setOnCompanyFilesFragmentListener, PhotoViewFragment.setOnPhotoViewFragmentListener, PDFViewFragment.setOnPDFViewFragmentListener,TestUploadFragment.setOnTestUploadFragmentListener,SketchpadFragment.setOnSketchpadFragmentListener {

    private LinearLayout btn_1, btn_2, btn_3, btn_4;
    private Fragment fragment1, fragment2, fragment3, fragment4;
    private ArrayList<Fragment> mFragments;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter adapter;
    private TextView tab_1, tab_2, tab_3, tab_4;
    private SharedPreferences sp;
    private FrameLayout frameLayout;
    private LinearLayout bottomTabBar;
    private boolean isExit = false;
    private static String TAG = "ORM";
    private static int REQUEST_CODE_UPLOAD = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setupXinge();
    }

    private void setupXinge() {
        XGPushManager.registerPush(this, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                //token在设备卸载重装的时候有可能会变
                Log.d("TPush", "注册成功，设备token为：" + data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
    }

    private void initData() {
        checkPermission();
        sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        setTabClick(btn_1, 0);
        setTabClick(btn_2, 1);
        setTabClick(btn_3, 2);
        setTabClick(btn_4, 3);

//        fragment1 = Fragment1.newsInstance(1);
        fragment1 = new LoginFragment();
        fragment2 = new CompanyFragment();
//        fragment3 = Fragment1.newInstance(3);
        fragment3 = new SettingsFragment();
//        fragment4 = Fragment1.newInstance(4);
        fragment4 = new TestUploadFragment();

        mFragments = new ArrayList<Fragment>();
        mFragments.add(fragment1);
        mFragments.add(fragment2);
        mFragments.add(fragment3);
        mFragments.add(fragment4);

        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

        viewPager.setCurrentItem(0, false);
        tab_1.setTextColor(Color.WHITE);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                initTabs();
                switch (position) {
                    case 0:
                        tab_1.setTextColor(Color.WHITE);
                        break;
                    case 1:
                        tab_2.setTextColor(Color.WHITE);
                        break;
                    case 2:
                        tab_3.setTextColor(Color.WHITE);
                        break;
                    case 3:
                        tab_4.setTextColor(Color.WHITE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        frameLayout = findViewById(R.id.frameLayout);
        bottomTabBar = findViewById(R.id.bottom_tabbar);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);

        tab_1 = btn_1.findViewById(R.id.tab1);
        tab_2 = btn_2.findViewById(R.id.tab2);
        tab_3 = btn_3.findViewById(R.id.tab3);
        tab_4 = btn_4.findViewById(R.id.tab4);

    }

    private void initTabs() {
        tab_1.setTextColor(getResources().getColor(R.color.colorDefaultFont));
        tab_2.setTextColor(getResources().getColor(R.color.colorDefaultFont));
        tab_3.setTextColor(getResources().getColor(R.color.colorDefaultFont));
        tab_4.setTextColor(getResources().getColor(R.color.colorDefaultFont));

    }

    private void setTabClick(LinearLayout btn, final int position) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewPager();
                FragmentManager fm = getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                    fm.popBackStack();
                }
                viewPager.setCurrentItem(position, false);
            }
        });
    }

    private void showFrameLayout() {
        viewPager.setVisibility(View.GONE);
        frameLayout.setVisibility(View.VISIBLE);
    }

    private void showViewPager() {
        viewPager.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.GONE);
    }

    private void showBottomTabBar() {
        bottomTabBar.setVisibility(View.VISIBLE);
    }

    private void hideBottomTabBar() {
        bottomTabBar.setVisibility(View.GONE);
    }

    /**
     * 获取权限
     */
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, getResources().getString(R.string.permission_request), Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
//            Toast.makeText(this, getResources().getString(R.string.permission_succeed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sp.getBoolean("remember", false) == false) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("userid", 0);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int backStackCount = fm.getBackStackEntryCount();
        Log.i("ORM", "onBackPressed: " + backStackCount);

        // fragment onbackpressed
        if (backStackCount > 0) {
            if (backStackCount == 1) {
                showViewPager();
            }
            Fragment f = fm.findFragmentById(R.id.frameLayout);
            if (f instanceof BaseFragment) {
                if (((BaseFragment) f).onBackPressed()) {
                    Log.i("ORM", "fragment back button handled");
                    fm.popBackStack();
                } else {
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        } else if (backStackCount == 0) {
            if (isExit) {
                this.finish();
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 500);
            }
        } else {
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment f, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, f, tag);
        ft.addToBackStack(tag);
        ft.commit();
    }

    private void addFragment(Fragment f, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(fm.findFragmentById(R.id.frameLayout));
        ft.add(R.id.frameLayout,f,tag);
        ft.addToBackStack(tag);
        ft.commit();
    }

    @Override
    public void toCompanyFilesFragment(int id) {
        showFrameLayout();
        Fragment f = CompanyFilesFragment.newInstance(id);
        replaceFragment(f, "companyFiles");
    }

    @Override
    public void toPDFViewFragment(String url,String filename) {
        hideBottomTabBar();
        Fragment f = PDFViewFragment.newInstance(url,filename);
        addFragment(f, "pdfView");
    }

    @Override
    public void toPhotoViewFragment(String url) {
        hideBottomTabBar();
        Fragment f = PhotoViewFragment.newInstance(url);
        addFragment(f, "photoView");
    }

    @Override
    public void showBottomBar() {
        showBottomTabBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPLOAD && data!=null){
            Uri uri = data.getData();
            Log.i(TAG, "onActivityResult: "+uri.getPath());
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            Log.i(TAG, "onActivityResult: "+picturePath);
        }
    }

    @Override
    public void toSketchpadFragment() {
        hideBottomTabBar();
        showFrameLayout();
        Fragment f = new SketchpadFragment();
        replaceFragment(f,"sketchpad");
    }
}
