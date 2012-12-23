package jp.gaomar.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.gaomar.keihan.BusDestination;
import jp.gaomar.keihan.BusTimeTable;
import jp.gaomar.keihan.DestinationAdapter;
import jp.gaomar.keihan.R;
import jp.gaomar.keihan.TimetableActivity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DestinationFragment extends ListFragment {
	String BaseUrl = "http://www.keihanbus.jp/local/";

	
	public void onActivityCreated(Bundle savedInstanceState) {

		ArrayAdapter<BusDestination> adapter = new ArrayAdapter<BusDestination>(getActivity(),
		android.R.layout.simple_list_item_1);
		setListAdapter(adapter);
		super.onActivityCreated(savedInstanceState);
	}
	
	public void searchDestination(final Document document) {

		AsyncTask<Void, Void, List<BusDestination>> task = new AsyncTask<Void, Void, List<BusDestination>>() {

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
			protected List<BusDestination> doInBackground(Void... params) {
			
				try {
						ArrayList<BusDestination> data = parseDocument(document);
						return data;
				} catch (NullPointerException e) {
					return null;
				}
	
			}
	
			private ArrayList<BusDestination> parseDocument(Document document) {
				
				ArrayList<BusDestination> retList = new ArrayList<BusDestination>();
				
				Element content = document.getElementById("local");
				Elements links = content.getElementsByTag("a");  
				Elements routes = content.getElementsByTag("td");  

				int routeNo = 0;
				int wkRouteNo = 0;
				String befUrl = "";
				for (Element link : links) {  
				  String url = link.attr("href");
				  if (befUrl.length() == 0) befUrl = url;
				  
				  String distination = link.text();
				  if (!"".equals(distination)) {
					  String route = routes.get(routeNo).html().replaceAll("&nbsp; ", "");
					  BusDestination data = new BusDestination(distination, url, route);
					  if (url.equals(befUrl)) {
						  data.setRouteNo(wkRouteNo);
						  wkRouteNo++;
					  } else {
						  wkRouteNo = 0;
						  data.setRouteNo(wkRouteNo);
						  wkRouteNo++;
						  
					  }
					  retList.add(data);
					  routeNo++;
					  befUrl = url;
				  }
				}
				
				return retList;
	
			}
	
			@Override
			protected void onPostExecute(List<BusDestination> result) {
				setListShown(true);
		
				// 処理中ダイアログをクローズ
                progressDialog.dismiss();
                
				if(result != null) {
//					ArrayAdapter<BusDestination> adapter = new
//					ArrayAdapter<BusDestination>(getActivity(),android.R.layout.simple_list_item_1, result);
					
					DestinationAdapter adapter = new DestinationAdapter(getActivity(), 0, result);
					setListAdapter(adapter);
					getListView().setVerticalFadingEdgeEnabled(false);
					getListView().setHorizontalFadingEdgeEnabled(false);

					Toast.makeText(getActivity(), R.string.destination, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
				}
		
				super.onPostExecute(result);
	
			}
		};

		task.execute();
	}
	
	public void searchNoDestination(final Document document) {

		AsyncTask<Void, Void, List<BusDestination>> task = new AsyncTask<Void, Void, List<BusDestination>>() {

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
			protected List<BusDestination> doInBackground(Void... params) {
			
				try {
						ArrayList<BusDestination> data = parseDocument(document);
						return data;
				} catch (NullPointerException e) {
					return null;
				}
	
			}
	
			private ArrayList<BusDestination> parseDocument(Document document) {
				
				ArrayList<BusDestination> retList = new ArrayList<BusDestination>();
				
				Element content = document.getElementById("weekday");

				int busRoutesCnt = content.getElementsByClass("bus-routes").size();
				int routeNo = 0;
				for (int ii=0; ii<busRoutesCnt; ii++) {
					String route = content.getElementsByClass("bus-routes").get(ii).text().replace(" ", "");
					String destination = content.getElementsByClass("destination").get(ii).text();
					String html = document.html();
					BusDestination data = new BusDestination(destination, "", route, html);
					data.setRouteNo(routeNo);
					routeNo++;
					retList.add(data);

				}
								
				return retList;
	
			}
	
			@Override
			protected void onPostExecute(List<BusDestination> result) {
				setListShown(true);
		
				// 処理中ダイアログをクローズ
                progressDialog.dismiss();
                
				if(result != null) {
//					ArrayAdapter<BusDestination> adapter = new
//					ArrayAdapter<BusDestination>(getActivity(),android.R.layout.simple_list_item_1, result);
					
					DestinationAdapter adapter = new DestinationAdapter(getActivity(), 0, result);
					setListAdapter(adapter);
					getListView().setVerticalFadingEdgeEnabled(false);
					getListView().setHorizontalFadingEdgeEnabled(false);

					Toast.makeText(getActivity(), R.string.destination, Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
				}
		
				super.onPostExecute(result);
	
			}
		};

		task.execute();
	}
	
	public void searchDestination(String query) {

		AsyncTask<String, Void, List<BusDestination>> task = new AsyncTask<String, Void, List<BusDestination>>() {

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
			protected List<BusDestination> doInBackground(String... params) {
	
				if (params.length < 1) {
					return null;
				}
		
				if (TextUtils.isEmpty(params[0])) {
					return null;
				}
		
				String url = params[0];
		
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet hg = new HttpGet(url);
		
				try {
					HttpResponse httpResponse = httpClient.execute(hg);
		
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						Document document = Jsoup.connect(url).get();
						ArrayList<BusDestination> data = parseDocument(document);
						hg.abort();
						return data;
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
	
			private ArrayList<BusDestination> parseDocument(Document document) {
				
				ArrayList<BusDestination> retList = new ArrayList<BusDestination>();
				
				Element content = document.getElementById("local");
				Elements links = content.getElementsByTag("a");  
				Elements routes = content.getElementsByTag("td");  

				int routeNo = 0;
				int wkRouteNo = 0;
				for (Element link : links) {  
				  String url = link.attr("href");  
				  String distination = link.text();
				  if (!"".equals(distination)) {
					  String route = routes.get(routeNo).html().replaceAll("&nbsp; ", "");
					  BusDestination data = new BusDestination(distination, url, route);				
					  if (route.length() == 0) {
						  data.setRouteNo(wkRouteNo);
						  wkRouteNo++;
					  }
					  retList.add(data);
					  routeNo++;
				  }
				}
				
				return retList;
	
			}
	
			@Override
			protected void onPostExecute(List<BusDestination> result) {
				setListShown(true);
		
				// 処理中ダイアログをクローズ
                progressDialog.dismiss();
                
				if(result != null) {
//					ArrayAdapter<BusDestination> adapter = new
//					ArrayAdapter<BusDestination>(getActivity(),android.R.layout.simple_list_item_1, result);
					
					DestinationAdapter adapter = new DestinationAdapter(getActivity(), 0, result);
					setListAdapter(adapter);
					getListView().setVerticalFadingEdgeEnabled(false);
					getListView().setHorizontalFadingEdgeEnabled(false);

				} else {
					Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
				}
		
				super.onPostExecute(result);
	
			}
		};

		task.execute(query);
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO 自動生成されたメソッド・スタブ
		super.onListItemClick(l, v, position, id);
		
		BusDestination data = (BusDestination) l.getAdapter().getItem(position);
		getTimetable(data, BaseUrl + data.getUrl());
		
//		BusTimeTable timetable = new BusTimeTable(document.html());
//		Intent intent = new Intent(getActivity(), TimetableActivity.class);
//		intent.putExtra("timetable", timetable);
//		intent.putExtra("route", data.getRoute());
//		intent.putExtra("dest", data.getDestination());
//		startActivity(intent);
	}
	
	private void getTimetable(final BusDestination data, String url) {
		AsyncTask<String, Void, Document> task = new AsyncTask<String, Void, Document>() {

			private ProgressDialog progressDialog = null;
			
			@Override
			protected void onPreExecute() {
				// バックグラウンドの処理前にUIスレッドでダイアログ表示
		        progressDialog = new ProgressDialog(getActivity());
		        progressDialog.setMessage(getResources().getText(
		                        R.string.data_loading));
		        progressDialog.setIndeterminate(true);
		        progressDialog.show();
		        
				super.onPreExecute();
			}

			@Override
			protected Document doInBackground(String... params) {
		
				String url = params[0];
		
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet hg = new HttpGet(url);
		
				try {
					if (data.getHtml() != null) {
						return Jsoup.parse(data.getHtml());
					} else {
						HttpResponse httpResponse = httpClient.execute(hg);
						
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							Document document = Jsoup.connect(url).get();
							hg.abort();
							return document;
						} else {
							hg.abort();
							return null;
						}		
						
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
					BusTimeTable timetable = new BusTimeTable(document.html());
					Intent intent = new Intent(getActivity(), TimetableActivity.class);
					intent.putExtra("timetable", timetable);
					intent.putExtra("route", data.getRoute());
					intent.putExtra("dest", data.getDestination());
					intent.putExtra("routeNo", data.getRouteNo());
					startActivity(intent);

					super.onPostExecute(document);
				} catch (NullPointerException e) {
					Toast.makeText(getActivity(), R.string.nullData, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}

			}
		};
		task.execute(url);

	}

}
