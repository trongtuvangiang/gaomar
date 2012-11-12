package jp.gaomar.magicofgreeting;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class Util {
	private static final String MAJOUR_TAG = "Fuate";

    /** ログ出力(デバッグ) */
    public static void log(String className, String message){
        Log.d(MAJOUR_TAG, className + ":" + message);
	}

    /** ログ出力(デバッグ) */
    public static void log(Context context, String message){
        Log.d(MAJOUR_TAG, context.getClass().getSimpleName() + ":" + message);
    }

    /** ログ出力(エラー) */
    public static void logE(String className, String message, Throwable tr){
        Log.e(MAJOUR_TAG, className + ":" + message, tr);
    }

    /** ログ出力(エラー) */
    public static void logE(Context context, String message, Throwable tr){
        Log.e(MAJOUR_TAG, context.getClass().getSimpleName() + ":" + message, tr);
    }

    public static int random(int max, int min) {
        return (int) Math.floor( Math.random() * (max - min) ) + min;
    }
    
	/**
	 * デバッグフラグ取得
	 * @param ctx
	 * @return
	 */
	public static boolean isDebuggable(Context ctx) {
		PackageManager manager = ctx.getPackageManager();
		ApplicationInfo appInfo = null;
		try {
			appInfo = manager.getApplicationInfo(ctx.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			return false;
		}
		if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE)
			return true;
		return false;
	}
}
