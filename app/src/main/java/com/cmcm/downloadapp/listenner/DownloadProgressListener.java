package com.cmcm.downloadapp.listenner;

/**
 * Created by Jiangcz on 2016/12/29.
 */

public interface DownloadProgressListener {
    void onDownloadSize(int mSize,int mAll);
    void onFinished();
}
