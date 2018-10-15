package com.ormediagroup.bpo.fragment;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ormediagroup.bpo.R;
import com.ormediagroup.bpo.lau.MyDownloadManager;

import java.io.UnsupportedEncodingException;

/**
 * Created by Lau on 2018/9/26.
 */

public class PDFViewFragment extends BaseFragment {
    private View view;
    private WebView pdfView;
    private MyDownloadManager downloadManager;

    public static final PDFViewFragment newInstance(String url, String filename) {
        PDFViewFragment f = new PDFViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("name", filename);
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pdfview, null);
        initView();
        initData();
        return view;
    }

    private void initView() {
        pdfView = view.findViewById(R.id.pdfView);
    }

    private void initData() {
        final Bundle bundle = getArguments();
        if (bundle != null) {
            Log.i("ORM", "initData: pdf url " + bundle.getString("url"));
            String url = bundle.getString("url");
            String mozilla_bpo = "http://120.79.165.149/bpo/wp-content/themes/BPO2/pdfjs/web/viewer.html?file=";
            webViewLoadPdfJS(pdfView,mozilla_bpo,url);
            downloadManager = new MyDownloadManager(mActivity,Uri.parse(url),bundle.getString("name")+".pdf","mychannel_1",R.mipmap.ic_launcher,"");
            pdfView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    downloadManager.initDownloadManager();
                }
            });
        }
    }

    private void webViewLoadPdfJS(WebView pdfView,String pdfJsPath,String url) {
        WebSettings settings = pdfView.getSettings();
        settings.setSavePassword(false);
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        pdfView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        pdfView.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//api >= 19
            pdfView.loadUrl(pdfJsPath + url);
            Log.i("ORM", "initData: load pdf " + pdfJsPath + url);
        } else {
            if (!TextUtils.isEmpty(url)) {
                byte[] bytes = null;
                try {// 获取以字符编码为utf-8的字符
                    bytes = url.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (bytes != null) {
                    url = String.valueOf(Base64.encode(bytes, 1));// BASE64转码
                }
            }
            pdfView.loadUrl(pdfJsPath + url);
        }
        pdfView.setWebChromeClient(new WebChromeClient());
        pdfView.loadUrl(pdfJsPath + url);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadManager!=null){
            downloadManager.unregisterReceiver();
        }
    }

    @Override
    public boolean onBackPressed() {
        setOnPDFViewFragmentListener sopvfl = (setOnPDFViewFragmentListener) mActivity;
        if (sopvfl != null) {
            sopvfl.showBottomBar();
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    public interface setOnPDFViewFragmentListener {
        void showBottomBar();
    }
}
