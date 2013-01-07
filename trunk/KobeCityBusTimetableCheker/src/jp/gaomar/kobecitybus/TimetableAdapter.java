package jp.gaomar.kobecitybus;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TimetableAdapter extends ArrayAdapter<BusTimeTable>{

	private LayoutInflater layoutInflater_;
	
	public TimetableAdapter(Context context, int textViewResourceId,
			List<BusTimeTable> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 特定の行(position)のデータを得る
		BusTimeTable item = (BusTimeTable)getItem(position);
		 
		 // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		 if (null == convertView) {
			 convertView = layoutInflater_.inflate(R.layout.timetable_item, null);
		 }
		 
		 // CustomDataのデータをViewの各Widgetにセットする
		 TextView txtHour, txtWeek, txtSat, txtSun;
		 txtHour = (TextView)convertView.findViewById(R.id.txtHour);
		 txtHour.setText(item.getwHour());
		 
		 txtWeek = (TextView)convertView.findViewById(R.id.txtWMinute);
		 txtWeek.setText(item.getwMinute());
		 txtSat = (TextView)convertView.findViewById(R.id.txtSTMinute);
		 txtSat.setText(item.getStMinute());
		 txtSun = (TextView)convertView.findViewById(R.id.txtSUMinute);
		 txtSun.setText(item.getSuMinute());
		 
		 return convertView;
	}

	
}
