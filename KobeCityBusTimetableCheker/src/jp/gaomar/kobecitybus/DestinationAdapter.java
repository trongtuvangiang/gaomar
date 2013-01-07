package jp.gaomar.kobecitybus;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DestinationAdapter extends ArrayAdapter<BusDestination>{

	private LayoutInflater layoutInflater_;
	
	public DestinationAdapter(Context context, int textViewResourceId,
			List<BusDestination> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ����̍s(position)�̃f�[�^�𓾂�
		BusDestination item = (BusDestination)getItem(position);
		 
		 // convertView�͎g���񂵂���Ă���\��������̂�null�̎������V�������
		 if (null == convertView) {
			 convertView = layoutInflater_.inflate(R.layout.destlist_item, null);
		 }
		 
		 // CustomData�̃f�[�^��View�̊eWidget�ɃZ�b�g����
		 TextView destTextView, routeTextView;
		 routeTextView = (TextView)convertView.findViewById(R.id.routeText);
		 routeTextView.setText(item.getRoute());
		 
		 destTextView = (TextView)convertView.findViewById(R.id.destinationText);
		 destTextView.setText(item.getDestination());
		 
		 return convertView;
	}

	
}
