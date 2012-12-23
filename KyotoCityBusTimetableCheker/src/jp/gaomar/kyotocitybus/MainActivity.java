package jp.gaomar.kyotocitybus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.Adlantis.Android.AdlantisView;
import jp.beyond.bead.Bead;
import jp.beyond.bead.Bead.ContentsOrientation;
import jp.co.cayto.appc.sdk.android.WebViewActivity;
import jp.co.imobile.android.AdRequestResult;
import jp.co.imobile.android.AdView;
import jp.co.imobile.android.AdViewRequestListener;
import jp.gaomar.kyotocitybus.db.DBAdapter;
import jp.gaomar.kyotocitybus.fragment.BusStationFragment;
import jp.gaomar.kyotocitybus.fragment.DestinationFragment;
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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
	String BaseUrl = "http://www.city.kyoto.jp/kotsu/busdia/hyperdia/";

	private static final int MENU_ID_MENU1 = (Menu.FIRST + 1);
	private static final int MENU_ID_MENU2 = (Menu.FIRST + 2);

	static DBAdapter dbAdapter;
	
	private AdWhirlLayout adWhirlLayout;
	private jp.co.imobile.android.AdView imobileAd;
	private AdlantisView adlantisAd;
	private NendAdView nendAd;
	MasAdView mad;
	
	Toast finToast;
	// backkeyで使うスレッド用////////////////////
	Timer bTimer = null;
	// 動いてるか動いてないかのチェック用
	boolean bChk = false;
	//バックを実行するかどうか
	int bStar = 0;
	//秒数かうんと
	int bcount = 0;
	
    @Override
	protected void onResume() {
		super.onResume();
		bStar = 0;
	}
	
	protected void onPause() {
		super.onPause();
		backCountStop();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	    setContentView(R.layout.main);
	    
	    StringBuffer appTitle = new StringBuffer();
	    appTitle.append(getString(R.string.app_name)).append(" [v").append(getVersionName(this)).append("]");
	    this.setTitle(appTitle.toString());
	    finToast =Toast.makeText(this, "[戻る]をもう一度押すと終了", Toast.LENGTH_SHORT);
        	    
	    textView = (AutoCompleteTextView) findViewById(R.id.auto_complete);
	    
	    dbAdapter = new DBAdapter(this);
	    
		Bead.setSid("6be7853b31cb96b6767e921fc893219e978b51e1cfa09fb8");
		Bead.setContentsOrientation(ContentsOrientation.Portrait);
		Bead.requestAd(this);
		Bead.setOnFinishClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bead.endAd();
				
				finish();
//		        android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		
	    updateTags();
	    
	    ImageButton btnDel = (ImageButton) findViewById(R.id.btnDelete);
	    btnDel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				textView.setText("");
				dispHistory();				
			}
		});
	
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
					    	dbAdapter.saveHistory(textView.getText().toString());
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
					MainActivity.this.searchTimeTable();
				}

				return true;
			}
		});
	    
	    // 広告初期化
	    adInit();

	}
    
	
	private void dispHistory() {
		
		dbAdapter.open();
		
	    try {
			// バス停履歴を表示する
			ArrayList<BusStation> list = dbAdapter.getHistoryList();
   
			if (list.size() > 0) {
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

	
	private void searchTimeTable() {
	    dbAdapter.open();
	    try {
			String id = dbAdapter.searchID(textView.getText().toString() );

			if (id.length() != 0) {
				MainActivity.this.getTimetable(id);
				dbAdapter.saveHistory(textView.getText().toString());
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
	
	/**
	 * 広告初期化
	 */
	private void adInit() {
		imobileAd = AdView.createForAdWhirl(this, 27552, 51520);
        mad = new MasAdView(this);
	    mad.setAuid("249347");
	    nendAd = new NendAdView(this, 13312, "0602c0e152d40769610b50cab7539a69b4d01671");
	    LinearLayout parentLayout = (LinearLayout) findViewById(R.id.ad_layout); //広告を表示するLinearLayoutを取得
	    adlantisAd = new AdlantisView(this); //AdLantisのビューを作成
	    //タイムアウトの設定
	    adWhirlLayout = new AdWhirlLayout(this, "d47e9128dbdc420089eef1d998527f88");
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
			    
			    dispHistory();
				
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
					String[] strAry = getString(R.string.gojhuon).split(",");
					
				    dbAdapter.open();

					try {
				        ArrayList<BusStation> list = new ArrayList<BusStation>();
				        List<ContentValues> valueList = new ArrayList<ContentValues>();						        

						for (int ii=0; ii<strAry.length; ii++) {
							StringBuffer url = new StringBuffer();
							url = url.append(BaseUrl)
									.append(strAry[ii])
									.append(".htm");

							HttpGet hg = new HttpGet(url.toString());
							HttpResponse httpResponse = httpClient.execute(hg);
				
							if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
								Document document = Jsoup.connect(url.toString()).get();
								hg.abort();
								
								Element tableTag = document.getElementsByTag("table").first();
								Elements anchorTag = tableTag.getElementsByTag("a");
								for (Element anchor : anchorTag) { 
									String name = anchor.text();
									String optionId = anchor.attr("href");
																		
									BusStation tr = new BusStation(optionId, name);
									ContentValues cv = dbAdapter.getContentValues(optionId, name);
									list.add(tr);
									valueList.add(cv);									
									
								}

							} else {
								hg.abort();
							}		

						}

						// 一気にInsert
						dbAdapter.insertMany("", valueList, 1, true);

						return list;

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

				    try {
						String tags[] = getAllString(list);
						updateTags(tags);// auto complete候補リストの更新

						Toast.makeText(MainActivity.this, R.string.datadone, Toast.LENGTH_SHORT).show();
						
						dispHistory();
					} catch (NullPointerException e) {
						Toast.makeText(MainActivity.this, R.string.nullData, Toast.LENGTH_SHORT).show();
					} 
					
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
				String url = BaseUrl +id;
		
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
					FragmentManager manager = getSupportFragmentManager();
					FragmentTransaction fragmentTransaction = manager.beginTransaction();

					DestinationFragment fragment = (DestinationFragment)manager.findFragmentById(R.id.result_fragment);
					BusStationFragment fragment2 = (BusStationFragment)manager.findFragmentById(R.id.busStation_fragment);
					fragment.searchDestination(document);

					fragmentTransaction.show(fragment);
					fragmentTransaction.hide(fragment2);
					fragmentTransaction.commit();

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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// なんのkeydownかを判断　今回はバックキー
		if (keyCode == KeyEvent.KEYCODE_BACK) {
/*
			if(bStar == 0){
				finToast.show();
				bStar = 1;
				backCountStart();
			}else {
				backCountStop();
				finToast.cancel();
				finish();
			}
*/
			Bead.showAd(this);

		}
		return false;
	}

	// //////////////////////タイマー/////////////
	public void backCountStart() {
		// 動いてたらそのまま
		if (bChk) {
			// 止まってたら起動
		} else {
			bTimer = new Timer(true);
			bTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					if(bcount == 2){
						bStar =0;
						bcount = 0;
						backCountStop();
					}
					bcount++;
				}
			}, 1000, 1000); // 初回起動の遅延と周期指定。単位はms
			bChk = true;
		}
	}

	public void backCountStop() {
	// 動いてたら入る、止まってたらスルー
		if (bChk) {
			if (bTimer != null) {
				bTimer.cancel();
				bTimer = null;
			}
			bChk = false;
		}
	}

	private String getVersionName( Context context ) {
	    String ver;
	    try {
	        ver = context.getPackageManager().getPackageInfo( context.getPackageName(), 1 ).versionName;
	    } catch (NameNotFoundException e) {
	        ver = "";
	    }
	    return ver;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ID_MENU1 , Menu.NONE , getString(R.string.menu_station));
		menu.add(Menu.NONE, MENU_ID_MENU2 , Menu.NONE , getString(R.string.menu_app));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = true;
        Intent intent = new Intent();
        
        switch (item.getItemId()) {
        default:
            ret = super.onOptionsItemSelected(item);
            break;
        case MENU_ID_MENU1:
        	busStationDelete();
    		intent = new Intent(this, MainActivity.class);
        	startActivity(intent);
        	finish();
            ret = true;
            break;
        case MENU_ID_MENU2:
    		intent = new Intent(this, WebViewActivity.class);
			intent.putExtra("type", "pr_list");
        	startActivity(intent);
            ret = true;
            break;
        }
        return ret;
	}

	private void busStationDelete() {
	    dbAdapter.open();
	    try {
			dbAdapter.deleteAllNotes();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
		    dbAdapter.close();			
		}
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