package jp.gaomar.fragment;

import java.util.ArrayList;

import jp.gaomar.keihan.BusDestination;
import jp.gaomar.keihan.BusStation;
import jp.gaomar.keihan.BusStationAdapter;
import jp.gaomar.keihan.R;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

public class BusStationFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		ArrayAdapter<BusDestination> adapter = new ArrayAdapter<BusDestination>(getActivity(),
		android.R.layout.simple_list_item_1);
		setListAdapter(adapter);
		super.onActivityCreated(savedInstanceState);
	}
	
	public void getStationList(final ArrayList<BusStation> list) {
		AsyncTask<Void, Void, ArrayList<BusStation>> task = new AsyncTask<Void, Void, ArrayList<BusStation>>() {

			private ProgressDialog progressDialog = null;
			
			@Override
			protected void onPreExecute() {
				setListShown(false);

				// バックグラウンドの処理前にUIスレッドでダイアログ表示
		        progressDialog = new ProgressDialog(getActivity());
		        progressDialog.setMessage(getResources().getText(
		                        R.string.data_loading));
		        progressDialog.setIndeterminate(true);
		        progressDialog.show();
		        
				super.onPreExecute();
			}

			@Override
			protected ArrayList<BusStation> doInBackground(Void... params) {

				return list;
			}
			
			@Override
			protected void onPostExecute(ArrayList<BusStation> retList) {	
				setListShown(true);

				// 処理中ダイアログをクローズ
		        progressDialog.dismiss();
		        
				if(retList != null) {					
					BusStationAdapter adapter = new BusStationAdapter(getActivity(), 0, retList);
					setListAdapter(adapter);
		
					Toast.makeText(getActivity(), R.string.selstation, Toast.LENGTH_SHORT).show();
				}
		
				super.onPostExecute(retList);

			}
		};
		task.execute();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		BusStation data = (BusStation) l.getAdapter().getItem(position);
		AutoCompleteTextView txtSearch = (AutoCompleteTextView)getActivity().findViewById(R.id.auto_complete);
		txtSearch.setText(data.getStationName());

		
	}

	
}
