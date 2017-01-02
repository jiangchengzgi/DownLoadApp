package com.cmcm.downloadapp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseIntArray;

import com.cmcm.downloadapp.listenner.DownloadProgressListener;
import com.cmcm.downloadapp.service.FileService;
import com.cmcm.downloadapp.thread.DownloadThread;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		getConnection(pDownloadUrl);//根据url创建网络连接
		if(!pSaveFileDir.exists()){
			pSaveFileDir.mkdirs();
		}
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
	 * 下载文件
	 */
	private void downloadFile(HttpURLConnection pConnection,String pReferer,File pPath){
		try {
			pConnection.setRequestMethod("GET");
			pConnection.setConnectTimeout(60*1000);
			/** 设置请求头属性 */
			pConnection.setRequestProperty("Accept","*/*");//接收文件类型是任意类型
			pConnection.setRequestProperty("Referer",pReferer);//告诉服务器我是从哪个链接过来的
			pConnection.setRequestProperty("Connection","Keep-Alive");//建议保持长时间连接
			pConnection.connect();
			int _ResponseCode=pConnection.getResponseCode();
			if(_ResponseCode==200){
				mFileSize=pConnection.getContentLength();//获得文件大小(inputStream.available())
				if(mFileSize<0){
					return;
				}
				getFileName(pConnection,true);
				mSaveFile=new File(pPath,mDownloadFileName);
				SparseIntArray _ThreadDownloadSize=mFService.getData(mDownloadUrl);
				/** 将各线程下载的长度缓存在mThreadDownloadSizes中*/
				int _Size=_ThreadDownloadSize.size();
				for(int i=0;i<_Size;i++){
					mThreadDownloadSizes.put(_ThreadDownloadSize.keyAt(i),_ThreadDownloadSize.valueAt(i));
				}
				/** 计算所有线程下载的文件长度总和 */
				if(mThreadDownloadSizes.size()==mThreads.length){
					for(int i=0;i<mThreadDownloadSizes.size();i++){
						mDownloadSize+=mThreadDownloadSizes.get(i+1);
					}
				}
				/** 每条线程下载的文件长度 */
				mThreadDownloadSize=(mFileSize%mThreads.length)==0?mFileSize/mThreads.length:mFileSize/mThreads.length+1;
			}
			else if(_ResponseCode==302||_ResponseCode==301){
				getFileName(pConnection,false);
				String _Location=pConnection.getHeaderField("location");
				HttpURLConnection _Connection= (HttpURLConnection) new URL(_Location).openConnection();
				_Connection.setInstanceFollowRedirects(true);
				downloadFile(_Connection,_Location,pPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据网络连接对象获取文件名
	 */
	public void getFileName(HttpURLConnection pConnection,boolean pLast){
		String _FileName="";
		for(int i=0;;i++){
			String _Header=pConnection.getHeaderField(i);//获取请求头
			if(_Header==null){
				break;
			}
			if("content-disposition".equals(pConnection.getHeaderFieldKey(i).toLowerCase())){
				Matcher _Matcher= Pattern.compile(".*=_FileName=(.*)").matcher(_Header.toLowerCase());
				if(_Matcher.find()){
					_FileName=_Matcher.group(1).replace("\"","");
					break;
				}
			}
		}
		if(_FileName!=null&&!_FileName.isEmpty()){
			mDownloadFileName=_FileName;
		}
		if(!pLast){
			return;
		}
		if(!mDownloadFileName.isEmpty()){//mDownloadFileName不为空，结束
			return;
		}
		_FileName=mDownloadUrl.substring(mDownloadUrl.lastIndexOf('/')+1);
		if(_FileName==null||_FileName.trim().isEmpty()||_FileName.contains("?")){
			_FileName= UUID.randomUUID().toString();
		}
		mDownloadFileName=_FileName;
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