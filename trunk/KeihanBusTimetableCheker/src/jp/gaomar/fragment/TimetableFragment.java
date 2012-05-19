package jp.gaomar.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gaomar.keihan.BusDestination;
import jp.gaomar.keihan.BusTimeTable;
import jp.gaomar.keihan.R;
import jp.gaomar.keihan.TimetableAdapter;

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
	/** ��v�p�^�[��*/
	private final Pattern pHour = Pattern.compile("[0-9]{1,2}");

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
				// �o�b�N�O���E���h�̏����O��UI�X���b�h�Ń_�C�A���O�\��
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
				
				Element contentTimetable = document.getElementById("timetable");
				String title = contentTimetable.getElementsByTag("h4").get(0).text();
				getActivity().setTitle(title);
				Element content = document.getElementById("weekday");
				Element contentST = document.getElementById("saturday");
				Element contentSU = document.getElementById("sunday");


				Elements hourClass = content.getElementsByClass("hour");
				int hourCnt = 0;
				Elements htmlHour = hourClass.get(0).children();
				List<String> hourList = new ArrayList<String>();

				for (Element hour : htmlHour) {
					String time = hour.text();
					Matcher m = pHour.matcher(time);
					if (m.find()) {
						hourCnt++;
						hourList.add(time);

					}
					hour.nextElementSibling();
				}
				
				int busRoutesCnt = content.getElementsByClass("bus-routes").size();
				int wkRouteNo = 0;
				for (int ii=0; ii<busRoutesCnt; ii++) {
//					buf.append("�n��:");
//					buf.append(content.getElementsByClass("bus-routes").get(ii).text()).append("\n");
//					buf.append("�s����:");
//					buf.append(content.getElementsByClass("destination").get(ii).text()).append("\n");

//					String route = content.getElementsByClass("bus-routes").get(ii).text().replace(" ", "");
					// �w��n�������擾
					if (wkRouteNo != routeNo) {
						wkRouteNo++;
						continue;
					}
					Element wkhtml = content.getElementsByClass("destination").get(ii).nextElementSibling();
					Element sthtml = contentST.getElementsByClass("destination").get(ii).nextElementSibling();
					Element suhtml = contentSU.getElementsByClass("destination").get(ii).nextElementSibling();
					for(int jj = 0; jj < hourCnt; jj++) {
						BusTimeTable timetable = new BusTimeTable();
						// ���Ԋi�[
						timetable.setwHour(hourList.get(jj));
						// ���l�i�[
						Elements sign = content.getElementsByClass("sign");
						timetable.setNote(sign.text());

						// ����
						String minute = wkhtml.text();
						timetable.setwMinute(getMinute(minute));
						// �y�j��
						minute = sthtml.text();
						timetable.setStMinute(getMinute(minute));
						// ���j��
						minute = suhtml.text();
						timetable.setSuMinute(getMinute(minute));
						
						wkhtml = wkhtml.nextElementSibling();
						sthtml = sthtml.nextElementSibling();
						suhtml = suhtml.nextElementSibling();
						
						retList.add(timetable);
						
					}

					break;
				}
				return retList;

			}
			
			private String getMinute(String minute) {
				String ret = "--";
				
				Matcher m = pHour.matcher(minute);
				if (m.find()) {
					return minute;
				}

				return ret;
			}
			
			@Override
			protected void onPostExecute(List<BusTimeTable> result) {
				setListShown(true);
		
				// �������_�C�A���O���N���[�Y
                progressDialog.dismiss();
                
				if(result != null) {					
					TimetableAdapter adapter = new TimetableAdapter(getActivity(), 0, result);
					setListAdapter(adapter);
		
				} else {
					Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
				}
		
				super.onPostExecute(result);
	
			}
		};
		
		task.execute(data);
	}
}
