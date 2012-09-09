package jp.gaomar.osakacitybus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.Adlantis.Android.AdlantisView;
import jp.co.imobile.android.AdRequestResult;
import jp.co.imobile.android.AdView;
import jp.co.imobile.android.AdViewRequestListener;
import jp.gaomar.osakacitybus.db.DBAdapter;
import jp.gaomar.osakacitybus.fragment.BusStationFragment;
import jp.gaomar.osakacitybus.fragment.DestinationFragment;
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
import android.content.pm.PackageManager.NameNotFoundException;
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
	String BaseUrl = "http://kensaku.kotsu.city.osaka.jp/bus/dia/";

	static DBAdapter dbAdapter;
	
	private AdWhirlLayout adWhirlLayout;
	private jp.co.imobile.android.AdView imobileAd;
	private AdlantisView adlantisAd;
	private NendAdView nendAd;
	MasAdView mad;
	
	Toast finToast;
	// backkey�Ŏg���X���b�h�p////////////////////
	Timer bTimer = null;
	// �����Ă邩�����ĂȂ����̃`�F�b�N�p
	boolean bChk = false;
	//�o�b�N�����s���邩�ǂ���
	int bStar = 0;
	//�b���������
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
	    finToast =Toast.makeText(this, "[�߂�]��������x�����ƏI��", Toast.LENGTH_SHORT);
        	    
	    textView = (AutoCompleteTextView) findViewById(R.id.auto_complete);
	    
	    dbAdapter = new DBAdapter(this);
	    
	    updateTags();
	    

	    ImageButton btnDel = (ImageButton) findViewById(R.id.btnDelete);
	    btnDel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				textView.setText("");
				
				dbAdapter.open();
				try {
					dispHistory();
				} catch (Exception e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				} finally {
					dbAdapter.close();
				}
				
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
					    	// �s�����\������
					    	MainActivity.this.getTimetable(id);
					    	dbAdapter.saveHistory(textView.getText().toString());
					    } else {
						   // �o�X�����\������
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
	    
	    // �L��������
	    adInit();

	}
    
	private void dispHistory() {
	    // �o�X�◚����\������
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
	}

	private void searchTimeTable() {
	    dbAdapter.open();
	    try {
			String id = dbAdapter.searchID(textView.getText().toString() );

			if (id.length() != 0) {
				MainActivity.this.getTimetable(id);
				dbAdapter.saveHistory(textView.getText().toString());
			} else {
			   // �o�X�����\������
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
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} finally {
		    dbAdapter.close();
		}

	}
	
	/**
	 * �L��������
	 */
	private void adInit() {
		imobileAd = AdView.createForAdWhirl(this, 27269, 51055);
        mad = new MasAdView(this);
	    mad.setAuid("248322");
	    nendAd = new NendAdView(this, 13045, "240ff4b1e90dc46c75b471d18ec5ad691cb93350");
	    LinearLayout parentLayout = (LinearLayout) findViewById(R.id.ad_layout); //�L����\������LinearLayout���擾
	    adlantisAd = new AdlantisView(this); //AdLantis�̃r���[���쐬
	    //�^�C���A�E�g�̐ݒ�
	    adWhirlLayout = new AdWhirlLayout(this, "1915c87bc0b840c6856f9b50f043decd");
	    //AdWhirl�Ǘ���ʂ�SDK Key:����͂���
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
		// �L���擾�J�n
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
	    updateTags(tags);// auto complete��⃊�X�g�̍X�V
	}
	
	private void updateTags(String[] tags) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, tags);
				textView.setAdapter(adapter);
	}
	  
	/**
	 * �S�Ă̍s���擾����
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
			
			dispHistory();
			
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
			
			// HTML�̃h�L�������g���擾		
			AsyncTask<Void, Void, ArrayList<BusStation>> task = new AsyncTask<Void, Void, ArrayList<BusStation>>() {

				private ProgressDialog progressDialog = null;
				
				@Override
				protected void onPreExecute() {
					// �o�b�N�O���E���h�̏����O��UI�X���b�h�Ń_�C�A���O�\��
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
							url = url.append(BaseUrl).append("gojhuon/gojhuon_")
									.append(strAry[ii])
									.append(".html");

							HttpGet hg = new HttpGet(url.toString());
							HttpResponse httpResponse = httpClient.execute(hg);
				
							if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
								Document document = Jsoup.connect(url.toString()).get();
								hg.abort();
								

								Element content = document.getElementById("list_table");
								Elements options = content.getElementsByTag("td");
								
								for (Element option : options) { 
									String name = option.getElementsByTag("b").get(0).text();
									String optionId = option.getElementsByTag("a").get(0).attr("href");
									
									BusStation tr = new BusStation(optionId, name);
									ContentValues cv = dbAdapter.getContentValues(optionId, name);
									list.add(tr);
									valueList.add(cv);									
									
								}

							} else {
								hg.abort();
							}		

						}

						// ��C��Insert
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
					// �������_�C�A���O���N���[�Y
			        progressDialog.dismiss();

				    String tags[] = getAllString(list);
				    updateTags(tags);// auto complete��⃊�X�g�̍X�V

				    Toast.makeText(MainActivity.this, R.string.datadone, Toast.LENGTH_SHORT).show();
				}
			};
			task.execute();

			

		} catch (Exception e) {
		} finally {
		}
		
	}
	
	private void getTimetable(final String id) {
		// HTML�̃h�L�������g���擾		
		AsyncTask<Void, Void, Document> task = new AsyncTask<Void, Void, Document>() {

			private static final String ERROR = "can not search";
			private ProgressDialog progressDialog = null;
			
			@Override
			protected void onPreExecute() {
				// �o�b�N�O���E���h�̏����O��UI�X���b�h�Ń_�C�A���O�\��
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
				String wkId = id.replace("../", "");
				String url = BaseUrl + wkId;
		
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
				// �������_�C�A���O���N���[�Y
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

//					} else {
//						FragmentManager manager = getSupportFragmentManager();
//						FragmentTransaction fragmentTransaction = manager.beginTransaction();
//
//						DestinationFragment fragment = (DestinationFragment)manager.findFragmentById(R.id.result_fragment);
//						BusStationFragment fragment2 = (BusStationFragment)manager.findFragmentById(R.id.busStation_fragment);
//						fragment.searchNoDestination(document);
//						
//						fragmentTransaction.show(fragment);
//						fragmentTransaction.hide(fragment2);
//						fragmentTransaction.commit();
//						
//					}
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// �Ȃ��keydown���𔻒f�@����̓o�b�N�L�[
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(bStar == 0){
				finToast.show();
				bStar = 1;
				backCountStart();
			}else {
				backCountStop();
				finToast.cancel();
				finish();
			}
	
		}
		return false;
	}

	// //////////////////////�^�C�}�[/////////////
	public void backCountStart() {
		// �����Ă��炻�̂܂�
		if (bChk) {
			// �~�܂��Ă���N��
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
			}, 1000, 1000); // ����N���̒x���Ǝ����w��B�P�ʂ�ms
			bChk = true;
		}
	}

	public void backCountStop() {
	// �����Ă������A�~�܂��Ă���X���[
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

//    @Override
//    public void onResume(){
//    	super.onResume();
//    	//start���\�b�h�ōL�����[�e�[�V�������ĊJ����܂�
//    	mad.start();
//    }
//    
//    @Override
//    public void onPause(){
//    	super.onPause();
//    	//stop���\�b�h�ōL�����[�e�[�V��������~����܂�
//    	mad.stop();
//    }

}