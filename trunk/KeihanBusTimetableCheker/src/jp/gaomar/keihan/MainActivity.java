package jp.gaomar.keihan;

import java.io.IOException;
import java.util.ArrayList;

import jp.Adlantis.Android.AdlantisView;
import jp.co.imobile.android.AdRequestResult;
import jp.co.imobile.android.AdView;
import jp.co.imobile.android.AdViewRequestListener;
import jp.gaomar.fragment.BusStationFragment;
import jp.gaomar.fragment.DestinationFragment;
import jp.gaomar.keihan.db.DBAdapter;
import mediba.ad.sdk.android.openx.MasAdView;
import net.nend.android.NendAdView;

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
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;

public class MainActivity extends FragmentActivity implements AdWhirlInterface{
    /** Called when the activity is first created. */
	private AutoCompleteTextView textView;
	String BaseUrl = "http://www.keihanbus.jp/local/";
	String BusStationUrl = BaseUrl + "timetable_index2.html";
	String BusTimeTableUrl = BaseUrl + "timetable.php?stop_cd=";

	static DBAdapter dbAdapter;
	
	private AdWhirlLayout adWhirlLayout;
	private jp.co.imobile.android.AdView imobileAd;
	private AdlantisView adlantisAd;
	private NendAdView nendAd;
	MasAdView mad;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	    setContentView(R.layout.main);
	    	    
	    textView = (AutoCompleteTextView) findViewById(R.id.auto_complete);
	    
	    dbAdapter = new DBAdapter(this);
	    
	    updateTags();
	    
	    ImageButton btn = (ImageButton) findViewById(R.id.btnSearch);
	    btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager inputMethodManager =   
			            (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
			      inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0); 
			      
				String keyword = textView.getText().toString();
				if (!TextUtils.isEmpty(keyword)) {
				    dbAdapter.open();
				    String id = "";
				    try {
						id = dbAdapter.searchID(textView.getText().toString() );
					    if (id.length() != 0) {
					    	// 行き先を表示する
					    	MainActivity.this.getTimetable(id);
					    } else {
						   // バス停候補を表示する
						   ArrayList<BusStation> list = dbAdapter.getBusStationList(textView.getText().toString());
						   
							FragmentManager manager = getSupportFragmentManager();
							FragmentTransaction fragmentTransaction = manager.beginTransaction();
							BusStationFragment fragment = (BusStationFragment)manager.findFragmentById(R.id.busStation_fragment);
							DestinationFragment fragment2 = (DestinationFragment)manager.findFragmentById(R.id.result_fragment);
							fragment.getStationList(list);
							
							fragmentTransaction.show(fragment);
							fragmentTransaction.hide(fragment2);
							fragmentTransaction.commit();

					   }
					} catch (Exception e) {
					} finally {
					    dbAdapter.close();						
					}

				}
			}
		});
	    
	    textView.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				InputMethodManager inputMethodManager =   
			            (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
			      inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0); 
			      
				String keyword = textView.getText().toString();
				if (!TextUtils.isEmpty(keyword)) {
				    dbAdapter.open();
				    try {
						String id = dbAdapter.searchID(textView.getText().toString() );

						if (id.length() != 0) {
							MainActivity.this.getTimetable(id);
						} else {
						   // バス停候補を表示する
						   ArrayList<BusStation> list = dbAdapter.getBusStationList(textView.getText().toString());
						   
							FragmentManager manager = getSupportFragmentManager();
							FragmentTransaction fragmentTransaction = manager.beginTransaction();
							BusStationFragment fragment = (BusStationFragment)manager.findFragmentById(R.id.busStation_fragment);
							DestinationFragment fragment2 = (DestinationFragment)manager.findFragmentById(R.id.result_fragment);
							fragment.getStationList(list);
							
							fragmentTransaction.show(fragment);
							fragmentTransaction.hide(fragment2);
							fragmentTransaction.commit();

						}
					} catch (Exception e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					} finally {
					    dbAdapter.close();
					}

				}

				return true;
			}
		});
	    
	    // 広告初期化
	    adInit();

	}
    
	/**
	 * 広告初期化
	 */
	private void adInit() {
		imobileAd = AdView.createForAdWhirl(this, 24253, 45294);
        mad = new MasAdView(this);
	    mad.setAuid("216002");
	    nendAd = new NendAdView(this, 10002, "03ecb4b857292235c51fe1c3dddc692b2b327ad9");
	    LinearLayout parentLayout = (LinearLayout) findViewById(R.id.ad_layout); //広告を表示するLinearLayoutを取得
	    adlantisAd = new AdlantisView(this); //AdLantisのビューを作成
	    //タイムアウトの設定
	    adWhirlLayout = new AdWhirlLayout(this, "b0d3e62a53c2491aa5d9003837e4bfc5");
	    //AdWhirl管理画面のSDK Key:を入力する
	    adWhirlLayout.setAdWhirlInterface(this);
	    parentLayout.addView(adWhirlLayout);
	    
	}
	
	public void handleMediba() {
		mad.start();
		adWhirlLayout.adWhirlManager.resetRollover();
		adWhirlLayout.removeAllViews();
		adWhirlLayout.addView(mad);
//		adWhirlLayout.handler.post(new ViewAdRunnable(adWhirlLayout, (ViewGroup) mad));
		adWhirlLayout.rotateThreadedDelayed();
	}

	public void handleNend() {
		adWhirlLayout.adWhirlManager.resetRollover();
		adWhirlLayout.removeAllViews();
		adWhirlLayout.addView(nendAd);
		//adWhirlLayout.handler.post(new ViewAdRunnable(adWhirlLayout, (ViewGroup) nendAd));
		adWhirlLayout.rotateThreadedDelayed();
	}

	public void handleimobile() {
		imobileAd.setOnRequestListener(new AdViewRequestListener() {
			@Override
			public void onCompleted(AdRequestResult result, AdView sender) {
				adWhirlLayout.adWhirlManager.resetRollover();
				adWhirlLayout.removeAllViews();
				adWhirlLayout.addView(sender);
				adWhirlLayout.rotateThreadedDelayed();
			}
			@Override
			public void onFailed(AdRequestResult result, AdView sender) {
				adWhirlLayout.rollover();
			}
		});
		// 広告取得開始
		imobileAd.start();
	}

	public void handleAdlantis() {
		adlantisAd.showNextAd();
		adWhirlLayout.adWhirlManager.resetRollover();
		adWhirlLayout.removeAllViews();
		adWhirlLayout.addView(adlantisAd);
		adWhirlLayout.rotateThreadedDelayed();
	}
	private void updateTags() {
	    ArrayList<BusStation> row_list = this.getAllRows();
	    String tags[] = getAllString(row_list);
	    updateTags(tags);// auto complete候補リストの更新
	}
	
	private void updateTags(String[] tags) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, tags);
				textView.setAdapter(adapter);
	}
	  
	/**
	 * 全ての行を取得する
	 * @return
	 */
	public ArrayList<BusStation> getAllRows() {
	    ArrayList<BusStation> list = new ArrayList<BusStation>();
	    dbAdapter.open();
	    
	    Cursor c = dbAdapter.getAllNotes();
	    
	    try {
			if (c.moveToFirst()) {
			    int numRows = c.getCount();	    	    	
			    for (int i = 0; i < numRows; i++) {
			    	BusStation tr = new BusStation(c.getString(1), c.getString(2));
			    	list.add(tr);
			    	c.moveToNext();
			    }
			} else {
				this.initBusStation();
			}
		} catch (Exception e) {
		} finally {
		    c.close();
		    dbAdapter.close();			
		}
	    
	    
	    return list;
	}

	private String[] getAllString(ArrayList<BusStation> list) {
		int size = list.size();
		String[] str = new String[size];
		for (int i = 0; i < size; i++) {
			str[i] = list.get(i).getStationName();
		}
		return str;
	}
	
	private void initBusStation() {		
		try {
			
			// HTMLのドキュメントを取得		
			AsyncTask<Void, Void, ArrayList<BusStation>> task = new AsyncTask<Void, Void, ArrayList<BusStation>>() {

				private ProgressDialog progressDialog = null;
				
				@Override
				protected void onPreExecute() {
					// バックグラウンドの処理前にUIスレッドでダイアログ表示
			        progressDialog = new ProgressDialog(MainActivity.this);
			        progressDialog.setMessage(getResources().getText(
			                        R.string.data_init));
			        progressDialog.setIndeterminate(true);
			        progressDialog.setCancelable(false);
			        progressDialog.show();
			        
					super.onPreExecute();
				}

				@Override
				protected ArrayList<BusStation> doInBackground(Void... params) {
					
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet hg = new HttpGet(BusStationUrl);

				    dbAdapter.open();

					try {
						HttpResponse httpResponse = httpClient.execute(hg);
			
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							Document document = Jsoup.connect(BusStationUrl).get();
							hg.abort();
							

							Element content = document.getElementById("local");
							Elements options = content.getElementsByTag("option");  
					        ArrayList<BusStation> list = new ArrayList<BusStation>();

							for (Element option : options) {  
							  String optionLabel = option.attr("label");  
							  String optionId = option.attr("value");
							  
							  if (!"".equals(optionLabel) && !"".equals(optionId)) {
								  dbAdapter.saveNote(optionId, optionLabel);
								  BusStation tr = new BusStation(optionId, optionLabel);
								  list.add(tr);

							  }
							}
							

							return list;
						} else {
							hg.abort();
							return null;
						}		
					} catch (NullPointerException e) {
						return null;
					} catch (IOException e) {
						return null;
					} finally {
						dbAdapter.close();
					}
				}
				
				@Override
				protected void onPostExecute(ArrayList<BusStation> list) {			
					// 処理中ダイアログをクローズ
			        progressDialog.dismiss();

				    String tags[] = getAllString(list);
				    updateTags(tags);// auto complete候補リストの更新

				    Toast.makeText(MainActivity.this, R.string.datadone, Toast.LENGTH_SHORT).show();
				}
			};
			task.execute();

			

		} catch (Exception e) {
		} finally {
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
		        progressDialog = new ProgressDialog(MainActivity.this);
		        progressDialog.setMessage(getResources().getText(
		                        R.string.data_loading));
		        progressDialog.setIndeterminate(true);
		        progressDialog.setCancelable(false);
		        progressDialog.show();
		        
				super.onPreExecute();
			}

			@Override
			protected Document doInBackground(Void... params) {
				String url = BusTimeTableUrl + id;
		
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
					String title = document.title();

					if (title.indexOf("時刻表：行き先選択") >= 0) {
						FragmentManager manager = getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = manager.beginTransaction();

						DestinationFragment fragment = (DestinationFragment)manager.findFragmentById(R.id.result_fragment);
						BusStationFragment fragment2 = (BusStationFragment)manager.findFragmentById(R.id.busStation_fragment);
						fragment.searchDestination(document);

						fragmentTransaction.show(fragment);
						fragmentTransaction.hide(fragment2);
						fragmentTransaction.commit();

					} else {
						FragmentManager manager = getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = manager.beginTransaction();

						DestinationFragment fragment = (DestinationFragment)manager.findFragmentById(R.id.result_fragment);
						BusStationFragment fragment2 = (BusStationFragment)manager.findFragmentById(R.id.busStation_fragment);
						fragment.searchNoDestination(document);
						
						fragmentTransaction.show(fragment);
						fragmentTransaction.hide(fragment2);
						fragmentTransaction.commit();
						
					}
				} catch (Exception e) {
					Toast.makeText(MainActivity.this, ERROR, Toast.LENGTH_SHORT).show();
				}


				super.onPostExecute(document);

			}
		};
		task.execute();

	}

	@Override
	public void adWhirlGeneric() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	
//    @Override
//    public void onResume(){
//    	super.onResume();
//    	//startメソッドで広告ローテーションが再開されます
//    	mad.start();
//    }
//    
//    @Override
//    public void onPause(){
//    	super.onPause();
//    	//stopメソッドで広告ローテーションが停止されます
//    	mad.stop();
//    }

}