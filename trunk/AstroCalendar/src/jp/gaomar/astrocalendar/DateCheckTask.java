package jp.gaomar.astrocalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class DateCheckTask extends AsyncTask<Void, Void, Calendar>{

	private Context mCtx;
	
	public DateCheckTask(Context ctx) {
		this.mCtx = ctx;
	}

	@Override
	protected Calendar doInBackground(Void... params) {
		Calendar now = Calendar.getInstance();
		return now;
	}

	@Override
	protected void onPostExecute(Calendar now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		
		final SharedPreferences pref = 
			mCtx.getSharedPreferences("astro",mCtx.MODE_WORLD_READABLE|mCtx.MODE_WORLD_WRITEABLE);
		
		NotificationManager mManager = (NotificationManager)mCtx.getSystemService(Context.NOTIFICATION_SERVICE);		
		try {

			Notification n1 = new Notification();
			n1.icon = mCtx.getResources().getIdentifier("a" + (now.get(Calendar.MONTH)+1), "drawable", mCtx.getPackageName());
			//n1.when = System.currentTimeMillis();
			//n1.number = now.get(Calendar.MONTH) + 1;
			
			Notification n2 = new Notification();		
			n2.icon = mCtx.getResources().getIdentifier("a" + now.get(Calendar.DATE), "drawable", mCtx.getPackageName());
			//n2.when = System.currentTimeMillis();
			//n2.number = now.get(Calendar.DATE); 
			Notification n3 = new Notification();		
			n3.icon = mCtx.getResources().getIdentifier("w" + now.get(Calendar.DAY_OF_WEEK), "drawable", mCtx.getPackageName());
				
			Intent i1 = new Intent(mCtx.getApplicationContext(),PrefActivity.class);
			i1.putExtra("key", (now.get(Calendar.MONTH)+1));
			i1.setType("month");
			PendingIntent pend1 = PendingIntent.getActivity(mCtx, 0, i1, PendingIntent.FLAG_UPDATE_CURRENT);
			Intent i2 = new Intent(mCtx.getApplicationContext(),PrefActivity.class);			
			i2.putExtra("key", (now.get(Calendar.DATE)));
			i2.setType("date");
			PendingIntent pend2 = PendingIntent.getActivity(mCtx, 0, i2, PendingIntent.FLAG_UPDATE_CURRENT);
			Intent i3 = new Intent(mCtx.getApplicationContext(),PrefActivity.class);			
			i3.putExtra("week", (now.get(Calendar.DAY_OF_WEEK)));
			i3.setType("week");
			PendingIntent pend3 = PendingIntent.getActivity(mCtx, 0, i3, PendingIntent.FLAG_UPDATE_CURRENT);
			
			n1.setLatestEventInfo(mCtx.getApplicationContext(), sdf.format(now.getTime()), mCtx.getString(R.string.app_name), pend1 );
			n1.flags = Notification.FLAG_ONGOING_EVENT;
			n2.setLatestEventInfo(mCtx.getApplicationContext(), sdf.format(now.getTime()), mCtx.getString(R.string.app_name), pend2 );
			n2.flags = Notification.FLAG_ONGOING_EVENT;
			n3.setLatestEventInfo(mCtx.getApplicationContext(), getDayOfTheWeek(now.get(Calendar.DAY_OF_WEEK)), mCtx.getString(R.string.app_name), pend3 );
			n3.flags = Notification.FLAG_ONGOING_EVENT;

			mManager.cancel(1); mManager.cancel(2); mManager.cancel(3);

			switch (pref.getInt("mode", 0)) {
			case 0:
				mManager.notify(1,n1);
				mManager.notify(2,n2);				
				break;
			case 1:
				mManager.notify(1,n1);
				mManager.notify(2,n2);				
				if (n3.icon != 0) mManager.notify(3,n3);				
				break;
			case 2:
				mManager.notify(1,n2);
				break;
			case 3:
				mManager.notify(1,n2);
				if (n3.icon != 0) mManager.notify(2,n3);
				break;				
			}
			
		} catch (Exception e) {
		}

	}

	/**
	 * åªç›ÇÃójì˙Çï‘ÇµÇ‹Ç∑ÅB
	 * @return	åªç›ÇÃójì˙
	 */
	public static String getDayOfTheWeek(int week) { 	  
	    switch (week) {
	        case Calendar.SUNDAY: return "ì˙ójì˙";
	        case Calendar.MONDAY: return "åéójì˙";
	        case Calendar.TUESDAY: return "âŒójì˙";
	        case Calendar.WEDNESDAY: return "êÖójì˙";
	        case Calendar.THURSDAY: return "ñÿójì˙";
	        case Calendar.FRIDAY: return "ã‡ójì˙";
	        case Calendar.SATURDAY: return "ìyójì˙";
	    }
	    throw new IllegalStateException();
	}
}
