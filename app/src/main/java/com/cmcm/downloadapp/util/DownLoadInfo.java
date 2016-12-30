package com.cmcm.downloadapp.util;

import android.app.Notification;

/**
 * 下载信息类
 * Created by Jiangcz on 2016/12/29.
 */

public class DownLoadInfo {
    private int mUid = 0;//
    public int mInfo_res = 0;//
    public int mIcon_res = 0;//图标
    public int mView_res = 0;//
    public int mText_res = 0;//
    public int mProgress_text_res = 0;//进度
    public int mProgress_bar_res = 0;//进度条
    private boolean mNeedRename = false;//是否需要充命名
    public String mUrl = "";//下载地址
    private String mTitle_info = "";//下载标题信息
    private String mDownloadFilepath = "";//文件下载路径
    private FileDownloader mDownloader = null;//文件下载器
    private Notification mNotify = null;//通知

    public int mId_btn_ok = 0;
    public int mId_btn_cancel = 0;
    public int mId_layout_stop = 0;
    public int mLastPercent = 0;

    /** 判断下载信息是否完整 */
    public boolean isAvailable() {
        if(mInfo_res <= 0 && (mTitle_info == null || mTitle_info.length() < 1))
            return false;
        if(mIcon_res <= 0)
            return false;
        if(mView_res <= 0)
            return false;
        if(mText_res <= 0)
            return false;
        if(mProgress_text_res <= 0)
            return false;
        if(mProgress_bar_res <= 0)
            return false;
        if(mUrl == null || mUrl.length() < 5)
            return false;
        return true;
    }

    public int getmUid() {
        return mUid;
    }

    public void setmUid(int mUid) {
        this.mUid = mUid;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public int getmInfo_res() {
        return mInfo_res;
    }

    public void setmInfo_res(int mInfo_res) {
        this.mInfo_res = mInfo_res;
    }

    public int getmIcon_res() {
        return mIcon_res;
    }

    public void setmIcon_res(int mIcon_res) {
        this.mIcon_res = mIcon_res;
    }

    public int getmView_res() {
        return mView_res;
    }

    public void setmView_res(int mView_res) {
        this.mView_res = mView_res;
    }

    public int getmText_res() {
        return mText_res;
    }

    public void setmText_res(int mText_res) {
        this.mText_res = mText_res;
    }

    public int getmProgress_text_res() {
        return mProgress_text_res;
    }

    public void setmProgress_text_res(int mProgress_text_res) {
        this.mProgress_text_res = mProgress_text_res;
    }

    public int getmProgress_bar_res() {
        return mProgress_bar_res;
    }

    public void setmProgress_bar_res(int mProgress_bar_res) {
        this.mProgress_bar_res = mProgress_bar_res;
    }

    public boolean ismNeedRename() {
        return mNeedRename;
    }

    public void setmNeedRename(boolean mNeedRename) {
        this.mNeedRename = mNeedRename;
    }

    public String getmTitle_info() {
        return mTitle_info;
    }

    public void setmTitle_info(String mTitle_info) {
        this.mTitle_info = mTitle_info;
    }

    public String getmDownloadFilepath() {
        return mDownloadFilepath;
    }

    public void setmDownloadFilepath(String mDownloadFilepath) {
        this.mDownloadFilepath = mDownloadFilepath;
    }

    public FileDownloader getmDownloader() {
        return mDownloader;
    }

    public void setmDownloader(FileDownloader mDownloader) {
        this.mDownloader = mDownloader;
    }

    public Notification getmNotify() {
        return mNotify;
    }

    public void setmNotify(Notification mNotify) {
        this.mNotify = mNotify;
    }
}
