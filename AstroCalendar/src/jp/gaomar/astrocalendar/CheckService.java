package jp.gaomar.astrocalendar;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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
    		
    		if (PrefActivity.isDisp(CheckService.this)) {
	            // 次回起動登録
	        	// ボタンクリックでレシーバーセット
	    		Intent intent = new Intent(CheckService.this, DoActionReceiver.class);
	    		PendingIntent sender = PendingIntent.getBroadcast(CheckService.this, 0, intent, 0);
	
	    		// アラームマネージャの用意（翌日の0時,そのあとは毎日実行）
	    		
	    		Calendar cal = Calendar.getInstance();
	    		cal.setTimeInMillis(System.currentTimeMillis());
	    		cal.add(Calendar.DATE, 1);
	    		cal.set(Calendar.HOUR_OF_DAY, 0);
	    		cal.set(Calendar.MINUTE, 0);
	    		cal.set(Calendar.SECOND, 1);		

	    		Calendar now = Calendar.getInstance();
	    		now.set(Calendar.SECOND, 1);		
	    		
	    		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);	    		
	    		am.set(AlarmManager.RTC, now.getTimeInMillis(), sender);
	    		am.set(AlarmManager.RTC, cal.getTimeInMillis(), sender);

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
