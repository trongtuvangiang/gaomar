package jp.gaomar.astronumbertouch.db;

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DBCommon {

    // テーブル名
    private static final String TABLE = "score";

	/**
	 * ベストタイム取得
	 * @param context
	 * @return
	 */
	public static long getScore(Context context, int mode) {
        StringBuffer sql = new StringBuffer();
        sql.append(" select");
        sql.append(" score");
        sql.append(" from " + TABLE);
        sql.append(" where ");
        sql.append(" mode = " + mode);        
        sql.append(";");

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = null;
        try {
			dbHelper.createEmptyDataBase();
			db = dbHelper.openDataBase();
		} catch (IOException e) {
		}

		Cursor cursor = null;
        long ret = 0;
        try{
            cursor = db.rawQuery(sql.toString(), null);  
            if (cursor.moveToNext()) {
                ret = cursor.getLong(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        if (ret > 0) {
            return ret;
        }

		return ret;
		
	}

	/**
	 * ハイスコアの値を更新
	 * @param context
	 * @param score
	 * @return
	 */
    public static boolean updateScore(Context context, int mode, long score) {
        String[] whereArgs = {Integer.toString(mode)};

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = null;
        try {
			dbHelper.createEmptyDataBase();
			db = dbHelper.openDataBase();
		} catch (IOException e) {
		}

		int ret;
        
        ContentValues cv = new ContentValues();
        cv.put("score", score);
        
        try {
        	ret = db.update(TABLE, cv, "mode = ?", whereArgs);
        } finally {
            db.close();
        }
        if (ret == -1) {
        	return false;
        }

		return true;
    	
    }

}
