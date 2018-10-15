package com.ormediagroup.bpo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.ormediagroup.bpo.R;
import com.ormediagroup.bpo.bean.CompanyFilesBean;
import com.ormediagroup.bpo.lau.CommonAdapter;
import com.ormediagroup.bpo.lau.CommonHolder;
import com.ormediagroup.bpo.network.JSONResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lau on 2018/9/20.
 */

public class CompanyFilesFragment extends BaseFragment {
    private View view;
    private RecyclerView fileRecyclerView;

    private String URL = "http://120.79.165.149/bpo/app-user-action/";
    private SharedPreferences sp;
    private CommonAdapter<CompanyFilesBean> adapter;

    public static final CompanyFilesFragment newInstance(int id) {
        CompanyFilesFragment f = new CompanyFilesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        f.setArguments(bundle);
        return f;
    }

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
        int id = 0;
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getInt("id");
            Toast.makeText(mActivity, "id = " + id, Toast.LENGTH_SHORT).show();
            fileRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
            String param = "action=get_company&uid=" + sp.getInt("userid", 0) + "&app_form_id=" + id;
            new JSONResponse(mActivity, URL, param, new JSONResponse.onComplete() {
                @Override
                public void onComplete(JSONObject json) {
                    Log.i("ORM", "onComplete: json=>" + json);
                    final List<CompanyFilesBean> fileList = getDataList(json);
                    adapter = new CommonAdapter<CompanyFilesBean>(mActivity, fileList, R.layout.item_company_file) {
                        @Override
                        protected void convert(final Context context, CommonHolder holder, final CompanyFilesBean companyFilesBean) {
                            if (companyFilesBean.getFileType().equals("application/pdf")) {
                                holder.setImageResource(R.id.fileImg, R.drawable.pdf_demo);
                            } else {
                                holder.setImageURL(R.id.fileImg, companyFilesBean.getUrl());
                            }
                            holder.setText(R.id.fileName, companyFilesBean.getName()).setText(R.id.fileDate, companyFilesBean.getDate());
                        }
                    };
                    fileRecyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new CommonAdapter.setOnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
//                            Toast.makeText(mActivity, "click " + position, Toast.LENGTH_SHORT).show();
                            setOnCompanyFilesFragmentListener socffl = (setOnCompanyFilesFragmentListener) mActivity;
                            if (fileList.get(position).getFileType().equals("application/pdf")) {
                                if (socffl != null) {
                                    socffl.toPDFViewFragment(fileList.get(position).getUrl(),fileList.get(position).getName());
                                }
                            } else {
                                if (socffl != null) {
                                    socffl.toPhotoViewFragment(fileList.get(position).getUrl());
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    private List<CompanyFilesBean> getDataList(JSONObject json){
        final List<CompanyFilesBean> fileList = new ArrayList<>();
        try {
            JSONObject company = json.getJSONArray("company").getJSONObject(0);
            JSONArray files = company.getJSONArray("file");
            for (int i = 0; i < files.length(); i++) {
                CompanyFilesBean fileBean = new CompanyFilesBean(files.getJSONObject(i).getInt("ID"),
                        files.getJSONObject(i).getString("name"), files.getJSONObject(i).getString("url"),
                        files.getJSONObject(i).getString("file_type"), files.getJSONObject(i).getString("post_modified"));
                fileList.add(fileBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    private void initView() {
        fileRecyclerView = view.findViewById(R.id.company_list);
    }

    public interface setOnCompanyFilesFragmentListener {

        void toPDFViewFragment(String url,String filename);

        void toPhotoViewFragment(String url);
    }
}
