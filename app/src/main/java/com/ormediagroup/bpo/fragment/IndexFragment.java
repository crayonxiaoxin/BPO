package com.ormediagroup.bpo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ormediagroup.bpo.R;
import com.ormediagroup.bpo.adapter.MyFragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Lau on 2018/9/25.
 */

public class IndexFragment extends BaseFragment {
    private View view;
    private LinearLayout btn_1, btn_2, btn_3, btn_4;
    private Fragment fragment1, fragment2, fragment3, fragment4;
    private ArrayList<Fragment> mFragments;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter adapter;
    private TextView tab_1, tab_2, tab_3, tab_4;
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_index, null);
        initView();
        initData();
        return view;
    }

    private void initData() {
        sp = mActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        setTabClick(btn_1, 0);
        setTabClick(btn_2, 1);
        setTabClick(btn_3, 2);
        setTabClick(btn_4, 3);

//        fragment1 = Fragment1.newsInstance(1);
        fragment1 = new LoginFragment();
        fragment2 = new CompanyFragment();
        fragment3 = Fragment1.newInstance(3);
        fragment4 = Fragment1.newInstance(4);

        mFragments = new ArrayList<Fragment>();
        mFragments.add(fragment1);
        mFragments.add(fragment2);
        mFragments.add(fragment3);
        mFragments.add(fragment4);

        adapter = new MyFragmentPagerAdapter(getFragmentManager(), mFragments);
        viewPager.setAdapter(adapter);

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
        viewPager = view.findViewById(R.id.viewPager);
        btn_1 = view.findViewById(R.id.btn_1);
        btn_2 = view.findViewById(R.id.btn_2);
        btn_3 = view.findViewById(R.id.btn_3);
        btn_4 = view.findViewById(R.id.btn_4);

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
                viewPager.setCurrentItem(position, false);
            }
        });
    }

}
