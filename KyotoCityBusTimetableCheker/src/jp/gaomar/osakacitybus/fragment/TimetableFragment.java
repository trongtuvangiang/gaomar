package jp.gaomar.osakacitybus.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gaomar.osakacitybus.BusDestination;
import jp.gaomar.osakacitybus.BusTimeTable;
import jp.gaomar.osakacitybus.R;
import jp.gaomar.osakacitybus.TimetableAdapter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.R.bool;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class TimetableFragment extends ListFragment {
	/** ��v�p�^�[��*/
	private final Pattern pTime = Pattern.compile("[0-9]{1,2}");

	/** �o�X�▼*/
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
				
				Element contentTimetable = document.getElementById("main_content");
				busStationName = contentTimetable.getElementsByTag("b").get(0).text() ;

				Element contentClass = contentTimetable.select("table.timetable").get(routeNo-1);
				Elements trTag = contentClass.getElementsByTag("tbody").get(1).getElementsByTag("tr");
				
				Elements tds;

				for (Element tr : trTag) {
					// ���ԃw�b�_�[
    				BusTimeTable timetable = new BusTimeTable();
    				Boolean timeFlg = false;
                    tds = tr.getElementsByTag("th");
                    if ((tds != null) && (!tds.isEmpty())) {
                    	int cnt = 0;
                    	for (Element td : tds) {
                            if (td != null) {
                            	String time = getTime(td.text());
                            	if (!"".equals(time)) timeFlg = true;
                      			switch (cnt) {
								case 0:
									// ����
									timetable.setwHour(time);
									break;
								case 1:
									// �y�j��
									timetable.setStHour(time);
									break;
								default:
									// ���j��
									timetable.setSuHour(time);
									break;
								}
                      			cnt++;
                            }
                        }
                    }
                    // ���f�[�^
                    tds = tr.getElementsByTag("td");
                    if ((tds != null) && (!tds.isEmpty())) {
                    	int cnt = 0;
                    	for (Element td : tds) {
                            if (td != null) {
                            	String time = getTime(td.text());
                      			switch (cnt) {
								case 0:
									// ����
									timetable.setwMinute(time);
									break;
								case 1:
									// �y�j��
									timetable.setStMinute(time);
									break;
								default:
									// ���j��
									timetable.setSuMinute(time);
									break;
								}
                      			cnt++;
                            }
                        }

                    }
                    
                    if (timeFlg) retList.add(timetable);

				}
				
				return retList;

			}
			
			private String getTime(String time) {
				String ret = "";
				
				Matcher m = pTime.matcher(time);
				if (m.find()) {
					return time;
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

					// �o�X�▼���^�C�g���o�[�ɕ\�L����
					getActivity().setTitle(busStationName);

					// �擾���������ɃJ�[�\�����ړ�������
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
