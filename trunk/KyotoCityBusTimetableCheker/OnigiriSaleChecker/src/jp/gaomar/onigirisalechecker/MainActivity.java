package jp.gaomar.onigirisalechecker;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	private Button mDetail;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
                
		NotificationManager notificationManager = 
			(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();

		final SharedPreferences pref = 
			getSharedPreferences("monitoring",MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);

		mDetail = (Button) findViewById(R.id.btn_detail);
	
		final ToggleButton mCheck = (ToggleButton) findViewById(R.id.btn_check);
		mCheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCheck.isChecked()) {
					onCheckStart();
					Toast.makeText(MainActivity.this, R.string.lbl_chk_start, Toast.LENGTH_SHORT).show();
					Toast.makeText(MainActivity.this, R.string.lbl_chk_start_message, Toast.LENGTH_LONG).show();					
				} else {
					onCheckStop();
					Toast.makeText(MainActivity.this, R.string.lbl_chk_stop, Toast.LENGTH_SHORT).show();
				}
				
				Editor e = pref.edit();
				e.putBoolean("monitoring", mCheck.isChecked());
				e.commit();
				
			}
		});
					
		if (getIntent().getBooleanExtra("check", false)) {
			HPCheckTask task = new HPCheckTask(this, true, mDetail);
			task.execute();				
		}
		mCheck.setChecked(pref.getBoolean("monitoring", false));
		
    }
    
    /**
     * 監視開始
     * @param view
     */
    private void onCheckStart() {
    	// ボタンクリックでレシーバーセット
		Intent intent = new Intent(this, DoActionReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

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
		
		// 今すぐチェック
		HPCheckTask task = new HPCheckTask(this, true, mDetail);
		task.execute();

    }
    
    /**
     * 監視停止
     * @param view
     */
    public void onCheckStop() {
    	Intent intent = new Intent(this, DoActionReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(sender);
    }

    /**
     * 監視停止
     * @param view
     */
    public void onDetail(View view) {
    	Uri uri = Uri.parse(HPCheckTask.ONIGIRI_ADDRESS);
    	Intent i = new Intent(Intent.ACTION_VIEW,uri);
    	startActivity(i); 
    }

    /**
     * 今すぐチェック
     * @param view
     */
    public void onNow(View view) {
		HPCheckTask task = new HPCheckTask(this, true, mDetail);
		task.execute();

    }
}