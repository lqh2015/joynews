package com.android.book;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

public class DialogUtil {

	public static AlertDialog.Builder checkStatus(final Context context,
			String dialogContent, boolean forcing, boolean autoD) {
		Util_Log.i("checkStatus() forcing:"+forcing+",auto:"+autoD);
		try {
			final SharedPreferences prefs = context.getSharedPreferences(
					UpdateUtils.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(UpdateUtils.getAppName(context));
			builder.setMessage(dialogContent);

			if (forcing) {
				builder.setCancelable(false);
			}

			if (!forcing && !autoD) {
				builder.setNegativeButton(
						context.getResources().getString(
								UpdateUtils.getStringId(context,
										"dialog_button_later")),
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								try {
									prefs.edit()
											.putLong(
													UpdateUtils.KEY.UPDATE_LATER_TIME,
													System.currentTimeMillis())
											.apply();
									NotificationManager nm = (NotificationManager) context
											.getSystemService(Context.NOTIFICATION_SERVICE);
									nm.cancel(UpdateUtils.REQUEST_CODE);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
			}

			if (autoD) {
				builder.setPositiveButton(
						context.getResources().getString(
								UpdateUtils.getStringId(context,
										"dialog_button_now")),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								try {
									prefs.edit()
											.putLong(
													UpdateUtils.KEY.UPDATE_LATER_TIME,
													0L).apply();
								} catch (Exception e) {
									e.printStackTrace();
								}
								File root = Environment
										.getExternalStorageDirectory();
								if (root != null) {
									File fold = new File(root,
											UpdateUtils.PATH_SAVE);
									File target = new File(fold, UpdateUtils
											.getApkName(context));
									if (target.exists()) {
										UpdateUtils.installApk(context, target);
									}
								}
							}
						});
			} else {
				builder.setPositiveButton(
						context.getResources().getString(
								UpdateUtils.getStringId(context,
										"dialog_button_intall")),
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								try {
									prefs.edit()
											.putLong(
													UpdateUtils.KEY.UPDATE_LATER_TIME,
													0L).apply();
								} catch (Exception e) {
									e.printStackTrace();
								}
								// Intent intent = new Intent(context,
								// DownloadService.class);
								// intent.putExtra(DownloadService.EXTRA_OF_SERVICE_DOWNLOAD,
								// UpdateUtils.KEY.IS_DOWNLOADING);
								// context.startService(intent);

								UpdateService.requestForDownload(context,
										UpdateUtils.KEY.IS_DOWNLOADING);

								Toast.makeText(context, "Downloading...",
										Toast.LENGTH_SHORT).show();
							}
						});
			}

			return builder;
		} catch (Exception e) {
			System.out.println("异常了："+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
