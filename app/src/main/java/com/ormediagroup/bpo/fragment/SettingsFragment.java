package com.ormediagroup.bpo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ormediagroup.bpo.MainActivity;
import com.ormediagroup.bpo.R;
import com.ormediagroup.bpo.bean.LanguageBean;
import com.ormediagroup.bpo.lau.CommonAdapter;
import com.ormediagroup.bpo.lau.CommonHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Lau on 2018/9/27.
 */

public class SettingsFragment extends BaseFragment {

    private View view;
    private RecyclerView language_change;
    private CommonAdapter<LanguageBean> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, null);
        initView();
        initData();
        return view;
    }

    private void initView() {
        language_change = view.findViewById(R.id.language_change);
    }

    private void initData() {
        language_change.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        final List<LanguageBean> languageBeanList = new ArrayList<>();
        LanguageBean lang_normal = new LanguageBean("default", getResources().getString(R.string.language_default));
        LanguageBean lang_zh_cn = new LanguageBean("zh_cn", "简体中文");
        LanguageBean lang_zh_hk = new LanguageBean("zh_hk", "繁體中文");
        LanguageBean lang_en = new LanguageBean("en", "English");
        languageBeanList.add(lang_normal);
        languageBeanList.add(lang_zh_cn);
        languageBeanList.add(lang_zh_hk);
        languageBeanList.add(lang_en);
        adapter = new CommonAdapter<LanguageBean>(mActivity, languageBeanList, R.layout.item_languange) {
            @Override
            protected void convert(Context context, CommonHolder holder, LanguageBean languageBean) {
                holder.setText(R.id.language_name, languageBean.getName());
            }
        };
        language_change.setAdapter(adapter);
        adapter.setOnItemClickListener(new CommonAdapter.setOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String type = languageBeanList.get(position).getType();
                switch (type) {
                    case "default":
                        changeAppLanguage(Locale.getDefault());
                        break;
                    case "zh_cn":
                        changeAppLanguage(Locale.SIMPLIFIED_CHINESE);
                        break;
                    case "zh_hk":
                        changeAppLanguage(Locale.TRADITIONAL_CHINESE);
                        break;
                    case "en":
                        changeAppLanguage(Locale.ENGLISH);
                        break;
                }
            }
        });
    }

    public void changeAppLanguage(Locale locale) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Configuration configuration = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        getResources().updateConfiguration(configuration, metrics);
        mActivity.recreate();
    }

}
