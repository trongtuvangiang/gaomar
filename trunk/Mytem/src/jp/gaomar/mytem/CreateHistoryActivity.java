package jp.gaomar.mytem;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jp.gaomar.mytem.quickaction.ActionItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CreateHistoryActivity extends Activity {

    // AdMakerさんから伝えられたURL
    private static final String ADMAKER_URL = "http://images.ad-maker.info/apps/3cxim1xrurk9.html";
    // AdMakerから伝えられたsiteID
    private static final String ADMAKER_SITEID = "881";
    // AdMakerから伝えられたsiteID
    private static final String ADMAKER_ZONEID = "3452";

	// 商品情報(MytemMaster)のキー
	private static final String KEY_MYTEM_MASTER = "MYTEM_MASTER";

	// JANコード
	private TextView janText = null;
	// 商品名
	private TextView nameText = null;
	// 商品の画像
	private ImageView mytemImageView = null;
	// 店名
	private EditText shopNameEdit = null;
	// 値段
	private EditText priceEdit = null;
	// 日付ボタン
	private Button dateButton = null;
	// コメント
	private EditText noteEdit = null;
	// 登録ボタン
	private Button createButton = null;
	// プログレスアイコン
	private ProgressDialog progressDialog = null;
	// リクエストコードの値（どこから呼び出されたか）
	private int requestCode = 0;
	// 商品情報
	private MytemMaster mytemMaster = null;	
	// 商品履歴情報
	private MytemHistory mytemHistory = null;	
	// 確認ダイアログ
	AlertDialog verificationDialog =null;
	// QuickAction のアイテム ホーム
	ActionItem itemHome = null;
	// 日付
	private int mYear, mMonth, mDay;
	static final int DATE_DIALOG_ID = 1;
	
	// WEB登録用スレッド
	EntryAsyncTask entry = null;

	/**
	 * CreateActivityがインテントで呼び出されたときに呼ばれる
	 * リクエストコードとMytemMasterがセットされていないと終了します
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_create_history);

		// リクエストコードを取得
		Intent intent = getIntent();
		requestCode = intent.getIntExtra(RequestCode.KEY_RESUEST_CODE, -1);
		// リクエストコードがセットされてない場合は終了
		if (requestCode == -1)
			finish();
		
		// ボタンとかエディットボックスとかのViewをメンバー変数に格納
		initUi();
		
		// 情報の取得
		mytemMaster = (MytemMaster) intent
				.getParcelableExtra(KEY_MYTEM_MASTER);

		String nmJancode = mytemMaster.getJanCode();
		String nmName = mytemMaster.getName();
		Bitmap nmImage = mytemMaster.getImage();

		// UIにMytemMasterの情報をセット
		if (nmJancode != null)
			janText.setText(nmJancode);
		if (nmName != null)
			nameText.setText(nmName);
		if (nmImage != null)
			mytemImageView.setImageBitmap(nmImage);
		
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		
//        adView();

	}

//    public void adView() {
//        // AdMakerの広告を表示させる
//        libAdMaker ad = (libAdMaker)findViewById(R.id.admakerview);
//        ad.setActivity(CreateHistoryActivity.this);
//        ad.siteId = ADMAKER_SITEID;
//        ad.zoneId = ADMAKER_ZONEID;
//        ad.setUrl(ADMAKER_URL);
//        ad.setVisibility(libAdMaker.VISIBLE);
//        ad.start();	
//    }

	/**
	 * ボタンとかエディットボックスとかのUIを取ってくる
	 */
	private void initUi(){
		janText = (TextView) findViewById(R.id.JanText);
		nameText = (TextView) findViewById(R.id.NameText);
		mytemImageView = (ImageView) findViewById(R.id.MytemImageView);
		shopNameEdit = (EditText) findViewById(R.id.NameEdit);
		priceEdit  = (EditText) findViewById(R.id.PriceEdit);
		dateButton = (Button) findViewById(R.id.PostDate);
		noteEdit  = (EditText) findViewById(R.id.NoteEdit);
		createButton = (Button) findViewById(R.id.CreateButton);
		
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.dialog_message));
        progressDialog.setCancelable(false);

	}
	
	/**
	 * 画面が回転時に呼び出される
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * アクションバーの履歴ボタンが押されたとき インテントに（RequestCode）をセットしてfinish()
	 * 
	 * @param v
	 */
	public void onHistoryButtonClick(View v) {
		Intent intent = new Intent();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, RequestCode.ACTION_HISTORY
				.ordinal());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 読込ボタンが押されたとき
	 * 
	 * @param view
	 */
	public void onReaderButtonClick(View view) {

		int requestCode = RequestCode.DASHBORAD2READER.ordinal();
		gotoReaderActivity(requestCode);
	}
	
	/**
	 * リーダーの起動 
	 * 
	 * @param requestCode
	 */
	private void gotoReaderActivity(int requestCode) {
		Intent intent = new Intent(this, ReaderActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		startActivityForResult(intent, requestCode);
		finish();
	}


	/**
	 * 登録ボタンが押された時の動作
	 * 
	 * @param v
	 */
	public void onCreateClick(View v) {
		// ソフトウェアキーボードを非表示にする
		InputMethodManager inputMethodManager =   
             (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
		inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);  
       
		//登録ボタンの二重押し防止
		createButton.setEnabled(false);		
		
		try {
			mytemHistory = getMytemHistory();
		} catch (CreateNoNameException e) {
			shopNameEdit.setFocusable(true);
			shopNameEdit.setFocusableInTouchMode(true);
			shopNameEdit.requestFocus();
			Toast.makeText(this, R.string.history_shop_message, Toast.LENGTH_LONG).show();
			createButton.setEnabled(true);		
			return;			
		} catch (NumberFormatException e) {
			priceEdit.setFocusable(true);
			priceEdit.setFocusableInTouchMode(true);
			priceEdit.requestFocus();
			Toast.makeText(this, R.string.history_number_message, Toast.LENGTH_LONG).show();
			createButton.setEnabled(true);		
			return;
		} catch (ParseException e) {
			dateButton.setFocusable(true);
			dateButton.setFocusableInTouchMode(true);
			dateButton.requestFocus();
			Toast.makeText(this, R.string.history_date_message, Toast.LENGTH_LONG).show();
			createButton.setEnabled(true);		
			return;			
		} catch (Exception e) {
			Toast.makeText(this, R.string.create_fill_form_message, Toast.LENGTH_LONG).show();
			createButton.setEnabled(true);		
			return;
		}
		entry = new EntryAsyncTask(this);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_create_history_verification,
				(ViewGroup) findViewById(R.id.layout_verification_root));
		TextView name_text = (TextView) layout.findViewById(R.id.ShopName);
		name_text.setText(mytemHistory.getShopName());
		TextView price_text = (TextView) layout.findViewById(R.id.Price);
		price_text.setText(mytemHistory.getPrice() + "円");
		TextView date_text = (TextView) layout.findViewById(R.id.PostDate);
		date_text.setText(mytemHistory.getSimpleDateFormat().format(mytemHistory.getPostDate()));
		TextView note_text = (TextView) layout.findViewById(R.id.Note);
		note_text.setText(mytemHistory.getNote());
		
		Button okButton = (Button) layout.findViewById(R.id.CreateDialogOkButton);
		okButton.setOnClickListener(dialogOkClick);
		Button cancelButton = (Button) layout.findViewById(R.id.CreateDialogCancelButton);
		cancelButton.setOnClickListener(dialogCancelClick);

		// 確認ダイアログの作成
		verificationDialog = new CustomAlertDialog(this, R.style.CustomDialog);
		verificationDialog.setTitle(R.string.dialog_create_verification_title);
		verificationDialog.setView(layout);
		verificationDialog.setCancelable(false);
		verificationDialog.show();		
	}
	
	public void onDatePicker(View v) {
		showDialog(DATE_DIALOG_ID);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		try {
			switch (id) {
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this,
			            mDateSetListener,
			            mYear, mMonth, mDay);			
			}
		} catch (Exception e) {
		}

		return null;
	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener =
			new DatePickerDialog.OnDateSetListener() {				
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;	
                    updateDisplay();
				}
			};

	private void updateDisplay() {
		try {
			dateButton.setText(
			    new StringBuilder()                    
			    		.append(mYear).append("年")        
			    		.append(mMonth + 1).append("月")
			            .append(mDay).append("日")
			            );
		} catch (Exception e) {
		}
    }

	/**
	 * ダイアログでOKがクリックされたとき
	 */
	OnClickListener dialogOkClick = new OnClickListener() {
		public void onClick(View v) {
			if(entry!=null && verificationDialog!=null){
				entry.execute(mytemHistory);			
				progressMode();
				verificationDialog.dismiss();
			}
		}
	}; 

	/**
	 * ダイアログでキャンセルがクリックされたとき
	 */
	OnClickListener dialogCancelClick = new OnClickListener() {
		public void onClick(View v) {
			if(verificationDialog!=null)
				verificationDialog.cancel();
			createButton.setEnabled(true);		
		}
	};
	/**
	 * 登録ボタンとかタイマーボタンを消してプログレスアイコンを表示する
	 */
	private void progressMode(){
		// 登録ボタンを消す (GONEなので空間ごと消える)
		createButton.setVisibility(View.GONE);
		// EditTextを無効化する
		shopNameEdit.setEnabled(false);
		priceEdit.setEnabled(false);
		dateButton.setEnabled(false);
		noteEdit.setEnabled(false);
		// プログレスアイコンの表示とアニメーションのセット
		progressDialog.show();
	}
	
	/**
	 * 登録ボタンとか表示してプログレスアイコンを非表示にする
	 */
	private void inputMode(){
		// 登録ボタンを消す (GONEなので空間ごと消える)
		createButton.setVisibility(View.VISIBLE);
		createButton.setEnabled(true);
		// EditTextを入力可能にする
		shopNameEdit.setEnabled(true);
		priceEdit.setEnabled(true);
		dateButton.setEnabled(true);
		noteEdit.setEnabled(true);
		// プログレス非表示
		progressDialog.dismiss();
		
	}
		
	/**
	 * logoボタンが押された時の動作
	 * 
	 * @param v
	 */
	public void onLogoClick(View v) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	/**
	 * UIから登録情報を集めて返す
	 * 
	 * @return
	 * @throws ParseException 
	 * @throws CreateNoImageException 
	 */
	MytemHistory getMytemHistory() throws CreateNoNameException, NumberFormatException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		
		// EditTextやRadioGroupから状態を取得
		String shopName = shopNameEdit.getText().toString();
		if (shopName.length() == 0) {
			throw new CreateNoNameException();
		}
		int price = 0;
		try {
			price = Integer.parseInt(priceEdit.getText().toString());
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			throw new NumberFormatException() ;
		}
		Date postDate = sdf.parse(dateButton.getText().toString());
		String note = noteEdit.getText().toString();
				
		MytemHistory mytem = new MytemHistory(shopName, price, postDate, note);
		return mytem;
	}

	/**
	 * 商品情報を登録する
	 * 
	 * @author leibun
	 * 
	 */
	private class EntryAsyncTask extends
			AsyncTask<MytemHistory, Integer, Integer> {
		// 表示用にコンテキストを保持
		private Activity activity = null;
		private MytemManager nm;
		private AlertDialog dialog = null;

		private static final int RESULT_CREATE_OK = 0; // 登録成功
		private static final int RESULT_ERROR_SQLITE = 1;// SQLITEでエラー
		private static final int RESULT_ERROR_GAE = 2; // Webへの登録でエラー
		private static final int RESULT_DUPLEX = 3; // 重複登録

		/**
		 * コンストラクタ
		 * 
		 * @param context
		 */
		public EntryAsyncTask(Activity activity) {
			this.activity = activity;
			this.nm = new MytemManager(activity);
		}

		/**
		 * Web登録があるので別スレッドで実行
		 * 
		 * @params params
		 */
		@Override
		protected Integer doInBackground(MytemHistory... params) {
			try {
				// 商品情報をWebに登録
				nm.createMytemHistory(mytemMaster.getJanCode(), 
						mytemMaster.getName(), params[0]);
			} catch (DuplexMytemMasterException e) {
				Log.i("ramentimer.CreateActivity",ExceptionToStringConverter.convert(e));
				return RESULT_DUPLEX;
			} catch (GaeException e) {
				Log.e("ramentimer.CreateActivity",ExceptionToStringConverter.convert(e));
				return RESULT_ERROR_GAE;
			} catch (SQLException e) {
				Log.e("ramentimer.CreateActivity",ExceptionToStringConverter.convert(e));
				return RESULT_ERROR_SQLITE;
			}
			return RESULT_CREATE_OK;
		}

		/**
		 * 
		 * doInBackgroundが呼ばれた後に呼び出される
		 */
		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			case RESULT_CREATE_OK:
				Toast.makeText(activity, R.string.sql_complete, Toast.LENGTH_LONG).show();
				// アニメーションの停止
				progressDialog.dismiss();
				break;
			case RESULT_DUPLEX:
				Toast.makeText(activity, R.string.sql_already_created, Toast.LENGTH_LONG).show();
				// アニメーションの停止
				progressDialog.dismiss();
				break;
			case RESULT_ERROR_GAE:
				Toast.makeText(activity, R.string.sql_gae_entry_error, Toast.LENGTH_LONG)
						.show();
				// UIを入力可能モードにする
				inputMode();
				return;
			case RESULT_ERROR_SQLITE:
				Toast.makeText(activity, R.string.sql_local_entry_error, Toast.LENGTH_LONG)
						.show();
				// UIを入力可能モードにする
				inputMode();
				return;
			}
			activity.finish();
		}
	}

}
