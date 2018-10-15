package com.ormediagroup.bpo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ormediagroup.bpo.R;
import com.ormediagroup.bpo.bean.CompanyBean;
import com.ormediagroup.bpo.lau.CommonAdapter;
import com.ormediagroup.bpo.lau.CommonHolder;
import com.ormediagroup.bpo.network.JSONResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lau on 2018/9/6.
 */

public class CompanyFragment extends BaseFragment {
    private View view;
    private RecyclerView company_list;
    private CommonAdapter<CompanyBean> adapter;

    private String URL = "http://120.79.165.149/bpo/app-user-action/";
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_company, null);
        initView();
        initData();
        return view;
    }

    private void initData() {
        sp = mActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        int userid = sp.getInt("userid", 0);
        Log.i("ORM", "initData: userid=>" + userid);
        if (userid > 0) {

            company_list.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));

            String param = "action=get_company&uid=" + userid;
            new JSONResponse(mActivity, URL, param, new JSONResponse.onComplete() {
                @Override
                public void onComplete(JSONObject json) {
                    Log.i("ORM", "onComplete: json " + json);
                    List<CompanyBean> companyBeanList = getDataList(json);
                    adapter = new CommonAdapter<CompanyBean>(mActivity, companyBeanList, R.layout.item_company) {
                        @Override
                        protected void convert(Context context, CommonHolder holder, final CompanyBean companyBean) {
                            holder.setText(R.id.company_name, companyBean.getName())
                                    .setText(R.id.company_time, companyBean.getDate())
                                    .setText(R.id.company_status, companyBean.getStatus());
                            if (!companyBean.getStatus().equals("publish")) {
                                holder.setVisibility(R.id.company_delete, true)
                                        .setText(R.id.company_delete, getResources().getString(R.string.company_delete), "#ff0000", true)
                                        .setVisibility(R.id.company_modify, true)
                                        .setText(R.id.company_modify, getResources().getString(R.string.company_modify), "#ff0000", true)
                                        .setVisibility(R.id.company_msg, false)
                                        .setVisibility(R.id.company_file, false);
                            } else {
                                holder.setVisibility(R.id.company_delete, false)
                                        .setVisibility(R.id.company_modify, false)
                                        .setVisibility(R.id.company_msg, true)
                                        .setText(R.id.company_msg, getResources().getString(R.string.company_message)+"(1)", "#ff0000", true)
                                        .setVisibility(R.id.company_file, true)
                                        .setText(R.id.company_file, getResources().getString(R.string.company_file)+"(" + companyBean.getFiles() + ")", "#ff0000", true);
                                holder.getView(R.id.company_file).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setOnCompanyFragmentListener cfl = (setOnCompanyFragmentListener) mActivity;
                                        if (cfl != null && companyBean.getFiles() > 0) {
                                            cfl.toCompanyFilesFragment(companyBean.getId());
                                        }
                                    }
                                });

                            }
                        }
                    };
                    company_list.setAdapter(adapter);
                    adapter.setOnItemClickListener(new CommonAdapter.setOnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Toast.makeText(mActivity, "position=>" + position, Toast.LENGTH_SHORT).show();
                        }
                    });
                    View header = LayoutInflater.from(mActivity).inflate(R.layout.item_company_head, company_list, false);
                    adapter.setHeaderView(header);
                }
            });
        }else{
            Toast.makeText(mActivity, R.string.please_login,Toast.LENGTH_SHORT).show();
        }
    }

    private List<CompanyBean> getDataList(JSONObject json){
        List<CompanyBean> companyBeanList = new ArrayList<>();
        try {
            JSONArray companies = json.getJSONArray("company");
            for (int i = 0; i < companies.length(); i++) {
                JSONObject company = companies.getJSONObject(i);
                int filesCount = company.has("file") ? company.getJSONArray("file").length() : 0;
                CompanyBean bean = new CompanyBean(company.getInt("ID"), company.getString("name"), company.getString("post_modified"), company.getString("status"), filesCount);
                companyBeanList.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return companyBeanList;
    }

    private void initView() {
        company_list = view.findViewById(R.id.company_list);
    }

    public interface setOnCompanyFragmentListener {
        void toCompanyFilesFragment(int id);
    }

}
