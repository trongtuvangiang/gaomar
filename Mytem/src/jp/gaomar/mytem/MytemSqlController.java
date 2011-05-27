package jp.gaomar.mytem;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.sql.SQLException;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteへの読み書きをするクラスです
 * 
 * @author hide
 */
public class MytemSqlController {
	private static final int DB_VERSION = 1;
	private static final String MYTEMHISTORYTABLENAME = "MytemHistory";

	/** テーブルCreate文 */
	private static final String CREATE_NOODLEHISTORY_TABLE = "CREATE TABLE "
			+ MYTEMHISTORYTABLENAME + "("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, jancode TEXT,"
			+ "itemname TEXT , "
			+ "shopname TEXT ,price INTEGER ,postdate TEXT, note TEXT)";

	/** DB読み書きクラス */
	private static SQLiteDatabase database = null;
	private Context context;

	/**
	 * コンストラクタ
	 * 
	 * @param context
	 * @param directory
	 */
	public MytemSqlController(Context context) {
		this.context = context;
		if (database == null) {
			DataBaseOpenHelper helper = new DataBaseOpenHelper(context);
			database = helper.getWritableDatabase();
		}
	}
	
	/**
	 * SQLiteから最新の引数件数の商品履歴を得ます
	 * 
	 * @param rows
	 * @return
	 * @throws SQLException
	 */
	public List<MytemHistory> getMytemHistories(int rows) throws SQLException {
		List<MytemHistory> histories = new ArrayList<MytemHistory>();
		String[] columns = {"_id", "itemname", "jancode", "shopname", "price", "postdate" , "note"};
		String orderby = "_id desc";
		Cursor cursor = null;
		try {
			// 検索
			cursor = database.query(MYTEMHISTORYTABLENAME, columns, null,
					null, null, null, orderby, Integer.toString(rows));
			if (cursor == null) {
				throw new SQLException("cursor is null");
			}
			while (cursor.moveToNext()) {
				histories.add(createMytemHistory(cursor));
			}
			return histories;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * SQLiteからJanCodeのLIKE検索で履歴を得ます
	 * @param janCode
	 * @return
	 * @throws SQLException
	 */
	public List<MytemHistory> getMytemHistories(String janCode)
			throws SQLException {
		List<MytemHistory> histories = new ArrayList<MytemHistory>();
		String[] columns = { "jancode", "itemname", "shopname", "price", "postdate" , "note"};
		String where = "jancode LIKE ?";
		String[] whereArgs = {"%" + janCode + "%"};
		String orderby = "postdate desc";
		Cursor cursor = null;
		try {
			// 検索
			cursor = database.query(MYTEMHISTORYTABLENAME, columns, where,
					whereArgs, null, null, orderby);
			if (cursor == null) {
				throw new SQLException("cursor is null");
			}
			while (cursor.moveToNext()) {
				histories.add(createMytemHistory(cursor));
			}
			return histories;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * SQLiteからすべての商品履歴を得ます
	 * 
	 * @return
	 * @throws SQLiteException
	 */
	public List<MytemHistory> getNoodleHistories() throws SQLException {
		List<MytemHistory> histories = new ArrayList<MytemHistory>();
		String[] columns = { "jancode", "shopname", "price", "postdate", "note" };
		String orderby = "postdate desc";
		Cursor cursor = null;
		try {
			// 検索
			cursor = database.query(MYTEMHISTORYTABLENAME, columns, null,
					null, null, null, orderby);
			if (cursor == null) {
				throw new SQLException("cursor is null");
			}
			while (cursor.moveToNext()) {
				histories.add(createMytemHistory(cursor));
			}
			return histories;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	/**
	 * カーソルから履歴を作成する
	 * 
	 * @param cursor
	 * @return
	 */
	private MytemHistory createMytemHistory(Cursor cursor)
			throws SQLException {
		try {
			String jancode = cursor.getString(cursor.getColumnIndex("jancode"));
			String itemname = cursor.getString(cursor.getColumnIndex("itemname"));
			String shopname = cursor.getString(cursor.getColumnIndex("shopname"));
			String postdateString = cursor.getString(cursor
					.getColumnIndex("postdate"));
			Date postdate = null;
			try {
				postdate = MytemHistory.getSimpleDateFormat().parse(
						postdateString);
			} catch (ParseException e) {
				// 絶対にExceptionは出ない
				e.printStackTrace();
			}
			String note = cursor.getString(cursor
					.getColumnIndex("note"));
			int price = cursor.getInt(cursor.getColumnIndex("price"));
			return new MytemHistory(jancode, itemname, shopname, price, postdate, note);
		} catch (SQLiteException ex) {
			throw new SQLException(ExceptionToStringConverter.convert(ex));
		}
	}

	/**
	 * 引数のJancode, 商品名,店舗名、値段、登録日履歴を作成します
	 * 
	 * @throws SQLException
	 */
	public void createHistory(
			String jancode,
			String itemname,
			MytemHistory mytemHistory) throws SQLException {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put("jancode", jancode);
			contentValues.put("itemname", itemname);
			contentValues.put("shopname", mytemHistory.getShopName());
			contentValues.put("price", Integer.toString(mytemHistory.getPrice()));
			contentValues.put("postdate", MytemHistory
					.getSimpleDateFormat().format(mytemHistory.getPostDate()));
			contentValues.put("note", mytemHistory.getNote());
			long ret = database.insert(MYTEMHISTORYTABLENAME, null,
					contentValues);
			if (ret < 0) {
				throw new SQLException("insert return value = " + ret);
			}
		} catch (SQLiteException ex) {
			throw new SQLException(ExceptionToStringConverter.convert(ex));
		}
	}

	/**
	 * DataBaseOpenHelper
	 * 
	 * @author hide
	 */
	private class DataBaseOpenHelper extends SQLiteOpenHelper {
		/**
		 * コンストラクタ
		 * 
		 * @param context
		 * @param factory
		 * @param version
		 */
		public DataBaseOpenHelper(Context context) {
			super(context, "Mytem.db", null, DB_VERSION);
		}

		/**
		 * データベースが新規に作成された
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// NoodleHistoryテーブルを作成する
			db.execSQL(CREATE_NOODLEHISTORY_TABLE);
		}

		/**
		 * 存在するデータベースと定義しているバージョンが異なる
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// テーブルを削除する
			StringBuilder builder = new StringBuilder("DROP TABLE ");
			builder = new StringBuilder("DROP TABLE ");
			builder.append(MYTEMHISTORYTABLENAME);
			db.execSQL(builder.toString());
			// テーブルを定義しなおす
			onCreate(db);
		}

	}
}
