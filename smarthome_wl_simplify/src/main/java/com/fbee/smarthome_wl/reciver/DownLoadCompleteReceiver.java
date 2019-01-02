package com.fbee.smarthome_wl.reciver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.content.FileProvider;

import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;

import java.io.File;

/**
 * apk下载完自动安装
 * @date 2016-12-6 下午4:53:36
 */
public class DownLoadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Long downID = PreferencesUtils.getLong("downID");
        if (downID == 0) {
            ToastUtils.showShort("下载出错");
        }
        // TODO Auto-generated method stub
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long id = intent
                    .getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (downID == id) {
                DownloadManager downManager = (DownloadManager) context
                        .getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = downManager.getUriForDownloadedFile(downID);
                if (uri == null) {
                    return;
                }
                String pathString = getFilePathFromUri(context, uri);
                Intent apkIntent = new Intent(Intent.ACTION_VIEW);
                apkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                apkIntent.setAction(Intent.ACTION_VIEW);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    apkIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    uri= FileProvider.getUriForFile(context, "com.fbee.smarthome_wl.fileprovider", new File(pathString));
                    apkIntent.setDataAndType(uri,
                            "application/vnd.android.package-archive");
                } else {
                    apkIntent.setDataAndType(Uri.fromFile(new File(pathString)),
                            "application/vnd.android.package-archive");
                }
                context.startActivity(apkIntent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }

        } else if (intent.getAction().equals(
                DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
        }
    }

    public static String getFilePathFromUri(Context c, Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            String[] filePathColumn = {MediaColumns.DATA};
            ContentResolver contentResolver = c.getContentResolver();

            Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                    null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if ("file".equals(uri.getScheme())) {
            filePath = new File(uri.getPath()).getAbsolutePath();
        }
        return filePath;
    }
}
