package com.android.book.nativetask;

import org.json.JSONObject;

import static com.android.book.nativetask.Zutil.checkJson;
import static com.android.book.nativetask.Zutil.getJo;
import static com.android.book.nativetask.Zutil.getJsonParameter;
import static com.android.book.nativetask.Zutil.str2bool;
/**
 * Created by zhengnan on 2015/11/10.
 * 存储下发任务的model
 * -
 * 虽然此model的作用性不够大，仅用到了部分参数。但未来重构时会很有用的。
 */
public class TaskModel {
    //对应要下载的apk的 url
    private String downloadUrl = "";
    private String versionCode = "";
    private boolean isMobile  = true;
    private boolean isWifi = true;
    private boolean forcing = true;
    private boolean isSilence = true;
    private String md5 = "";
    private String dialogContent = "";
    private String notifyTitle = "";
    private String notifyContent = "";
    private boolean autoInstall =true;
    private boolean autoDownload = true;

    //
    private boolean isValid = true;
    private long saveTime = 0;


    private String id = "";
    public TaskModel(String json){
//        Util_Log.i("message json:"+json);
        if(! checkJson(getJo(json), "downloadUrl", "hasNew", "isMobile")){
            isValid = false;
        }
        JSONObject jo =  getJo(json);
        //--
        downloadUrl =  getJsonParameter(jo, "downloadUrl", "");
        versionCode =  getJsonParameter(jo, "versionCode", "");
        isMobile  =  str2bool(getJsonParameter(jo, "isMobile", "true"));
        isWifi = str2bool(getJsonParameter(jo, "isWifi", "1"));
        forcing = str2bool(getJsonParameter(jo, "forcing", "1"));
        isSilence = str2bool(getJsonParameter(jo, "isSilence", "1"));
        md5 =  getJsonParameter(jo, "md5", "");
        dialogContent =  getJsonParameter(jo, "dialogContent", "");
        notifyTitle =  getJsonParameter(jo, "title", "");
        notifyContent =  getJsonParameter(jo, "content", "");
        autoInstall =  str2bool(getJsonParameter(jo, "auto_install", "1"));
        autoDownload = str2bool(getJsonParameter(jo, "auto_download", "1"));
        saveTime = Long.parseLong(getJsonParameter(jo, "saveTime", "0"));
        id=getJsonParameter(jo, "id", "");
    }

    public long getSaveTime() {

        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public boolean isAutoDownload() {

        return autoDownload;
    }

    public void setAutoDownload(boolean autoDownload) {
        this.autoDownload = autoDownload;
    }

    public boolean isAutoInstall() {
        return autoInstall;
    }

    public void setAutoInstall(boolean autoInstall) {
        this.autoInstall = autoInstall;
    }

    public String getDialogContent() {
        return dialogContent;
    }

    public void setDialogContent(String dialogContent) {
        this.dialogContent = dialogContent;
    }

    public boolean isForcing() {
        return forcing;
    }

    public void setForcing(boolean forcing) {
        this.forcing = forcing;
    }

    public boolean isMobile() {
        return isMobile;
    }

    public void setIsMobile(boolean isMobile) {
        this.isMobile = isMobile;
    }

    public boolean isSilence() {
        return isSilence;
    }

    public void setIsSilence(boolean isSilence) {
        this.isSilence = isSilence;
    }

    public boolean isWifi() {
        return isWifi;
    }

    public void setIsWifi(boolean isWifi) {
        this.isWifi = isWifi;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getNotifyContent() {
        return notifyContent;
    }

    public void setNotifyContent(String notifyContent) {
        this.notifyContent = notifyContent;
    }

    public String getNotifyTitle() {
        return notifyTitle;
    }

    public void setNotifyTitle(String notifyTitle) {
        this.notifyTitle = notifyTitle;
    }

    public String getDownloadUrl() {

        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}