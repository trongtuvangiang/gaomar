package jp.gaomar.osakacitybus.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.gaomar.osakacitybus.BusStation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DBAdapter {
	
	  private static final String ROWID = "_id";
	  private static final String STID = "station_id";
	  private static final String NAME = "staion_name";
	  private static final String DATE = "date";	  
	  private static final String DB_NAME = "KeihanBus.db";
	  private static final String TABLE_STATION = "Station";
	  private static final String TABLE_HISTORY = "History";
	  
	  private static final int DB_VERSION = 2;
	  
	  private static final String CREATE_TABLE_STMT = "create table " + TABLE_STATION
	          + " (" + ROWID + " integer primary key autoincrement, " + STID
	          + " text, " + NAME + " text ); ";

	  private static final String CREATE_TABLE_HISTORY_STMT = "create table " + TABLE_HISTORY
	          + " (" + ROWID + " integer primary key autoincrement, " + NAME + " text, "
	          + DATE + " integer  ); ";

	  protected final Context context;
	  protected DatabaseHelper dbHelper;
	  private SQLiteDatabase db;
	 
	  public DBAdapter(Context context){
		    this.context = context;
		    dbHelper = new DatabaseHelper(this.context);
	  }
	  
	  private static class DatabaseHelper extends SQLiteOpenHelper {

		    public DatabaseHelper(Context context) {
		      super(context, DB_NAME, null, DB_VERSION);
		    }

		    @Override
		    public void onCreate(SQLiteDatabase db) {
		      db.execSQL(CREATE_TABLE_STMT);
		      db.execSQL(CREATE_TABLE_HISTORY_STMT);
		    }

		    @Override
		    public void onUpgrade(
		      SQLiteDatabase db,
		      int oldVersion,
		      int newVersion) {
				if( oldVersion == 1 && newVersion == 2 ){
					// 更新対象のテーブル
					List<String> targetList = new ArrayList<String>();
					List<List<String>> old_ColumnsList = new ArrayList<List<String>>();			
					db.beginTransaction();
					try {
						targetList.add(TABLE_STATION);
						
						final int num = targetList.size();
						// データベースバックアップ
						for (int ii=0; ii<num; ii++) {
							// 元カラム一覧
							old_ColumnsList.add(getColumns(db, targetList.get(ii)));
							
							// 退避
							db.execSQL("ALTER TABLE " + targetList.get(ii) + " RENAME TO temp_"
							+ targetList.get(ii));
												
						}
						
						// 新しいデータベース作成
						this.onCreate(db);
						
						// 新しいデータベースへ書き込む
						for (int ii=0; ii<num; ii++) {
							List<String> columns = old_ColumnsList.get(ii);
							List<String> newColumns = getColumns(db, targetList.get(ii));
							
							// 変化しないカラムのみ抽出
							columns.retainAll(newColumns);
							
							String cols = join(columns, ",");
							db.execSQL(String.format(
									"INSERT INTO %s (%s) SELECT %s from temp_%s", targetList.get(ii),
									cols, cols, targetList.get(ii)));
							// 終了処理
							db.execSQL("DROP TABLE temp_" + targetList.get(ii));
							
						}
						db.setTransactionSuccessful();
					} finally {
						db.endTransaction();
					}
				}
				
		    }
		    
		  }
		    
		  //
		  // Adapter Methods
		  //
		  
		  public DBAdapter open() {
		    db = dbHelper.getWritableDatabase();
		    return this;
		  }
		  
		  
		  public void close(){
		    dbHelper.close();
		  }
		  

		  //
		  // App Methods
		  //
		  
		  
		  public boolean deleteAllNotes(){
		    return db.delete(TABLE_STATION, null, null) > 0;
		  }
		  
		  public boolean deleteNote(int id){
		    return db.delete(TABLE_STATION, ROWID + "=" + id, null) > 0;
		  }
		  
		  public Cursor getAllNotes(){
			    return db.query(TABLE_STATION, null, null, null, null, null, null);
		  }

		  public Cursor getAllHistory(){
			    return db.query(TABLE_HISTORY, new String[] { ROWID, NAME }, null, null, null, null, DATE + " DESC");
		  }

		  public Cursor getOldHistory(){
			    return db.query(TABLE_HISTORY, new String[] { ROWID }, null, null, null, null, DATE + " ASC", "1");
		  }

		  public void saveNote(String id, String name){
		    ContentValues values = new ContentValues();
		    values.put(STID, id);
		    values.put(NAME, name);
		    db.insertOrThrow(TABLE_STATION, null, values);
		  }

		  public void saveHistory(String name) {
			  // データ数取得
			  int id = searchROWID(name);
			  Cursor c = getAllHistory();
			  Calendar now = Calendar.getInstance();
			  //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			  // 10以上は上書き更新
			  if (c.getCount() < 10) {
				  if (id < 0) {
				  	// 新規登録				  	
				    ContentValues values = new ContentValues();
				    values.put(NAME, name);
				    values.put(DATE, now.getTimeInMillis());
				    db.insertOrThrow(TABLE_HISTORY, null, values);
				  } else {
					// 既存上書き更新  
					  ContentValues values = new ContentValues();
					  values.put(DATE, now.getTimeInMillis());
					  db.update(TABLE_HISTORY, values, ROWID + " = ?", new String[]{ String.valueOf(id) });
				  }
			  } else {
				  // 既に登録された名前かどうか
				  if (id < 0) {
					  // 一番古いデータに上書き
					  c = getOldHistory();
					  if (c.moveToFirst()) {
						  int wkid = c.getInt(0);
						  
						  ContentValues values = new ContentValues();
						  values.put(NAME, name);
						  values.put(DATE, now.getTimeInMillis());
						  db.update(TABLE_HISTORY, values, ROWID + " = ?", new String[]{ String.valueOf(wkid) });
						  
					  }
				  } else {
						// 既存上書き更新  
					  ContentValues values = new ContentValues();
					  values.put(DATE, now.getTimeInMillis());
					  db.update(TABLE_HISTORY, values, ROWID + " = ?", new String[]{ String.valueOf(id) });
					  
				  }
			  }
			  c.close();
			    
		  }

		  public ContentValues getContentValues(String id, String name){
			    ContentValues values = new ContentValues();
			    values.put(STID, id);
			    values.put(NAME, name);

			    return values;
		  }

		  public String searchID(String name) {
			  String ret = "";
			  
			  final String[] columns = new String[]{STID};
			  String where = NAME + " like ?";
			  
			  Cursor c = db.query(TABLE_STATION, columns, where, new String[]{name}, null, null, null);
			  if (c.moveToFirst()) {
				  ret = c.getString(0);
			  }
			  c.close();
			  
			  return ret;
		  }

		  public int searchROWID(String name) {
			  int ret = -1;
			  
			  final String[] columns = new String[]{ROWID};
			  String where = NAME + " like ?";
			  
			  Cursor c = db.query(TABLE_HISTORY, columns, where, new String[]{name}, null, null, null);
			  if (c.moveToFirst()) {
				  ret = c.getInt(0);
			  }
			  c.close();
			  
			  return ret;
		  }

		  public ArrayList<BusStation> getBusStationList(String name) {
			  ArrayList<BusStation> retList = new ArrayList<BusStation>();
			  			  
			  String sql = "SELECT " + STID + "," + NAME + " FROM " + TABLE_STATION + " WHERE " + NAME + " LIKE '%" + name + "%' ";
			  Cursor c = db.rawQuery(sql, null);
			  if (c.moveToFirst()) {
				  for (int ii = 0; ii < c.getCount(); ii++) {
					  String stationId = c.getString(0);
					  String stationName = c.getString(1);
					  BusStation data = new BusStation(stationId, stationName);
					  retList.add(data);
					  c.moveToNext();
				  }
			  }
			  c.close();

			  return retList;
		  }
		  
		  public ArrayList<BusStation> getHistoryList() {
			  ArrayList<BusStation> retList = new ArrayList<BusStation>();
			  			  
			  Cursor c = getAllHistory();
			  if (c.moveToFirst()) {
				  for (int ii = 0; ii < c.getCount(); ii++) {
					  String stationId = c.getString(0);
					  String stationName = c.getString(1);
					  BusStation data = new BusStation(stationId, stationName);
					  retList.add(data);
					  c.moveToNext();
				  }
			  }
			  c.close();

			  return retList;
		  }

		    /** 
		     * insert a lot of data 
		     * 
		     * @param nullColumnHack  
		     * @param valueList 値のリスト 
		     * @param conflictAlgorithm コンフリクト発生時の処理 
		     * @param transaction トランザクション処理を併用するか否か 
		     * @return Boolean 成功 or 失敗 
		     */  
		    public Boolean insertMany(String nullColumnHack, List<ContentValues> valueList, int conflictAlgorithm, Boolean transaction) {  
		          
		        if(valueList != null && valueList.size() > 0){  
		            String[] CONFLICT_VALUES = new String[]{"", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE "};  
		      
		            // At first, create sql statement  
		            ContentValues initialValues = valueList.get(0);  
		              
		            // Measurements show most sql lengths <= 152  
		            StringBuilder sql_build = new StringBuilder(152);  
		            sql_build.append("INSERT");  
		            sql_build.append(CONFLICT_VALUES[conflictAlgorithm]);  
		            sql_build.append(" INTO ");  
		            sql_build.append(TABLE_STATION);  
		            // Measurements show most values lengths < 40  
		            StringBuilder values = new StringBuilder(40);  
		      
		            Set<Map.Entry<String, Object>> entrySet = null;  
		              
		            if (initialValues != null && initialValues.size() > 0) {  
		                entrySet = initialValues.valueSet();  
		                Iterator<Map.Entry<String, Object>> entriesIter = entrySet.iterator();  
		                sql_build.append('(');  
		      
		                boolean needSeparator = false;  
		                while (entriesIter.hasNext()) {  
		                    if (needSeparator) {  
		                        sql_build.append(", ");  
		                        values.append(", ");  
		                    }  
		                    needSeparator = true;  
		                    Map.Entry<String, Object> entry = entriesIter.next();  
		                    sql_build.append(entry.getKey());  
		                    values.append('?');  
		                }  
		                sql_build.append(')');  
		            } else {  
		                sql_build.append("(" + nullColumnHack + ") ");  
		                values.append("NULL");  
		            }  
		      
		            sql_build.append(" VALUES(");  
		            sql_build.append(values);  
		            sql_build.append(");");  
		            String sql = sql_build.toString();  
		              
		            SQLiteStatement statement = null;  
		              
		            // if transaction id true, beginTransaction()  
		            if(transaction){  
		                db.beginTransaction();  
		            }  
		            try {  
		                for(int i=0,length=valueList.size(); i<length; i++){  
		                    statement = db.compileStatement(sql);  
		                    initialValues = valueList.get(i);  
		                    entrySet = initialValues.valueSet();  
		                      
		                    // Bind the values  
		                    if (entrySet != null) {  
		                        int size = entrySet.size();  
		                        Iterator<Map.Entry<String, Object>> entriesIter = entrySet.iterator();  
		                        for (int j = 0; j < size; j++) {  
		                            Map.Entry<String, Object> entry = entriesIter.next();  
		                            DatabaseUtils.bindObjectToProgram(statement, j + 1, entry.getValue());  
		                        }  
		                    }  
		                    // Run the program and then cleanup  
		                    statement.execute();  
		                    statement.close();  
		                }  
		                  
		                // if transaction id true, setTransactionSuccessful()  
		                if(transaction){  
		                    db.setTransactionSuccessful();  
		                    db.endTransaction();  
		                    transaction = false;  
		                }  
		                return true;  
		            } catch (SQLiteDatabaseCorruptException e) {  
		                throw e;  
		            } finally {  
		                if (statement != null) {  
		                    statement.close();  
		                }  
		                // if transaction id true, endTransaction()  
		                if(transaction){  
		                    db.endTransaction();  
		                }  
		            }  
		        }  
		        return false;  
		    }  
		    
			/**
			 * 指定テーブルのカラム名リストを取得する
			 * @param db
			 * @param tableName
			 * @return カラム名リスト
			 */
			private static List<String> getColumns(SQLiteDatabase db, String tableName) {
				List<String> ar = null;
				Cursor c = null;
				try {
					c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
					if (c != null) {
						ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
					}
				} finally {
					if (c != null) {
						c.close();
					}
				}
				return ar;
			}
			
			/**
			 * 文字列を任意の区切り文字で連結する
			 * @param list
			 * @param delim 区切り文字
			 * @return 連結後の文字列
			 */
			private static String join(List<String> list, String delim) {
				final StringBuilder buf = new StringBuilder();
				final int num = list.size();
				for (int ii = 0; ii < num; ii++) {
					if (ii != 0) {
						buf.append(delim);
					}
					buf.append((String) list.get(ii));
				}
				return buf.toString();
			}
			
}
