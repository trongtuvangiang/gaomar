package jp.gaomar.onigirisalechecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent i) {
	      context.startService(new Intent(context, CheckService.class));  
	}

}
