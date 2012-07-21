package jp.gaomar.kyotocitybus.fragment;

import java.io.IOException;
import java.util.ArrayList;

import jp.gaomar.kyotocitybus.BusDestination;
import jp.gaomar.kyotocitybus.BusStation;
import jp.gaomar.kyotocitybus.BusStationAdapter;
import jp.gaomar.kyotocitybus.R;
import jp.gaomar.kyotocitybus.db.DBAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

public class BusStationFragment extends ListFragment {
	static DBAdapter dbAdapter;
	String BaseUrl = "http://www.city.kyoto.jp/kotsu/busdia/hyperdia/";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		ArrayAdapter<BusDestination> adapter = new ArrayAdapter<BusDestination>(getActivity(),
		android.R.layout.simple_list_item_1);
		setListAdapter(adapter);
		super.onActivityCreated(savedInstanceState);
		dbAdapter = new DBAdapter(getActivity());
	    
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
					getListView().setVerticalFadingEdgeEnabled(false);
					getListView().setHorizontalFadingEdgeEnabled(false);

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
		
	    dbAdapter.open();
	    try {
			String idNo = dbAdapter.searchID(data.getStationName() );

			if (idNo.length() != 0) {
				getTimetable(idNo);
		    	dbAdapter.saveHistory(data.getStationName());

			}
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
		    dbAdapter.close();
		}

		
	}
	
	private void getTimetable(final String id) {
		// HTMLのドキュメントを取得		
		AsyncTask<Void, Void, Document> task = new AsyncTask<Void, Void, Document>() {

			private static final String ERROR = "can not search";
			private ProgressDialog progressDialog = null;
			
			@Override
			protected void onPreExecute() {
				// バックグラウンドの処理前にUIスレッドでダイアログ表示
		        progressDialog = new ProgressDialog(getActivity());
		        progressDialog.setMessage(getResources().getText(
		                        R.string.data_loading));
		        progressDialog.setIndeterminate(true);
		        progressDialog.setCancelable(false);
		        progressDialog.show();
		        
				super.onPreExecute();
			}

			@Override
			protected Document doInBackground(Void... params) {
				String url = BaseUrl + id;
		
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet hg = new HttpGet(url);
		
				try {
					HttpResponse httpResponse = httpClient.execute(hg);
		
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						Document document = Jsoup.connect(url).get();
						hg.abort();
						return document;
					} else {
						hg.abort();
						return null;
					}		
				} catch (NullPointerException e) {
					return null;
				} catch (IOException e) {
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(Document document) {			
				// 処理中ダイアログをクローズ
		        progressDialog.dismiss();

				try {
					FragmentManager manager = getActivity().getSupportFragmentManager();
					FragmentTransaction fragmentTransaction = manager.beginTransaction();

					DestinationFragment fragment = (DestinationFragment)manager.findFragmentById(R.id.result_fragment);
					BusStationFragment fragment2 = (BusStationFragment)manager.findFragmentById(R.id.busStation_fragment);
					fragment.searchDestination(document);

					fragmentTransaction.show(fragment);
					fragmentTransaction.hide(fragment2);
					fragmentTransaction.commit();

				} catch (Exception e) {
					Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
				}


				super.onPostExecute(document);

			}
		};
		task.execute();

	}

}
