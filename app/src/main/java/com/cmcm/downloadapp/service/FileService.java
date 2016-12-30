package com.cmcm.downloadapp.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cmcm.downloadapp.db.DBData;
import com.cmcm.downloadapp.db.DBOpenHelper;

import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Created by Jiangcz on 2016/12/29.
 */
public class FileService {
    private DBOpenHelper mHelper;
    private final ReentrantLock mLock = new ReentrantLock();
    public FileService(Context pContext){
        mHelper=DBOpenHelper.getInstance(pContext);
    }
    /**
     * 文件下载完成，删除对应的下载记录
     */
    public void delete(String pPath){
        mLock.lock();
        try {
            SQLiteDatabase _SD=mHelper.getReadableDatabase();
            _SD.execSQL(DBData.FileDownload_COLUMNS.SQL_DELETE_FILEDOWNLOAD,new Object[]{pPath});
        } finally {
            mLock.unlock();
        }
    }
    /**
     * 操作结束后，统一关闭数据库
     */
    public void closeDB(){
        mHelper.close();
    }
}
