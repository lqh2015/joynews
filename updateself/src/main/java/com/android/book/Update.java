package com.android.book;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.joy.event.EventBus;

import java.io.File;

public class Update {
	public static void updateOnCreate(Activity act) {
		if(!EventBus.getDefault().isRegistered(act))
			EventBus.getDefault().register(act);
		UpdateService.requestForUpdate(act);
		UpdateUtils.reset(act);
	}

	public static void updateOnDestroy(Activity act) {
		EventBus.getDefault().unregister(act);
	}

	public static void showLocalUpdateDialog(Activity act) {
		Util_Log.i("showLocalUpdateDialog()");
		SharedPreferences pref = act.getSharedPreferences(
				UpdateUtils.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
		try {
			if (pref.getBoolean(UpdateUtils.KEY.KEY_SILENCE_INSTALL, false)) {
				return;
			}
			long later = pref.getLong(UpdateUtils.KEY.UPDATE_LATER_TIME, 0L);
			if (later > 10000) {
				if (System.currentTimeMillis() - later > UpdateUtils.UPDATE_LATER_TIME) {
					pref.edit().putLong(UpdateUtils.KEY.UPDATE_LATER_TIME, 0L)
							.apply();
				} else {
					return;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (pref.getBoolean(UpdateUtils.KEY.KEY_HAS_NEW, false)) {
				boolean forcing = pref.getBoolean(
						UpdateUtils.KEY.KEY_FORCING_UPDATE, false);
				int versionCode = pref.getInt(UpdateUtils.KEY.KEY_VERSION_CODE,
						0);
				if (versionCode <= Utils.getVersionCode(act)) {
					pref.edit().putBoolean(UpdateUtils.KEY.KEY_HAS_NEW, false);
					return;
				}
				if (!forcing) {
					if (pref.getBoolean(UpdateUtils.KEY.IS_DOWNLOADING, false)
							|| pref.getBoolean(
									UpdateUtils.KEY.IS_WIFI_DOWNLOADING, false)) {
						return;
					}
				}
				boolean autoD = pref.getBoolean(
						UpdateUtils.KEY.KEY_AUTO_DOWNLOAD, false);
				int cVersion = pref.getInt(
						UpdateUtils.KEY.COMPLETED_VERSION_CODE, 0);
				if (autoD) {
					File root = Environment.getExternalStorageDirectory();
					if (root != null) {
						File fold = new File(root, UpdateUtils.PATH_SAVE);
						File target = new File(fold,
								UpdateUtils.getApkName(act));
						// Intent intent = new Intent(this,
						// DownloadService.class);

						if (!target.exists() || cVersion < versionCode) {

							if (pref.getBoolean(
									UpdateUtils.KEY.KEY_MOBILE_AUTO, false)) {
								// intent.putExtra(DownloadService.EXTRA_OF_SERVICE_DOWNLOAD,
								// UpdateUtils.KEY.IS_DOWNLOADING);
								UpdateService.requestForDownload(act,
										UpdateUtils.KEY.IS_DOWNLOADING);
							} else {
								// intent.putExtra(DownloadService.EXTRA_OF_SERVICE_DOWNLOAD,
								// UpdateUtils.KEY.IS_WIFI_DOWNLOADING);
								UpdateService.requestForDownload(act,
										UpdateUtils.KEY.IS_WIFI_DOWNLOADING);
							}
							// startService(intent);
							return;
						} else if (target.exists()) {
							String md5 = null;
							md5 = UpdateUtils.getHash(target.getAbsolutePath(),
									"MD5").toLowerCase();
							if (!md5.equals(pref.getString(
									UpdateUtils.KEY.KEY_MD5, ""))) {
								target.delete();
								pref.edit()
										.putInt(UpdateUtils.KEY.COMPLETED_VERSION_CODE,
												0).apply();
								if (pref.getBoolean(
										UpdateUtils.KEY.KEY_MOBILE_AUTO, false)) {
									// intent.putExtra(DownloadService.EXTRA_OF_SERVICE_DOWNLOAD,
									// UpdateUtils.KEY.IS_DOWNLOADING);
									UpdateService.requestForDownload(act,
											UpdateUtils.KEY.IS_DOWNLOADING);
								} else {
									// intent.putExtra(DownloadService.EXTRA_OF_SERVICE_DOWNLOAD,
									// UpdateUtils.KEY.IS_WIFI_DOWNLOADING);
									UpdateService
											.requestForDownload(
													act,
													UpdateUtils.KEY.IS_WIFI_DOWNLOADING);
								}
								// startService(intent);
								return;
							}

						}
					}
				}
				String dialogContent = pref.getString(
						UpdateUtils.KEY.KEY_DIALOG_CONTENT, "");

				AlertDialog.Builder builder = DialogUtil.checkStatus(act,
						dialogContent, forcing, autoD);
				Dialog dialog = null;
				if (builder != null) {
					if (dialog == null) {
						dialog = builder.create();
						dialog.show();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
