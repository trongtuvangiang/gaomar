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
		// 特定の行(position)のデータを得る
		BusDestination item = (BusDestination)getItem(position);
		 
		 // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		 if (null == convertView) {
			 convertView = layoutInflater_.inflate(R.layout.destlist_item, null);
		 }
		 
		 // CustomDataのデータをViewの各Widgetにセットする
		 TextView destTextView, routeTextView;
		 routeTextView = (TextView)convertView.findViewById(R.id.routeText);
		 routeTextView.setText(item.getRoute());
		 
		 destTextView = (TextView)convertView.findViewById(R.id.destinationText);
		 destTextView.setText(item.getDestination());
		 
		 return convertView;
	}

	
}
