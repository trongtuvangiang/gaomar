package jp.gaomar.kyotocitybus.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.gaomar.kyotocitybus.BusDestination;
import jp.gaomar.kyotocitybus.BusTimeTable;
import jp.gaomar.kyotocitybus.R;
import jp.gaomar.kyotocitybus.TimetableAdapter;

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
//	private final Pattern pTime = Pattern.compile("[0-9]{1,2}");

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
				Elements trTag = tableTag.select("tr[align=center]");
				busStationName = document.title().split(" ")[0];

				for (Element tag : trTag) {
					Elements tdTag = tag.getElementsByTag("td");
    				BusTimeTable timetable = new BusTimeTable();
    				for (int ii=0; ii<tdTag.size(); ii++) {
    					switch (ii) {
						case 0:
							timetable.setwMinute(tdTag.get(ii).text());
							break;
						case 1:
							timetable.setwHour(tdTag.get(ii).text());
							break;
						case 2:
							timetable.setStMinute(tdTag.get(ii).text());
							break;
						case 3:
							timetable.setStHour(tdTag.get(ii).text());
							break;
						default:
							timetable.setSuMinute(tdTag.get(ii).text());
							break;
						}
    				}
    				
    				retList.add(timetable);
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
