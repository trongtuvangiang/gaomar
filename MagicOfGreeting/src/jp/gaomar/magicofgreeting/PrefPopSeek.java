package jp.gaomar.magicofgreeting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

public class PrefPopSeek extends DialogPreference implements SeekBar.OnSeekBarChangeListener {

	private static SharedPreferences sp;
	private SeekBar bar;
	private Button btn_Def;
	private static final String OPT_SEEKBAR_KEY = "popseek";

	public PrefPopSeek(Context context, AttributeSet attrs) {
		super(context, attrs);
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		setDialogLayoutResource(R.layout.popdlg);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		bar = (SeekBar) view.findViewById(R.id.popseek);
		bar.setOnSeekBarChangeListener(this);
		bar.setProgress(getVal());
		btn_Def = (Button) view.findViewById(R.id.btn_default);
		btn_Def.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bar.setProgress(0);
			}
		});
	}


	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			setVal(bar.getProgress());
		}

	}

	private void setVal(int value) {
		Editor ed = sp.edit();
		ed.putInt(OPT_SEEKBAR_KEY, value);
		ed.commit();
	}

	private int getVal() {
		return sp.getInt(OPT_SEEKBAR_KEY, 0);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {


	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}



}
