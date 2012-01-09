package jp.gaomar.onigirisalechecker;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DoActionReceiver extends BroadcastReceiver {

	private HPCheckTask mTask;
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		Intent intent_ = new Intent(ctx, DoActionReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(ctx, 0, intent_, 0);

		Calendar cal = Calendar.getInstance();
		
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
			// 金曜日にチェックする
			mTask = new HPCheckTask(ctx);
			mTask.execute();
		}
		
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 1);		
		
		long firstTime = cal.getTimeInMillis();
		
		AlarmManager am = (AlarmManager) ctx.getSystemService(ctx.ALARM_SERVICE);
		am.cancel(sender);
		am.set(AlarmManager.RTC_WAKEUP, firstTime, sender);
		
	}

}
