package jp.gaomar.mytem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jp.gaomar.mytem.quickaction.ActionItem;
import jp.gaomar.mytem.quickaction.QuickAction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CreateActivity extends Activity {
	// RequestCode
	private static final int REQUEST_GALLERY = 1;
	private static final int REQUEST_CAMERA = 2;

	// 商品情報(MytemMaster)のキー
	private static final String KEY_MYTEM_MASTER = "MYTEM_MASTER";

	// JANコード
	private TextView janText = null;
	// 商品名
	private EditText nameEdit = null;
	// 登録ボタン
	private Button createButton = null;
	// プログレスアイコン
	private ProgressDialog progressDialog = null;
	// 商品の画像
	private ImageButton mytemImageView = null;
	private Bitmap mytemImage = null;
	// リクエストコードの値（どこから呼び出されたか）
	private int requestCode = 0;
	// 商品情報
	private MytemMaster mytemMaster = null;
	// カメラ撮影用
	private Uri mPictureUri;
	
	// 確認ダイアログ
	AlertDialog verificationDialog =null;
	
	
	// QuickAction のアイテム カメラ
	ActionItem itemCamera = null;
	// QuickAction のアイテム ギャラリー
	ActionItem itemGallery = null;
	// QuickAction のアイテム ホーム
	ActionItem itemHome = null;

	// WEB登録用スレッド
	EntryAsyncTask entry = null;

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
	}

	
	/**
	 * CreateActivityがインテントで呼び出されたときに呼ばれる
	 * リクエストコードとMytemMasterがセットされていないと終了します
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);

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
		
		// MytemMasterから情報を取り出す
		String nmJancode = mytemMaster.getJanCode();
		String nmName = mytemMaster.getName();
		Bitmap nmImage = mytemMaster.getImage();

		// UIにMytemMasterの情報をセット
		if (nmJancode != null)
			janText.setText(nmJancode);
		if (nmName != null)
			nameEdit.setText(nmName);

		// MytemMasterが全部埋まっている場合は、既に登録されているので終了
		if (nmJancode != null && nmName != null && nmImage != null) {
			Toast.makeText(this, R.string.sql_already_created, Toast.LENGTH_LONG).show();
			finish();
		}
		
		// QuickAction用のItemを初期化
		initActionItem();

	}
	/**
	 * ボタンとかエディットボックスとかのUIを取ってくる
	 */
	private void initUi(){
		janText = (TextView) findViewById(R.id.JanText);
		nameEdit = (EditText) findViewById(R.id.NameEdit);
		createButton = (Button) findViewById(R.id.CreateButton);
		mytemImageView = (ImageButton) findViewById(R.id.MytemImageButton);
		
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.dialog_message));
        progressDialog.setCancelable(false);

	}

	/**
	 *  QuickActionのためのItemを作成 QuickAction自体は onLoadImageClick()で作成
	 */
	private void initActionItem(){
		Resources resources = getResources();
		
		itemCamera = new ActionItem();
		itemCamera.setTitle(resources.getString(R.string.create_quick_action_camera));
		itemCamera.setIcon(getResources().getDrawable(
				R.drawable.ic_popup_camera));
		itemCamera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				callCamera(); // カメラを起動
			}
		});

		itemGallery = new ActionItem();
		itemGallery.setTitle(resources.getString(R.string.create_quick_action_gallery));
		itemGallery.setIcon(getResources().getDrawable(
				R.drawable.ic_popup_photos));
		itemGallery.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				callGallery(); // ギャラリーを起動
			}
		});
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
	 * インテントがもどってきた時の動作
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param intent
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// dimens.xmlから値を取得 リサイズのパラメータ
		int resizeLength = (int) getResources().getDimension(
				R.dimen.image_longer_length);
		if (requestCode == REQUEST_CAMERA || requestCode == REQUEST_GALLERY) {
			if (resultCode == RESULT_OK) {
				Uri uri = null;
				if (requestCode == REQUEST_GALLERY) {// ギャラリー
					uri = intent.getData();
				} else {
					/*
					 * カメラの動作
					 * GalaxyS対策：uriをPath(String)から生成　
					 * callCamera側でセットしたUriはnullになってしまうらしい。
					 * Xperia対策：セットしたファイル名の通りに画像が作られないので、getData()からUriを取得
					 */					
					uri = mPictureUri;
					// Experia 2.1対策
					if (intent != null){
						Uri _uri = intent.getData();
						if(_uri != null)
							uri = intent.getData();
					}
				}
				try {
					// 画像の取得
					// URI -> image size -> small bitmap
					mytemImage = getImageFromUriUsingBitmapFactoryOptions(uri,
							resizeLength);
					// // URI -> bitmap -> small bitmap
//					 noodleImage = getImageFromUriUsingResizeImage(uri,resizeLength);

					// ビューに画像をセット
					mytemImageView.setImageBitmap(mytemImage);
		
				} catch (IOException e) {
					Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
							.show();
				} catch (NullPointerException e) {
					Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
							.show();
				} catch (OutOfMemoryError e) {
					Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
							.show();
				}
			}
		}
		// アクションバーのボタン動作をダッシュボードまで伝達させる
		else if(requestCode == RequestCode.CREATE2TIMER.ordinal()){
			setResult(resultCode, intent);
			finish();
		}
	}

	/**
	 * UriからBitmapを取得する
	 * メモリを節約のために
	 * 大まかに1/（2^n）にサイズを縮小してBitmapを読み込んで
	 * さらに、Bitmap.createで微調整
	 * 
	 * @param uri
	 * @param resizeLength
	 *            　リサイズパラメータ
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Bitmap getImageFromUriUsingBitmapFactoryOptions(Uri uri,
			int resizeLength) throws FileNotFoundException, IOException {
		if (uri == null)
			throw new NullPointerException();
		// UriからBitmapクラスを取得
		InputStream is = getContentResolver().openInputStream(uri);
		// オプション
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 画像サイズだけを取得するように設定　デコードはされない
		opts.inJustDecodeBounds = true;
		Bitmap image = BitmapFactory.decodeStream(is, null, opts);
		is.close();
		// デコードするように設定
		opts.inJustDecodeBounds = false;
		// 縦横比を固定したままリサイズ
		resizeOptions(opts, resizeLength);
		is = getContentResolver().openInputStream(uri);
		image = BitmapFactory.decodeStream(is, null, opts);
		is.close();		
		Bitmap rImage = resizeImage(image, resizeLength);
		return rImage;
	}

	/**
	 * UriからBitmapを取得する
	 * 
	 * @param uri
	 * @param resizeLength
	 *            　リサイズパラメータ
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Bitmap getImageFromUriUsingResizeImage(Uri uri, int resizeLength)
			throws FileNotFoundException, IOException {
		// UriからBitmapクラスを取得
		InputStream is = getContentResolver().openInputStream(uri);
		// メモリを大量に使うのでガベコレ これしておかないと、何回か呼び出されるとエラーで止まる
		System.gc();
		Bitmap tmp = BitmapFactory.decodeStream(is);
		is.close();
		// 縦横比を固定したままリサイズ
		Bitmap image = resizeImage(tmp, resizeLength);
		return image;
	}

	/**
	 * 縦横比を維持したまま画像をリサイズするメソッド 長い方の辺が引数のlengthの長さなる
	 * 
	 * @param img
	 * @param length
	 * @return
	 */
	public Bitmap resizeImage(Bitmap img, int length) {
		int height = img.getHeight();
		int width = img.getWidth();
		// 縦、横の長い方
		float longer = height < width ? (float) width : (float) height;
		// 伸縮するスケール
		float scale = length / longer;
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		// リサイズした画像を作成
		Bitmap dst = Bitmap
				.createBitmap(img, 0, 0, width, height, matrix, true);
		return dst;
	}

	/**
	 * 縦横比を維持したまま画像をリサイズするメソッド 長い方の辺が引数のlengthの長さなる
	 * 
	 * @param opts
	 * @param length
	 */
	public void resizeOptions(BitmapFactory.Options opts, int length) {
		int height = opts.outWidth;
		int width = opts.outHeight;
		// 縦、横の長い方
		float longer = height < width ? (float) width : (float) height;
		// 伸縮するスケール
		float scale = longer/length;
		// inSampleSizeが2の倍数しか受付ないので、端数を切り捨て
		// Log2(scale)を計算
		int log2= (int)(Math.log10(scale)/Math.log10(2));
		int scale_int = 1;
		for(int i=0;i<log2;i++)
			scale_int *= 2;
			
		// 置き換え
		opts.outHeight = Math.round(height / scale_int);
		opts.outWidth = Math.round(width / scale_int);
		opts.inSampleSize=scale_int;
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
	 * 画像読み込みボタンが押された時の動作 インテントでギャラリーかカメラを呼び出す
	 * 
	 * @param v
	 */
	public void onLoadImageClick(View v) {
		// ストレージが使えるか確認
		MytemManager mytemManager = new MytemManager(this);
		if(mytemManager.hasExternalStorage()){
			// QuickAction
			QuickAction qa = new QuickAction(v);
	
			qa.addActionItem(itemCamera);
			qa.addActionItem(itemGallery);
			qa.show();
		}
	}
		
	/**
	 * ギャラリーをインテントで起動
	 */
	private void callGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, REQUEST_GALLERY);
	}

	/**
	 * カメラをインテントで起動
	 */
	private void callCamera() {
		String filename = "RamenTimer_" + System.currentTimeMillis() + ".jpg";
		
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, filename);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		mPictureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		
		Intent intent = new Intent();
		intent.setAction("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mPictureUri);
		startActivityForResult(intent, REQUEST_CAMERA);
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
			mytemMaster = getMytemMaster();
		} catch (CreateNoImageException e) {
			Toast.makeText(this, R.string.create_set_image_message, Toast.LENGTH_LONG).show();
			createButton.setEnabled(true);		
			return;
		} catch (CreateNoNameException e) {
			nameEdit.setFocusable(true);
			nameEdit.setFocusableInTouchMode(true);
			nameEdit.requestFocus();
			Toast.makeText(this, R.string.create_set_name_message, Toast.LENGTH_LONG).show();
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
		View layout = inflater.inflate(R.layout.dialog_create_verification,
				(ViewGroup) findViewById(R.id.layout_verification_root));
		TextView jan_text = (TextView) layout.findViewById(R.id.JanText);
		jan_text.setText(mytemMaster.getJanCode());
		TextView name_text = (TextView) layout.findViewById(R.id.MytemName);
		name_text.setText(mytemMaster.getName());
		ImageView image = (ImageView) layout.findViewById(R.id.MytemImage);
		image.setImageBitmap(mytemMaster.getImage());
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
	
	/**
	 * ダイアログでOKがクリックされたとき
	 */
	OnClickListener dialogOkClick = new OnClickListener() {
		public void onClick(View v) {
			if(entry!=null && verificationDialog!=null){
				entry.execute(mytemMaster);			
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
		// ImageButtonを無効化する
		mytemImageView.setClickable(false);
		// EditTextを無効化する
		nameEdit.setEnabled(false);
		// プログレスアイコンの表示とアニメーションのセット
		progressDialog.show();
	}
	/**
	 * 登録ボタンとかタイマーボタンを表示してプログレスアイコンを非表示にする
	 */
	private void inputMode(){
		// 登録ボタンを消す (GONEなので空間ごと消える)
		createButton.setVisibility(View.VISIBLE);
		createButton.setEnabled(true);		
		// ImageButtonを押せるようにする
		mytemImageView.setClickable(true);
		// EditTextを入力可能にする
		nameEdit.setEnabled(true);
		// プログレス非表示
		progressDialog.dismiss();
		
	}
		
	/**
	 * logoボタンが押された時の動作
	 * 
	 * @param v
	 */
	public void onLogoClick(View v) {
		finish();
	}

	/**
	 * UIから登録情報を集めて返す
	 * 
	 * @return
	 * @throws CreateNoImageException 
	 */
	MytemMaster getMytemMaster() throws CreateNoImageException, CreateNoNameException {
		// EditTextやRadioGroupから状態を取得
		String jancode = janText.getText().toString();
		String name = nameEdit.getText().toString();
		// 画像の取得
		Bitmap image;
		if(mytemImage==null) // セットされていない場合なダミー画像を入れる
			throw new CreateNoImageException();
		else
			image = mytemImage;
		if(name.length() == 0)
			throw new CreateNoNameException();
		
		MytemMaster mytem = new MytemMaster(jancode, name, image);
		return mytem;
	}

	/**
	 * 商品情報を登録する
	 * 
	 * @author leibun
	 * 
	 */
	private class EntryAsyncTask extends
			AsyncTask<MytemMaster, Integer, Integer> {
		// 表示用にコンテキストを保持
		private Activity activity = null;
		private MytemManager nm;

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
		protected Integer doInBackground(MytemMaster... params) {
			try {
				// 商品情報をWebに登録
				nm.createMytemMaster(params[0]);
			} catch (DuplexMytemMasterException e) {
				Log.i("ramentimer.CreateActivity",ExceptionToStringConverter.convert(e));
				return RESULT_DUPLEX;
			} catch (GaeException e) {
				Log.e("ramentimer.CreateActivity",ExceptionToStringConverter.convert(e));
				return RESULT_ERROR_GAE;
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
			callItemActivity();
			activity.finish();
		}
	}

	/**
	 * 履歴登録をインテントで呼び出す
	 */
	private void callItemActivity() {
		Intent intent = new Intent(this, ItemActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, RequestCode.READER2HISTORY
				.ordinal());
		intent.putExtra(KEY_MYTEM_MASTER, mytemMaster);
		startActivityForResult(intent, RequestCode.READER2HISTORY.ordinal());
	}

}
