package jp.gaomar.onigirisalechecker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class HPCheckTask extends AsyncTask<Void, Void, DOMResult>{

	/** アドレス */
	public static final String BASE_ADDRESS = "http://www.sej.co.jp/cmp/";
	public static final String ONIGIRI_ADDRESS = BASE_ADDRESS + "onigiricmp.html";

	/** 一致パターン*/
	private final Pattern pDate = Pattern.compile("[0-9]{4}年[0-9]{2}月[0-9]{1,2}日");
	private final Pattern pKikan = Pattern.compile("(?<=【)[0-9]{1,}");
	
	/** プログレスダイアログ*/
    private ProgressDialog progressDialog;

	/** コンテキスト*/
	private Context mCtx;
	/** 今すぐチェックフラグ*/
	private boolean mNowFlg;
	/** 詳細ボタン*/
	private Button mButton;
	
	/**
	 * コンストラクタ
	 * @param ctx
	 */
	public HPCheckTask(Context ctx) {
		super();
		this.mCtx = ctx;
	}

	/**
	 * コンストラクタ
	 * @param ctx
	 * @param flg
	 */
	public HPCheckTask(Context ctx, boolean flg, Button btn) {
		super();
		this.mCtx = ctx;
		this.mNowFlg = flg;
		this.mButton = btn;		
	}

	/**
	 * HTTPクライアント取得
	 * @return
	 */
	private HttpClient getClient() {
	    // HTTPクライアントを生成
        HttpClient client = new DefaultHttpClient();
        // HTTPパラメーターを取得
        HttpParams httpParams = client.getParams();
        // HTTPタイムアウトを設定
        HttpConnectionParams.setSoTimeout(httpParams, 10000);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        return client;
	}

	/**
	 * ソース取得
	 * @param request
	 * @param client
	 * @return
	 */
	private InputSource httpGet(String request, HttpClient client) {
		InputSource source = null;
		
        try {
			HttpGet httpGet = new HttpGet(request);
			HttpResponse httpResponse = client.execute(httpGet);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_BAD_REQUEST) {
				return null;				
			}
			if (statusCode > HttpStatus.SC_BAD_REQUEST) {
				return null;
			}

			source = new InputSource(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (IllegalStateException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return source;
	}

	
	@Override
	protected void onPreExecute() {
		if (mNowFlg) {
			setupDialog(mCtx.getString(R.string.lbl_dialog));
		}
	}

	@Override
	protected DOMResult doInBackground(Void... params) {
		DOMResult result = new DOMResult();
        HttpClient client = getClient();
        InputSource source = httpGet(ONIGIRI_ADDRESS, client);        
		try {
			XMLReader reader = new Parser();
			reader.setFeature(Parser.namespacesFeature, false);
			reader.setFeature(Parser.namespacePrefixesFeature, false);

			Transformer transformer = TransformerFactory.newInstance()
			.newTransformer();					
			transformer.transform(new SAXSource(reader, source), result);

							
		} catch (TransformerConfigurationException e) {

			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {

			e.printStackTrace();
		} catch (TransformerException e) {

			e.printStackTrace();
		} catch (SAXNotRecognizedException e) {

			e.printStackTrace();
		} catch (SAXNotSupportedException e) {

			e.printStackTrace();
		}
		return result;

	}

	@Override
	protected void onPostExecute(DOMResult result) {
		dialogClose();
		Document doc = (Document) result.getNode();
		NodeList dlChilds = doc.getElementsByTagName("dl");

		try {
			for (int i = 0; i < dlChilds.getLength(); i++) {
				Element elem = (Element) dlChilds.item(i);
				Matcher m = pDate.matcher(elem.getTextContent());
				if (m.find()) {
					Matcher mKikan = pKikan.matcher(elem.getTextContent());
					Calendar now = Calendar.getInstance();
					Calendar start = Calendar.getInstance();
					Calendar end = Calendar.getInstance();
					start.setTimeInMillis(strToDate(m.group(), "yyyy年MM月dd日").getTime());
					int kikan = 0;
					if (mKikan.find()) {
						kikan = Integer.parseInt(mKikan.group());
						end.set(Calendar.DATE, start.get(Calendar.DATE) + kikan);
						// セール中かどうか
						if (start.before(now) && end.after(now)) {
							if (!mNowFlg) {
								// 通知する
								NotificationManager notificationManager = 
									(NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
								Notification notification = 
									new Notification(R.drawable.onigiri, 
											mCtx.getString(R.string.lbl_sale),
											System.currentTimeMillis());
				
								// 通知をクリックされたときのintent設定
								Intent newIntent = new Intent(mCtx, MainActivity.class);
								newIntent.putExtra("check", true);
								PendingIntent contentIntent = PendingIntent.getActivity(mCtx, 0, newIntent, 0);
				
								notification.setLatestEventInfo(mCtx.getApplicationContext(), 
										mCtx.getString(R.string.app_name), String.format(mCtx.getString(R.string.format_notification), m.group(), mKikan.group()), contentIntent);
				
								// 古い通知をクリアし、最新の情報を通知する
								notificationManager.cancelAll();
								notificationManager.notify(R.string.app_name, notification);
							} else {
								mButton.setVisibility(View.VISIBLE);
								// 今すぐ確認
								Toast.makeText(mCtx, mCtx.getString(R.string.lbl_sale), Toast.LENGTH_SHORT).show();
							}
						} else if (mNowFlg){
							mButton.setVisibility(View.GONE);							
							// 今すぐ確認
							Toast.makeText(mCtx, mCtx.getString(R.string.lbl_not_sale), Toast.LENGTH_SHORT).show();
							
						}
						break;						
					}				
				}
				
			}
		} catch (NumberFormatException e) {
		} catch (DOMException e) {
		} catch (Exception e) {			
		}

	}

	/**
	 * 文字列日付→指定フォーマット日付型変換
	 * 
	 * @param strdate
	 *            日付変換前文字列
	 * @param fmt
	 *            指定フォーマット(例:yyyy/MM/dd)
	 * @return
	 */
	public static Date strToDate(String strdate, String fmt) {
		Date RetDate = null;
		DateFormat parser = new SimpleDateFormat(fmt);
		try {
			RetDate = parser.parse(strdate);
		} catch (ParseException e) {
		}

		return RetDate;
	}

	/**
     * ダイアログセットアップ
     */
    private void setupDialog(String message) {
		progressDialog = new ProgressDialog(mCtx);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);        
		progressDialog.setMessage(message);
		progressDialog.show();    
    }

    /**
     * ダイアログ閉じる
     */
    private void dialogClose() {
    	if (progressDialog != null ) {
    		if (progressDialog.isShowing()) {
           		progressDialog.dismiss();
           		progressDialog = null;
    		}
    	}    	
    }
}
