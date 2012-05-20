package jp.gaomar.keihan.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.gaomar.keihan.BusStation;

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
	  private static final String DB_NAME = "KeihanBus.db";
	  private static final String TABLE = "Station";
	  private static final int DB_VERSION = 1;
	  private static final String CREATE_TABLE_STMT = "create table " + TABLE
	          + " (" + ROWID + " integer primary key autoincrement, " + STID
	          + " text, " + NAME + " text ); ";
	 
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
		    }

		    @Override
		    public void onUpgrade(
		      SQLiteDatabase db,
		      int oldVersion,
		      int newVersion) {
		      db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		      onCreate(db);
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
		    return db.delete(TABLE, null, null) > 0;
		  }
		  
		  public boolean deleteNote(int id){
		    return db.delete(TABLE, ROWID + "=" + id, null) > 0;
		  }
		  
		  public Cursor getAllNotes(){
			    return db.query(TABLE, null, null, null, null, null, null);
		  }
		  
		  public void saveNote(String id, String name){
		    ContentValues values = new ContentValues();
		    values.put(STID, id);
		    values.put(NAME, name);
		    db.insertOrThrow(TABLE, null, values);
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
			  
			  Cursor c = db.query(TABLE, columns, where, new String[]{name}, null, null, null);
			  if (c.moveToFirst()) {
				  ret = c.getString(0);
			  }
			  c.close();
			  
			  return ret;
		  }
		  
		  public ArrayList<BusStation> getBusStationList(String name) {
			  ArrayList<BusStation> retList = new ArrayList<BusStation>();
			  			  
			  String sql = "SELECT " + STID + "," + NAME + " FROM " + TABLE + " WHERE " + NAME + " LIKE '%" + name + "%' ";
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
		            sql_build.append(TABLE);  
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
}
