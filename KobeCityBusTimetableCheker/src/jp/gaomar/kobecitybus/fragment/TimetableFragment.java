package jp.gaomar.kobecitybus.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gaomar.kobecitybus.BusDestination;
import jp.gaomar.kobecitybus.BusTimeTable;
import jp.gaomar.kobecitybus.R;
import jp.gaomar.kobecitybus.TimetableAdapter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class TimetableFragment extends ListFragment {
//	/** 一致パターン*/
	private final Pattern pTime = Pattern.compile("[0-9]{1,2}");

	/** バス停名*/
	private String busStationName = "";
//	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		
//		View view = inflater.inflate(R.layout.fragment_list, null);
//
//		return view;
//	}

	public void onActivityCreated(Bundle savedInstanceState) {

		ArrayAdapter<BusDestination> adapter = new ArrayAdapter<BusDestination>(getActivity(),
		android.R.layout.simple_list_item_1);
		setListAdapter(adapter);
		super.onActivityCreated(savedInstanceState);
	}
	
	public void parseTimetable(Document data, final String routeData, final int routeNo) {
		AsyncTask<Document, Void, List<BusTimeTable>> task = new AsyncTask<Document, Void, List<BusTimeTable>>() {
			
			private static final String ERROR = "can not search";
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
			protected List<BusTimeTable> doInBackground(Document... params) {
				if (params.length < 1) {
					return null;
				}
				
				Document document = params[0];
				ArrayList<BusTimeTable> retList = parseDocument(document);

				return retList;
			}
			
			private ArrayList<BusTimeTable> parseDocument(Document document) {
				ArrayList<BusTimeTable> retList = new ArrayList<BusTimeTable>();
				
				Element tableTag = document.getElementsByTag("table").first();
				Element tableStTag = document.getElementsByTag("table").get(1);
				Element tableSuTag = document.getElementsByTag("table").get(2);
				Elements trTag = tableTag.getElementsByTag("tr");
				Elements trStTag = tableStTag.getElementsByTag("tr");
				Elements trSuTag = tableSuTag.getElementsByTag("tr");
				busStationName = document.title().split(" ")[1];

				for (int ii=0; ii<trTag.size(); ii++) {
					Element tr = trTag.get(ii);
					Element trSt = trStTag.get(ii);
					Element trSu = trSuTag.get(ii);
					Elements thTag = tr.getElementsByTag("th");
					Elements tdTag = tr.getElementsByTag("td");
					Elements tdStTag = trSt.getElementsByTag("td");
					Elements tdSuTag = trSu.getElementsByTag("td");
					
					Matcher m = pTime.matcher(thTag.text());
    				BusTimeTable timetable = new BusTimeTable();
					if (m.find()) {
						timetable.setwHour(thTag.text().replaceAll("時", ""));
						timetable.setwMinute(tdTag.text().replaceAll("分", ""));
						timetable.setStMinute(tdStTag.text().replaceAll("分", ""));
						timetable.setSuMinute(tdSuTag.text().replaceAll("分", ""));
	    				retList.add(timetable);
					}
    				
				}				


				return retList;

			}
			
//			private String getTime(String time) {
//				String ret = "";
//				
//				Matcher m = pTime.matcher(time);
//				if (m.find()) {
//					return time;
//				}
//
//				return ret;
//			}
			
			@Override
			protected void onPostExecute(List<BusTimeTable> result) {
				setListShown(true);
		
				// 処理中ダイアログをクローズ
                progressDialog.dismiss();
                
				if(result != null) {					
					TimetableAdapter adapter = new TimetableAdapter(getActivity(), 0, result);
					setListAdapter(adapter);

					// バス停名をタイトルバーに表記する
					getActivity().setTitle(busStationName);

					// 取得した時刻にカーソルを移動させる
					final Calendar calendar = Calendar.getInstance();
					final int hour = calendar.get(Calendar.HOUR_OF_DAY);
					int wk = 0;
					getListView().setVerticalFadingEdgeEnabled(false);
					getListView().setHorizontalFadingEdgeEnabled(false);
					
					for (BusTimeTable table : result) {
						if (hour == Integer.parseInt(table.getwHour())) {
							getListView().setSelection(wk);
							break;
						}
						wk++;
					}
				} else {
					Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
				}
		
				super.onPostExecute(result);
	
			}
		};
		
		task.execute(data);
	}
}
