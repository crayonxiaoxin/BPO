package com.ormediagroup.bpo.notuse;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
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
import android.widget.Toast;

import com.ormediagroup.bpo.R;
import com.ormediagroup.bpo.fragment.BaseFragment;
import com.ormediagroup.bpo.lau.MyDownloadManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lau on 2018/9/26.
 */

public class PDFViewFragment extends BaseFragment {
    private View view;
    private WebView pdfView;
    private List<Long> list = new ArrayList<>();
    private DownloadManager downloadManager;

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

    BroadcastReceiver downloadComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            list.remove(downloadId);
            if (list.isEmpty()) {
//                Toast.makeText(mActivity, getResources().getString(R.string.download_completed), Toast.LENGTH_SHORT).show();
                showNotification(mActivity, "mychannel_1", downloadId, R.mipmap.ic_launcher, "", getResources().getString(R.string.download_completed), NotificationManager.IMPORTANCE_DEFAULT);
            }
        }
    };

    private void showNotification(Context context, String channelId, long id, int icon, String title, String content, int importance) {
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(id);
        Cursor c = downloadManager.query(q);
        String localUri = "";
        if (c.moveToFirst()) {
            int stauts = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (stauts == DownloadManager.STATUS_SUCCESSFUL) {
                localUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if (title.length()==0){
                    title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
                }
            }
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, getPdfFileIntent(context, Uri.parse(localUri)), PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setFullScreenIntent(null, true)
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= 21) {
            builder.setVibrate(new long[0]);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(channelId, title, importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        notificationManager.notify(0, builder.build());
    }

    public static Intent getPdfFileIntent(Context context, Uri uri) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        Log.i("ORM", "onDownload : getPdfFileIntent " + uri.getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", new File(uri.getPath()));
        } else {
            uri = Uri.fromFile(new File(uri.getPath()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    private void initData() {
        final Bundle bundle = getArguments();
        if (bundle != null) {
            Log.i("ORM", "initData: pdf url " + bundle.getString("url"));
            String url = bundle.getString("url");
            String mozilla_bpo = "http://120.79.165.149/bpo/wp-content/themes/BPO2/pdfjs/web/viewer.html?file=";
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
                pdfView.loadUrl(mozilla_bpo + url);
                Log.i("ORM", "initData: load pdf " + mozilla_bpo + url);
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
                pdfView.loadUrl(mozilla_bpo + url);
            }
            pdfView.setWebChromeClient(new WebChromeClient());

            pdfView.loadUrl(mozilla_bpo + url);
            final String downloadurl = url;
            pdfView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    Log.i("ORM", "onDownloadStart: pdf url " + downloadurl);
                    Uri uri = Uri.parse(downloadurl);
                    Toast.makeText(mActivity, getResources().getString(R.string.downloading), Toast.LENGTH_SHORT).show();
                    downloadManager = (DownloadManager) mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
                    mActivity.registerReceiver(downloadComplete,
                            new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    DownloadManager.Request request = new DownloadManager.Request(uri);
//                    request.setDestinationInExternalFilesDir(mActivity, Environment.DIRECTORY_DOWNLOADS, bundle.getString("name") + ".pdf");
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, bundle.getString("name") + ".pdf");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                    request.setTitle(bundle.getString("name")+".pdf");
                    long id = downloadManager.enqueue(request);
                    list.add(id);
                }
            });
        }
    }

    private void initView() {
        pdfView = view.findViewById(R.id.pdfView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(downloadComplete);
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
