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

    public static boolean isSound(Context con){
        return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("chk_sound", false);
    }

    public static String getGravity(Context con){
        return PreferenceManager.getDefaultSharedPreferences(con).getString("opt_gravity", "3");
    }

    public static float getSoundSpeed(Context con) {
    	float ret = PreferenceManager.getDefaultSharedPreferences(con).getInt("soundseek", 5) / 10F;
    	ret = ret+0.5F;
    	return ret;
    }

    public static int getPopValue(Context con) {
    	int ret = PreferenceManager.getDefaultSharedPreferences(con).getInt("popseek", 0);
    	ret = ret+1;
    	return ret;
    }

    public static int getEarValue(Context con) {
    	int ret = PreferenceManager.getDefaultSharedPreferences(con).getInt("earseek", 9000);
    	ret = ret+1000;
    	return ret;
    }

}
