package jp.gaomar.astrocalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DoActionReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		DateCheckTask task = new DateCheckTask(ctx);
		task.execute();
	}

}
