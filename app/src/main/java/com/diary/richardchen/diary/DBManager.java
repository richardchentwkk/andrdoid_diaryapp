package com.diary.richardchen.diary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by RichardChen on 2015/3/10.
 */
public class DBManager extends SQLiteOpenHelper {
    private volatile static DBManager dbManager = null;
    private final static String DATABASE_NAME = "test123diary.db";
    private final static int DATABASE_VERSION = 1;
    public static abstract class DiaryContent implements BaseColumns {
        public static final String TABLE_NAME = "diary";
        public static final String CREATE_ID = "_id";
        public static final String CREATE_TIME = "datetime";
        public static final String CREATE_CONTENT = "content";
    }
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DiaryContent.TABLE_NAME;
    private String sql = "CREATE TABLE IF NOT EXISTS "+DiaryContent.TABLE_NAME+"("+
                    DiaryContent.CREATE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    DiaryContent.CREATE_TIME+" TEXT,"+ DiaryContent.CREATE_CONTENT+" TEXT"+
                    ")";
    private SQLiteDatabase database;

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
        Log.d("Richard","Create database success"+db.getPath());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public boolean insert(String datetime, String content){
        Log.d("Richard", "inset content right now");
        ContentValues values = new ContentValues();
        values.put(DiaryContent.CREATE_TIME, datetime);
        values.put(DiaryContent.CREATE_CONTENT, content);
        long newid = database.insert(DiaryContent.TABLE_NAME,null,values);
        if (newid>=1)return true;
        else return false;
    }

    public static DBManager getDbManager(Context context){
        if(dbManager==null){
            synchronized (DBManager.class) {
                if (dbManager==null) {
                    dbManager = new DBManager(context, null, null, 1);
                }
            }
        }
        return dbManager;
    }

    public Cursor select(){
        Cursor cursor = database.query(DiaryContent.TABLE_NAME, null, null, null, null, null, DiaryContent.CREATE_ID+" DESC");
        return cursor;
    }

    public void update(int id, String content){
        ContentValues values = new ContentValues();
        values.put(DiaryContent.CREATE_CONTENT, content);
        database.update(DiaryContent.TABLE_NAME, values, DiaryContent.CREATE_ID+"="+Integer.toString(id), null);
    }

    public String getContentbyID(int id){
        Cursor returncursor = database.query(DiaryContent.TABLE_NAME,null,DiaryContent.CREATE_ID+"="+id,null,null,null,null);
        returncursor.moveToFirst();
        return returncursor.getString(returncursor.getColumnIndexOrThrow(DiaryContent.CREATE_CONTENT));
    }

    public String returndatebyID(int id){
        Cursor returncursor = database.query(DiaryContent.TABLE_NAME,null,DiaryContent.CREATE_ID+"="+id,null,null,null,null);
        returncursor.moveToFirst();
        return returncursor.getString(returncursor.getColumnIndexOrThrow(DiaryContent.CREATE_TIME));
    }
    public Cursor filter_query(CharSequence constraint){
        String values = constraint.toString();
        Cursor returncur = database.query(DiaryContent.TABLE_NAME,null,DiaryContent.CREATE_CONTENT+" LIKE ?",new String[]{"%"+values+"%"},null, null,DiaryContent.CREATE_ID+" DESC");
        return returncur;
    }

    public Cursor FilterByDate (String date){
        return database.query(DiaryContent.TABLE_NAME,null,DiaryContent.CREATE_TIME+"=?",new String[]{date},null, null,DiaryContent.CREATE_ID+" DESC");
    }

    public void close(){
        Log.d("Richard", "Close database"+database);
        database.close();
    }
    public void delete(int id){
        database.delete(DiaryContent.TABLE_NAME, DiaryContent.CREATE_ID+"="+Integer.toString(id), null);
    }
    public void remove_all_data(){
        database.execSQL(SQL_DELETE_ENTRIES);
        Log.d("Richard","Remove all data and create new table");
        database.execSQL(sql);
    }
}
