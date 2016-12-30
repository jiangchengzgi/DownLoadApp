package com.cmcm.downloadapp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseIntArray;

import com.cmcm.downloadapp.listenner.DownloadProgressListener;
import com.cmcm.downloadapp.service.FileService;
import com.cmcm.downloadapp.thread.DownloadThread;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件下载工具类
 * Created by Jiangcz on 2016/12/29.
 */
@SuppressLint("DefaultLocale")
public class FileDownloader {
	private Context mContext;
	private FileService mFService;
	private DownloadProgressListener mDownloadListenner;
	private String mDownloadFileName="";
	/** 已下载文件长度 */
	private int mDownloadSize=0;
	/** 文件大小 */
	private int mFileSize=0;
	/** 下载线程 */
	private DownloadThread[] mThreads;
	/** 本地保存文件 */
	private File mSaveFile;
	/** 缓存各线程下载的长度 */
	private SparseIntArray mThreadDownloadSizes= new SparseIntArray();
	/** 每条线程下载的长度 */
	private int mThreadDownloadSize;
	/** 已完成下载的线程数 */
	private int mFinishDownloadThreadCount=0;
	/** 下载路径 */
	private String mDownloadUrl;

	public FileDownloader(Context pContext,String pDownloadUrl,File pSaveFileDir,int pThreaNum,DownloadProgressListener pDownloadListener){
		this.mContext=pContext;
		this.mDownloadUrl=pDownloadUrl;
		this.mDownloadListenner=pDownloadListener;
		this.mThreads=new DownloadThread[pThreaNum];
		mFService=new FileService(mContext);
		getConnection(pDownloadUrl);
		if(!pSaveFileDir.exists()){
			pSaveFileDir.mkdirs();
		}
		mDownloadFileName="";
	}
	/**
	 * 建立网络连接
	 */
	private void getConnection(String pURL){
		try {
			URL _URL=new URL(pURL);
			HttpURLConnection _Connection= (HttpURLConnection) _URL.openConnection();
			_Connection.setInstanceFollowRedirects(false);//连接不遵循重定向
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 累计下载大小
	 */
	public synchronized void appendDownloadSize(int pSize){
		mDownloadSize+=pSize;
		//如果下载完成，删除下载记录并关闭数据库
		if(mDownloadSize==mFileSize){
			mFService.delete(mDownloadUrl);
			mFService.closeDB();
		}
		//如果没有下载完成，通知已经下载了多少
		if(mDownloadListenner!=null){
			mDownloadListenner.onDownloadSize(mDownloadSize,mFileSize);
		}
	}

	/**
	 * 停止下载
	 * @return
     */

	public int getmDownloadSize() {
		return mDownloadSize;
	}

	public void setmDownloadSize(int mDownloadSize) {
		this.mDownloadSize = mDownloadSize;
	}

	public int getmFileSize() {
		return mFileSize;
	}

	public void setmFileSize(int mFileSize) {
		this.mFileSize = mFileSize;
	}

	public DownloadThread[] getmThreads() {
		return mThreads;
	}

	public void setmThreads(DownloadThread[] mThreads) {
		this.mThreads = mThreads;
	}

	public File getmSaveFile() {
		return mSaveFile;
	}

	public void setmSaveFile(File mSaveFile) {
		this.mSaveFile = mSaveFile;
	}

	public SparseIntArray getmThreadDownloadSizes() {
		return mThreadDownloadSizes;
	}

	public void setmThreadDownloadSizes(SparseIntArray mThreadDownloadSizes) {
		this.mThreadDownloadSizes = mThreadDownloadSizes;
	}

	public int getmThreadDownloadSize() {
		return mThreadDownloadSize;
	}

	public void setmThreadDownloadSize(int mThreadDownloadSize) {
		this.mThreadDownloadSize = mThreadDownloadSize;
	}

	public int getmFinishDownloadThreadCount() {
		return mFinishDownloadThreadCount;
	}

	public void setmFinishDownloadThreadCount(int mFinishDownloadThreadCount) {
		this.mFinishDownloadThreadCount = mFinishDownloadThreadCount;
	}

	public String getmDownloadUrl() {
		return mDownloadUrl;
	}

	public void setmDownloadUrl(String mDownloadUrl) {
		this.mDownloadUrl = mDownloadUrl;
	}
}