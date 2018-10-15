package com.ormediagroup.bpo.lau;

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
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.ormediagroup.bpo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lau on 2018/9/30.
 */

public class MyDownloadManager {

    private DownloadManager downloadManager;
    private List<Long> list = new ArrayList<>();

    private Context context;
    private Uri uri;
    private String filename;
    private String channelId;
    private int icon;
    private String title;

    public MyDownloadManager(Context context, Uri uri, String filename, String channelId, int icon, String title) {
        this.context = context;
        this.uri = uri;
        this.filename = filename;
        this.channelId = channelId;
        this.icon = icon;
        this.title = title;
        context.registerReceiver(downloadComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    BroadcastReceiver downloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            list.remove(downloadId);
            if (list.isEmpty()) {
                showNotification(context, channelId, downloadId, icon, title, context.getResources().getString(R.string.download_completed), NotificationManager.IMPORTANCE_DEFAULT);
            }
        }
    };


    public void initDownloadManager() {
        Toast.makeText(context, context.getResources().getString(R.string.downloading), Toast.LENGTH_SHORT).show();
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
//      request.setDestinationInExternalFilesDir(mActivity, Environment.DIRECTORY_DOWNLOADS, bundle.getString("name") + ".pdf");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle(filename);
        long id = downloadManager.enqueue(request);
        list.add(id);
    }

    private void showNotification(Context context, String channelId, long id, int icon, String title, String content, int importance) {
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(id);
        Cursor c = downloadManager.query(q);
        String localUri = "";
        String filetype = "";
        if (c.moveToFirst()) {
            int stauts = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (stauts == DownloadManager.STATUS_SUCCESSFUL) {
                localUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                filetype = c.getString(c.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                Log.i("ORM", "showNotification: " + filetype);
                if (title.length() == 0) {
                    title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
                }
            }
        }
        Intent intent = null;
        switch (filetype) {
            case "application/pdf":
                intent = getPdfFileIntent(context, Uri.parse(localUri));
                break;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
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

    private static Intent getPdfFileIntent(Context context, Uri uri) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        Log.i("ORM", "onDownload : getPdfFileIntent " + uri.getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            // should add fileProvider element to manifest
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", new File(uri.getPath()));
        } else {
            uri = Uri.fromFile(new File(uri.getPath()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    public void unregisterReceiver() {
        context.unregisterReceiver(downloadComplete);
    }

}
