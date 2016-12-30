package com.cmcm.downloadapp.db;

/**
 * 数据库
 * Created by Jiangcz on 2016/12/29.
 */

public class DBData {
    /** SDK 本地数据库名 */
    public static final String DB_NAME = "app_download_db";
    /** SDK 本地数据库的版本号,用于数据升级 */
    public static final int DB_VERSION = 1;
    /* ====== SQL BEGIN =======*/
    /** 文件下载表 */
    public static final class FileDownload_COLUMNS{
        /**  表名 */
        public static final String TABLE_NAME="filedownload";
        /**  ID */
        public static final String ID="_id";
        /**  文件下载路径 */
        public static final String FILE_DOWNLOAD_PATH="filedownloadpath";
        /** 线程id */
        public static final String THREAD_ID="threadid";
        /**  下载文件长度 */
        public static final String DOWNLOAD_LENGTH="downloadlength";
        /** 创建SQL表 */
        public static final String SQL_CREATE_FILEDOWNLOAD="CREATE TABLE IF NOT EXISTS "
                +TABLE_NAME+"("
                +ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +FILE_DOWNLOAD_PATH+" VARCHAR(100),"
                +THREAD_ID+" INTEGER,"
                +DOWNLOAD_LENGTH+" INTEGER)";
        /** 删除SQL表 */
        public static final String SQL_DROP_FILEDOWNLOAD="DROP TABLE IF EXISTS "
                +TABLE_NAME;
        /** 根据文件下载路径删除SQL表记录 */
        public static final String SQL_DELETE_FILEDOWNLOAD="DELETE FROM "+TABLE_NAME+" WHERE "+FILE_DOWNLOAD_PATH+"=?";
    }
    /* ====== SQL END =======*/
}
