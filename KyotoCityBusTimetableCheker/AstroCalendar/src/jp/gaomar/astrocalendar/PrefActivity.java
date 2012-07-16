package jp.gaomar.astrocalendar;

import java.util.Calendar;

import mediba.ad.sdk.android.MasAdView;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class PrefActivity extends PreferenceActivity{

	private SharedPreferences mPref;
	private ImageView mImgView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(R.drawable.form_background_shelf);  
		addPreferencesFromResource(R.xml.pref);

		// ここから追加
		CheckBoxPreference preference = (CheckBoxPreference) findPreference(getText(R.string.pref_service));
		SpannableString title = new SpannableString(getText(R.string.pref_service_title));
		title.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title.length(), 0);
		preference.setTitle(title);

		SpannableString summary = new SpannableString(getText(R.string.pref_service_summary));
		summary.setSpan(new ForegroundColorSpan(Color.rgb(49, 49, 49)), 0, summary.length(), 0);
		preference.setSummary(summary);
		
		mPref = getSharedPreferences("astro",MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);

		CharSequence cs = getText(R.string.pref_service); 
        CheckBoxPreference cbp = (CheckBoxPreference)findPreference(cs);  
        // リスナーを設定する  
        cbp.setOnPreferenceChangeListener(checkBoxPreference_OnPreferenceChangeListener);  
        
        // 広告表示
        MasAdView adView = new MasAdView(this);
        adView.setVisibility(View.VISIBLE);
        adView.startRequest();
        
        LayoutParams adLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        adView.setLayoutParams(adLayoutParams);
        LinearLayout linearlayout = new LinearLayout(this);
        linearlayout.addView(adView);
        linearlayout.setGravity(Gravity.BOTTOM);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        addContentView(linearlayout, layoutParams);

        // 画像        
        mImgView = new ImageView(this);
        try {
        	if (getIntent().getIntExtra("week", 0) == 0) {
        		mImgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("a" + getIntent().getIntExtra("key", 0), "drawable", getPackageName())));
        	} else {
        		mImgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("w" + getIntent().getIntExtra("week", 0), "drawable", getPackageName())));
        	}
		} catch (Exception e) {
		}
        
        LayoutParams imgLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
//        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//
//        imgLayoutParams.setMargins(0, 50, 0, 0);
        mImgView.setLayoutParams(imgLayoutParams);
        LinearLayout imgLinearlayout = new LinearLayout(this);
        imgLinearlayout.addView(mImgView);        
        LayoutParams img2LayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        addContentView(imgLinearlayout, img2LayoutParams);
        
        // チェックがあればカレンダー表示
        if (isDisp(this)) {
        	DateCheckTask task = new DateCheckTask(PrefActivity.this);
        	task.execute();  
        }
	}

    // チェックボックスPreferenceの　PreferenceChangeリスナー  
    private OnPreferenceChangeListener checkBoxPreference_OnPreferenceChangeListener =  
        new OnPreferenceChangeListener(){  
            @Override  
            public boolean onPreferenceChange(Preference preference, Object newValue) {  
                return checkBoxPreference_OnPreferenceChange(preference,newValue);  
            }
    };
    
    private boolean checkBoxPreference_OnPreferenceChange(Preference preference, Object newValue){  
        if (((Boolean)newValue).booleanValue()) {  
            selectMode();
        } else {  
            onServiceStop();  
        }  
        return true;  
    }
    
	/**
	 * 表示形式を選択する
	 */
	private void selectMode() {

		final String[] getMode = getResources().getStringArray(R.array.disp_mode);
		
		new AlertDialog.Builder(PrefActivity.this)
        .setCancelable(false)
        .setSingleChoiceItems(getMode, -1, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
        		Editor e = mPref.edit();
        		e.putInt("mode", which);
        		e.commit();

        		// 次回起動登録
	        	// ボタンクリックでレシーバーセット
	    		Intent intent = new Intent(PrefActivity.this, DoActionReceiver.class);
	    		PendingIntent sender = PendingIntent.getBroadcast(PrefActivity.this, 0, intent, 0);
	
	    		// アラームマネージャの用意（初回は現在の0時,そのあとは毎日実行）

	    		Calendar cal = Calendar.getInstance();
	    		cal.setTimeInMillis(System.currentTimeMillis());
	    		cal.add(Calendar.DATE, 1);
	    		cal.set(Calendar.HOUR_OF_DAY, 0);
	    		cal.set(Calendar.MINUTE, 0);
	    		cal.set(Calendar.SECOND, 1);		
	    			    		
	    		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);	    		
	    		am.set(AlarmManager.RTC, cal.getTimeInMillis(), sender);

            	DateCheckTask task = new DateCheckTask(PrefActivity.this);
            	task.execute();        		

				// ダイアログ閉じる
            	dialog.dismiss();

			}
		}).show();

	}
	
    /**
     * サービス停止
     * @param view
     */
    public void onServiceStop() {    	
		NotificationManager mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		mManager.cancel(1);
		mManager.cancel(2);
		mManager.cancel(3);

    	Intent intent = new Intent(this, DoActionReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(sender);

		mImgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("a0", "drawable", getPackageName())));
		
	}
    
	public static boolean isDisp(Context con){
        return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(con.getString(R.string.pref_service), false);
    }
	
}
