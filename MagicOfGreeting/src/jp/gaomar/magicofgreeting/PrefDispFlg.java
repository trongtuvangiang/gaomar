package jp.gaomar.magicofgreeting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class PrefDispFlg extends DialogPreference{

	private static SharedPreferences sp;
	private Context con;
	public static final String CHK_DISP_KEY = "disp_";
	public static final int DISP_MAX = 16;
	private CheckBox chk_Disp1,chk_Disp2,chk_Disp3,chk_Disp4;
	private CheckBox chk_Disp5,chk_Disp6,chk_Disp7,chk_Disp8;
	private CheckBox chk_Disp9,chk_Disp10,chk_Disp11,chk_Disp12;
	private CheckBox chk_Disp13,chk_Disp14,chk_Disp15,chk_Disp16;
	
	public PrefDispFlg(Context context, AttributeSet attrs) {
		super(context, attrs);
		con = context;
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		setDialogLayoutResource(R.layout.dispdlg);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		initUi(view);
	
		// 設定フラグ取得
		getFlg();
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			setFlg();
		}
	}

	private void initUi(View view) {
		try {
			chk_Disp1 = (CheckBox) view.findViewById(R.id.disp_1);
			chk_Disp2 = (CheckBox) view.findViewById(R.id.disp_2);
			chk_Disp3 = (CheckBox) view.findViewById(R.id.disp_3);
			chk_Disp4 = (CheckBox) view.findViewById(R.id.disp_4);
			chk_Disp5 = (CheckBox) view.findViewById(R.id.disp_5);
			chk_Disp6 = (CheckBox) view.findViewById(R.id.disp_6);
			chk_Disp7 = (CheckBox) view.findViewById(R.id.disp_7);
			chk_Disp8 = (CheckBox) view.findViewById(R.id.disp_8);
			chk_Disp9 = (CheckBox) view.findViewById(R.id.disp_9);
			chk_Disp10 = (CheckBox) view.findViewById(R.id.disp_10);
			chk_Disp11 = (CheckBox) view.findViewById(R.id.disp_11);
			chk_Disp12 = (CheckBox) view.findViewById(R.id.disp_12);
			chk_Disp13 = (CheckBox) view.findViewById(R.id.disp_13);
			chk_Disp14 = (CheckBox) view.findViewById(R.id.disp_14);
			chk_Disp15 = (CheckBox) view.findViewById(R.id.disp_15);
			chk_Disp16 = (CheckBox) view.findViewById(R.id.disp_16);
		} catch (Exception e) {
		}	
	}
	
	private void setVal(int id, boolean flg) {
		Editor ed = sp.edit();
		ed.putBoolean(CHK_DISP_KEY + id, flg);
		ed.commit();
	}

	private boolean getVal(int id) {
		return sp.getBoolean(CHK_DISP_KEY + id, true );
	}

	private void getFlg() {
		try {
			chk_Disp1.setChecked(getVal(1));
			chk_Disp2.setChecked(getVal(2));
			chk_Disp3.setChecked(getVal(3));
			chk_Disp4.setChecked(getVal(4));
			chk_Disp5.setChecked(getVal(5));
			chk_Disp6.setChecked(getVal(6));
			chk_Disp7.setChecked(getVal(7));
			chk_Disp8.setChecked(getVal(8));
			chk_Disp9.setChecked(getVal(9));
			chk_Disp10.setChecked(getVal(10));
			chk_Disp11.setChecked(getVal(11));
			chk_Disp12.setChecked(getVal(12));
			chk_Disp13.setChecked(getVal(13));
			chk_Disp14.setChecked(getVal(14));
			chk_Disp15.setChecked(getVal(15));
			chk_Disp16.setChecked(getVal(16));
		} catch (Exception e) {
		}

	}
	
	private void setFlg() {
		try {
			if (chk_Disp1.isChecked() || chk_Disp2.isChecked() || chk_Disp3.isChecked() || chk_Disp4.isChecked()
			|| chk_Disp5.isChecked() || chk_Disp6.isChecked() || chk_Disp7.isChecked() || chk_Disp8.isChecked()
			|| chk_Disp9.isChecked() || chk_Disp10.isChecked() || chk_Disp11.isChecked() || chk_Disp12.isChecked()
			|| chk_Disp13.isChecked() || chk_Disp14.isChecked() || chk_Disp15.isChecked() || chk_Disp16.isChecked()) {
				setVal(1, chk_Disp1.isChecked());
				setVal(2, chk_Disp2.isChecked());
				setVal(3, chk_Disp3.isChecked());
				setVal(4, chk_Disp4.isChecked());
				setVal(5, chk_Disp5.isChecked());
				setVal(6, chk_Disp6.isChecked());
				setVal(7, chk_Disp7.isChecked());
				setVal(8, chk_Disp8.isChecked());
				setVal(9, chk_Disp9.isChecked());
				setVal(10, chk_Disp10.isChecked());
				setVal(11, chk_Disp11.isChecked());
				setVal(12, chk_Disp12.isChecked());
				setVal(13, chk_Disp13.isChecked());
				setVal(14, chk_Disp14.isChecked());
				setVal(15, chk_Disp15.isChecked());
				setVal(16, chk_Disp16.isChecked());
			} else {
				Toast.makeText(con, "どれか一つは必ずチェックしてください", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
		}
	}
}
