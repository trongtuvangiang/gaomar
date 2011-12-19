package jp.gaomar.onigirisalechecker;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DoActionReceiver extends BroadcastReceiver {

	private HPCheckTask mTask;
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
			// 金曜日にチェックする
			mTask = new HPCheckTask(ctx);
			mTask.execute();
		}
	}

}
