package com.cmcm.downloadapp.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.cmcm.downloadapp.listenner.DownloadProgressListener;
import com.cmcm.downloadapp.util.DownLoadInfo;
import com.cmcm.downloadapp.util.FileDownloader;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载服务
 * Created by Jiangcz on 2016/12/29.
 * 1:onCreate():创建NotificationManager，初始化下载信息mList
 * 2:onStartCommand():
 *  1):开始下载：下载前，获取下载信息DownLoadInfo，若下载信息可用，创建下载文件存放位置；
 *               下载时，在分线程中进行，先获取下载进度，再进行文件下载
 */
public class DownLoadService extends Service {
    /** 下载开始与停止 */
    private static final String DOWNLOAD_ACTION_START = "DOWNLOAD_START";
    private static final String DOWNLOAD_ACTION_STOP = "DOWNLOAD_STOP";
    /** 下载进度 */
    private static final int HANDLE_PRECENT = 0x1;
    private static final int HANDLE_FINISHED = 0x2;
    private static final int NOTIFY_ID = 0x4362;
    private Context mContext;
    private NotificationManager mManager=null;
    private List<DownLoadInfo> mList;
    private boolean mIsRunning = false;
    private int mDownIndex=0;
    private Handler mHandler=new Handler(){
        WeakReference<DownLoadService> _WR=new WeakReference<DownLoadService>(DownLoadService.this);
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    public DownLoadService(Context pContext,DownLoadInfo pDinfo) {
        mContext=pContext;
        mContext.startService(getIntent(pDinfo));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);//获得系统通知管理器
        mList=new ArrayList<DownLoadInfo>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()){
            case DOWNLOAD_ACTION_START://开始下载
                DownLoadInfo _Info=getDownLoadInfo(intent);
                if(mIsRunning){//如果
                    for(DownLoadInfo pInfo:mList){
                        if(pInfo.mUrl.equals(_Info.mUrl)){
                            return super.onStartCommand(intent, flags, startId);
                        }
                    }
                }
                mIsRunning=true;
                if(_Info.isAvailable()){//如果下载信息完整
                    File _Path=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/download");
                    if(_Path.isDirectory()||!_Path.exists()){
                        _Path.mkdirs();
                    }
                    _Info.setmUid(mDownIndex);
                    mDownIndex++;
                    download(_Info,_Path);
                }
                break;
            case DOWNLOAD_ACTION_STOP://停止下载

                break;
            default:break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void download(final DownLoadInfo pInfo, final File pPath) {
        //下载在分线程中执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadProgressListener _Listener=new DownloadProgressListener() {
                    @Override
                    public void onDownloadSize(int mSize, int mAll) {
                        //计算下载进度百分比
                        float _Num=mSize/mAll;
                        int _Percent= (int) (_Num*100);
                        if(_Percent>=100&&mSize<mAll){
                            _Percent=99;
                            Log.e("_Percent",_Percent+"");
                        }
                        if(_Percent!=pInfo.mLastPercent){
                            pInfo.mLastPercent=_Percent;
                            Message _Message=new Message();
                            _Message.what=HANDLE_PRECENT;
                            _Message.obj=pInfo;
                            _Message.getData().putInt("percent",_Percent);
                            mHandler.sendMessage(_Message);
                        }
                    }
                    @Override
                    public void onFinished() {
                        Message _Message=new Message();
                        _Message.what=HANDLE_FINISHED;
                        _Message.obj=pInfo;
                        mHandler.sendMessage(_Message);
                    }
                };
                FileDownloader _FileDownloader=new FileDownloader(mContext,pInfo.getmUrl(),pPath,1,_Listener);//文件下载器
                File _File=_FileDownloader.getmSaveFile();
                if(_File==null){
                    onDownloadStopted(null);
                    return;
                }
                pInfo.setmDownloader(_FileDownloader);
                pInfo.setmDownloadFilepath(_File.getAbsolutePath());

                mList.add(pInfo);
                //_FileDownloader.download();

            }
        }).start();
    }

    /**
     * 开始下载文件
     * @param
     */
    public int download(){
        return 0;
    }
    private void onDownloadStopted(DownLoadInfo pInfo){
        if(pInfo!=null){
            mManager.cancel(NOTIFY_ID+pInfo.getmUid());
            mList.remove(pInfo);
        }
        if(mList.size()<1){
            stopSelf();
        }
    }
    private Intent getIntent(DownLoadInfo pDinfo){
        Intent _Intent=new Intent(mContext,DownLoadService.class);
        _Intent.putExtra("url", pDinfo.mUrl);
        _Intent.putExtra("info", pDinfo.mInfo_res);
        _Intent.putExtra("icon", pDinfo.mIcon_res);
        _Intent.putExtra("text", pDinfo.mText_res);
        _Intent.putExtra("view", pDinfo.mView_res);
        _Intent.putExtra("title", pDinfo.getmTitle_info());
        _Intent.putExtra("progress", pDinfo.mProgress_bar_res);
        _Intent.putExtra("protext", pDinfo.mProgress_text_res);
        _Intent.putExtra("id_btn_ok", pDinfo.mId_btn_ok);
        _Intent.putExtra("id_btn_cancel", pDinfo.mId_btn_cancel);
        _Intent.putExtra("id_layout_stop", pDinfo.mId_layout_stop);
        return _Intent;
    }
    private DownLoadInfo getDownLoadInfo(Intent pIntent){
        DownLoadInfo _Info=new DownLoadInfo();
        _Info.mUrl=pIntent.getStringExtra("url");
        _Info.mInfo_res=pIntent.getIntExtra("info",0);
        _Info.mIcon_res=pIntent.getIntExtra("icon",0);
        _Info.mText_res=pIntent.getIntExtra("text",0);
        _Info.mView_res=pIntent.getIntExtra("view",0);
        _Info.mProgress_bar_res=pIntent.getIntExtra("progress",0);
        _Info.mProgress_text_res=pIntent.getIntExtra("protext",0);
        _Info.mId_btn_ok=pIntent.getIntExtra("id_btn_ok",0);
        _Info.mId_btn_cancel=pIntent.getIntExtra("id_btn_cancel",0);
        _Info.mId_layout_stop=pIntent.getIntExtra("d_layout_stop",0);
        _Info.setmTitle_info(pIntent.getStringExtra("title"));
        _Info.mLastPercent=0;
        return _Info;
    }
}
