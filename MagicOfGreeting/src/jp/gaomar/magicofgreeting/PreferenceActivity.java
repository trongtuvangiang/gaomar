package jp.gaomar.magicofgreeting;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class PreferenceActivity extends android.preference.PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
	}

    public static boolean isDisp(Context con){
        return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("chk_disp", false);
    }

    public static String getGravity(Context con){
        return PreferenceManager.getDefaultSharedPreferences(con).getString("opt_gravity", "3");
    }

}
