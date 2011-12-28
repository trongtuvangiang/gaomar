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
                    // TODO DB�o�^����   
                } catch (Exception e) {  
                        }  
            }  

    		final SharedPreferences pref = 
    			getSharedPreferences("monitoring",MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
    		
    		if (pref.getBoolean("monitoring", false)) {
	            // ����N���o�^
	        	// �{�^���N���b�N�Ń��V�[�o�[�Z�b�g
	    		Intent intent = new Intent(CheckService.this, DoActionReceiver.class);
	    		intent.putExtra("checkFlg", 1);
	    		PendingIntent sender = PendingIntent.getBroadcast(CheckService.this, 0, intent, 0);
	
	    		// �A���[���}�l�[�W���̗p�Ӂi����͌��݂�0��,���̂��Ƃ͖������s�j
	
	    		Calendar cal = Calendar.getInstance();
	    		cal.set(Calendar.HOUR, 0);
	    		cal.set(Calendar.MINUTE, 0);
	    		cal.set(Calendar.SECOND, 0);
	    		cal.set(Calendar.MILLISECOND, 0);		
	    	    
	    		long firstTime = cal.getTimeInMillis();
	    		
	    		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
	    		am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, AlarmManager.INTERVAL_DAY, sender);
	    		
    		}
            // �T�[�r�X�I��  
            CheckService.this.stopSelf();  
        }  
    };  
    
	private final IBinder binder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return super.onTransact(code, data, reply, flags);
		}
    	
    };
}
