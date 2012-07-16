package jp.gaomar.astronumbertouch.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

    /** データベースパス*/
	private static String DB_PATH = "/data/data/jp.gaomar.astronumbertouch/databases/";
    
	/** データベース名 */
	private static final String DB_NAME = "astro";
	private static final String DB_NAME_ASSET = "db/astro.db";
	/** データベースのバージョン */
	private static final int DB_VER = 1;
    
    private final Context mContext; 
    
    public DatabaseHelper (Context context) {
		super(context, DB_NAME, null, DB_VER);
		this.mContext = context;
	}

    /** 
     * asset に格納したデータベースをコピーするための空のデータベースを作成する 
     *  
     **/  
    public void createEmptyDataBase() throws IOException{  
        boolean dbExist = checkDataBaseExists();  
  
        if(dbExist){  
            // すでにデータベースは作成されている  
        }else{  
            // このメソッドを呼ぶことで、空のデータベースが  
            // アプリのデフォルトシステムパスに作られる  
            this.getReadableDatabase();  
   
            try {  
                // asset に格納したデータベースをコピーする  
                copyDataBaseFromAsset();   
            } catch (IOException e) {  
                throw new Error("Error copying database");  
            }  
        }  
    }  
    
    /** 
     * 再コピーを防止するために、すでにデータベースがあるかどうか判定する 
     *  
     * @return 存在している場合 {@code true} 
     */  
    private boolean checkDataBaseExists() {  
        SQLiteDatabase checkDb = null;  
   
        try{  
            String dbPath = DB_PATH + DB_NAME;  
            checkDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);  
        }catch(SQLiteException e){  
            // データベースはまだ存在していない  
        }  
   
        if(checkDb != null){  
            checkDb.close();  
        }  
        return checkDb != null ? true : false;  
    }
    
    /** 
     * asset に格納したデーだベースをデフォルトの 
     * データベースパスに作成したからのデータベースにコピーする 
     * */  
    private void copyDataBaseFromAsset() throws IOException{  
   
        // asset 内のデータベースファイルにアクセス  
        InputStream mInput = mContext.getAssets().open(DB_NAME_ASSET);  
   
        // デフォルトのデータベースパスに作成した空のDB  
        String outFileName = DB_PATH + DB_NAME;  
   
        OutputStream mOutput = new FileOutputStream(outFileName);  
  
        // コピー  
        byte[] buffer = new byte[1024];  
        int size;  
        while ((size = mInput.read(buffer)) > 0){  
            mOutput.write(buffer, 0, size);  
        }  
   
        //Close the streams  
        mOutput.flush();  
        mOutput.close();  
        mInput.close();  
    }  
    
    public SQLiteDatabase openDataBase() throws SQLException{  
        //Open the database  
        String myPath = DB_PATH + DB_NAME;  
        SQLiteDatabase dataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);  
        return dataBase;  
    }  
    
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	
}
