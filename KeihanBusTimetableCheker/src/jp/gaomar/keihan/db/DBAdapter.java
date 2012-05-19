package jp.gaomar.keihan.db;

import java.util.ArrayList;

import jp.gaomar.keihan.BusStation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
}
