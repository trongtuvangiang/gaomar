package jp.gaomar.nagoyacitybus;

import jp.gaomar.nagoyacitybus.fragment.TimetableFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

public class TimetableActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable);
		
		BusTimeTable baseData = null;
		
		Bundle extras=getIntent().getExtras();
		if (extras!=null) {
			TextView txtDest = (TextView) findViewById(R.id.txtDest);
			TextView txtNote = (TextView) findViewById(R.id.txtNote);
			txtDest.setText((String)extras.getString("dest"));
			baseData = (BusTimeTable)extras.getSerializable("timetable");
			String route = (String)extras.getString("route");
			int routeNo = (int)extras.getInt("routeNo");
			parseData(baseData.getHtml(), route, routeNo);

			String note = getNote(baseData.getHtml(), routeNo);
			if ("".equals(note)) {
				txtNote.setVisibility(View.GONE);
			} else {
				txtNote.setText(note);				
			}
			
			
		}
	}

	/**
	 * 取得データをパースする
	 * @param document
	 */
	private void parseData(String html, String route, int routeNo) {
		
		try {
			FragmentManager manager = getSupportFragmentManager();
			TimetableFragment fragment = (TimetableFragment)manager.findFragmentById(R.id.result_fragment);
			fragment.parseTimetable(Jsoup.parse(html), route, routeNo);

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}
	
	private String getNote(String html, int routeNo) {
		String note = "";
		Document document = Jsoup.parse(html);
		
		Element tableTag = document.getElementsByTag("table").get(3).getElementsByTag("td").get(2);
		
//		Elements trTag = tableTag.getElementsByIndexEquals(6);

		if (tableTag != null) {
			note = tableTag.text();
		}
		return note;
	}
}
