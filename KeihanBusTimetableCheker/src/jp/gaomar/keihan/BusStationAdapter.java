package jp.gaomar.keihan;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BusStationAdapter extends ArrayAdapter<BusStation>{

	private LayoutInflater layoutInflater_;
	
	public BusStationAdapter(Context context, int textViewResourceId,
			List<BusStation> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ����̍s(position)�̃f�[�^�𓾂�
		BusStation item = (BusStation)getItem(position);
		 
		 // convertView�͎g���񂵂���Ă���\��������̂�null�̎������V�������
		 if (null == convertView) {
			 convertView = layoutInflater_.inflate(R.layout.stationlist_item, null);
		 }
		 
		 // CustomData�̃f�[�^��View�̊eWidget�ɃZ�b�g����
		 TextView txtStation;
		 txtStation = (TextView)convertView.findViewById(R.id.txtStation);
		 txtStation.setText(item.getStationName());
		 		 
		 return convertView;
	}

	
}
