package jp.gaomar.onigirisalechecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent i) {
	    if (Intent.ACTION_BOOT_COMPLETED.equals(i.getAction()))  
	    { 
	      context.startService(new Intent(context, CheckService.class));  
	    } 		
	}

}
