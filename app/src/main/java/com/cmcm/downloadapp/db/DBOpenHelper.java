package com.cmcm.downloadapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库工具类
 * Created by Jiangcz on 2016/12/29.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
	private static DBOpenHelper mHelper = null;
	/** DBOpenHelper单例模式调用 */
	public static DBOpenHelper getInstance(Context pContext) {
		if (mHelper == null) {
			synchronized (DBOpenHelper.class) {
				if (mHelper == null) {
					mHelper = new DBOpenHelper(pContext.getApplicationContext());
				}
			}
		}
		return mHelper;
	}
	private DBOpenHelper(Context context) {
		super(context, DBData.DB_NAME, null, DBData.DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase pDB) {
		pDB.execSQL(DBData.FileDownload_COLUMNS.SQL_CREATE_FILEDOWNLOAD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase pDB, int pOldVersion, int pNewVersion) {
		pDB.execSQL(DBData.FileDownload_COLUMNS.SQL_DROP_FILEDOWNLOAD);
		onCreate(pDB);
	}
}