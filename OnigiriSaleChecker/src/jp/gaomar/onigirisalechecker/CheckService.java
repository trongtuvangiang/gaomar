package jp.gaomar.onigirisalechecker;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

public class CheckService extends Service{
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	
    @Override
	public void onStart(Intent intent, int startId) {
		Thread thr = new Thread(null, task, "CheckService");
		thr.start();
	}

    private Runnable task = new Runnable() {          
        @Override  
        public void run() {  
            synchronized (binder) {  
                try {  
                    // TODO DB登録処理   
                } catch (Exception e) {  
                        }  
            }  

    		final SharedPreferences pref = 
    			getSharedPreferences("monitoring",MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
    		
    		if (pref.getBoolean("monitoring", false)) {
	            // 次回起動登録
	        	// ボタンクリックでレシーバーセット
	    		Intent intent = new Intent(CheckService.this, DoActionReceiver.class);
	    		PendingIntent sender = PendingIntent.getBroadcast(CheckService.this, 0, intent, 0);
	
	    		// アラームマネージャの用意（初回は現在の0時,そのあとは毎日実行）
	
	    		Calendar cal = Calendar.getInstance();
	    		cal.add(Calendar.DATE, 1);
	    		cal.set(Calendar.HOUR_OF_DAY, 0);
	    		cal.set(Calendar.MINUTE, 0);
	    		cal.set(Calendar.SECOND, 1);		
	    		
	    		long firstTime = cal.getTimeInMillis();
	    		
	    		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
	    		am.cancel(sender);
	    		am.set(AlarmManager.RTC_WAKEUP, firstTime, sender);
	    		
	    		
    		}
            // サービス終了  
            CheckService.this.stopSelf();  
        }  
    };  
    
	private final IBinder binder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			// TODO 自動生成されたメソッド・スタブ
			return super.onTransact(code, data, reply, flags);
		}
    	
    };
}
