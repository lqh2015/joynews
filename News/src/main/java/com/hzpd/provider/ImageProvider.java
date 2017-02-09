package com.hzpd.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import com.hzpd.utils.Log;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Webview 新闻内容 下载图片
 */
public class ImageProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        return super.openAssetFile(uri, mode);
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) {
        try {
            Log.e("test", "uri " + uri);
            File file = null;
            file = Environment.getExternalStorageDirectory();
            file = new File(file, "test.jpg");
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
