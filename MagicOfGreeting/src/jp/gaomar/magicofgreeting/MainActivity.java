package jp.gaomar.magicofgreeting;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.nobot.libAdMaker.libAdMaker;
import jp.gaomar.magicofgreeting.ProximityManager.OnProximityListener;
import jp.gaomar.magicofgreeting.ShakeListener.OnShakeListener;
import jp.gaomar.magicofgreeting.SoundSwitch.OnReachedVolumeListener;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.runnable.RunnableHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.LayoutGameActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

/*
 *  This class shows the example of applying velocity using physics.
 *  Some of the functionality was inspired by the code by Nicolas Gramlich from AndEngine(www.andengine.org).
 */
public class MainActivity extends LayoutGameActivity implements
	IOnSceneTouchListener,
	IOnAreaTouchListener {

	// ===========================================================
	// Constants
	// ===========================================================
	private final static int CAMERA_WIDTH  = 320;
	private final static int CAMERA_HEIGHT = 480;

	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);

	private PhysicsWorld mPhysicsWorld;
	private Body mGroundBody;
	private MouseJoint mMouseJointActive;

    private static final String ADMAKER_URL = "http://images.ad-maker.info/apps/t9hrrx9sv97a.html";
    private static final String ADMAKER_SITEID = "881";
    private static final String ADMAKER_ZONEID = "1835";

	// ===========================================================
	// Texture
	// ===========================================================
	private BitmapTextureAtlas mTexture, mTexture_Atlas;
	private BitmapTextureAtlas mBgTexture;
	private TextureRegion mBgTextureRegion, mBgNightTextureRegion, mParticleTextureRegion, mParticle_heartTextureRegion, mParticle_starTextureRegion;

	private TextureRegion mTx_ac, mTx_greateusagi, mTx_inu, mTx_kinglion, mTx_kita, mTx_lion, mTx_mama, mTx_manbo;
	private TextureRegion mTx_migeru, mTx_nezumi, mTx_onna, mTx_otoko, mTx_sai, mTx_sukanku, mTx_takata, mTx_unagi, mTx_usagi, mTx_wani;

	private final int mID_otoko = 0;
	private final int mID_onna = 1;
	private final int mID_inu = 2;
	private final int mID_usagi = 3;
	private final int mID_wani = 4;
	private final int mID_lion = 5;
	private final int mID_unagi = 6;
	private final int mID_mama = 7;
	private final int mID_sukanku = 8;
	private final int mID_manbo = 9;
	private final int mID_nezumi = 10;
	private final int mID_sai = 11;
	private final int mID_ac = 12;
	private final int mID_greateusagi = 13;
	private final int mID_kinglion = 14;
	private final int mID_migeru = 15;
	private final int mID_takata = 16;
	private final int mID_kita = 17;


	// ===========================================================
	// Other
	// ===========================================================
	private final static int PREF_FORM = 1;
	private final static int BARCODE_FORM = 2;
	private String m_Code = "";

	private float mGravity;
	private SoundPool sp, sp_japanet, sp_tokadho, sp_b;
    int[] seID = new int[PrefDispFlg.DISP_MAX];
    private final int JAPANET_ID = 17;
    private final int TOKADHO_ID = 18;
    private final int JAPANET_CNT = 28;
    private final int TOKADHO_CNT = 12;
    int[] seID_Japanet = new int[JAPANET_CNT];
    int[] seID_Tokadho = new int[TOKADHO_CNT];
    int[] seID_B = new int[1];

    int cnt = 0;

    private List<String> dispFlg = new ArrayList<String>();

	private ShakeListener mShakeListener;
	private ProximityManager mProximityManager;
    private boolean mShakeFlg;

	Handler mSoundHandler = new Handler();

	// マイクからの入力
	public boolean mSoundFlg;
	private SoundSwitch mSoundSwitch;

	// 近接センサー反応
	public boolean mProximityFlg;

	@Override
	public Engine onLoadEngine() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT, new FillResolutionPolicy(), camera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		return new Engine(engineOptions);
	}

	@Override
	public Scene onLoadScene() {
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		Scene scene = new Scene();
		scene.setOnSceneTouchListener(this);
		scene.setOnAreaTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, mGravity), false);
		this.mGroundBody = this.mPhysicsWorld.createBody(new BodyDef());


		// create physics box
		int size = 0;
		final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, size);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, size);
		final Shape left = new Rectangle(0, 0, size, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, size, CAMERA_HEIGHT);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		scene.attachChild(ground);
		scene.attachChild(roof);
		scene.attachChild(left);
		scene.attachChild(right);



		scene.registerUpdateHandler(this.mPhysicsWorld);
		scene.registerUpdateHandler(updateHandler);

		Sprite bgSprite = new Sprite(0, 0, mBgTextureRegion);

		if (PreferenceActivity.isFireworks(this)) {
			// 衝突判定スレッド開始
			mPhysicsWorld.setContactListener(new NewContactListener(this));
			bgSprite = new Sprite(0, 0, mBgNightTextureRegion);
		}

		scene.attachChild(bgSprite);

		if (PreferenceActivity.isSound(this)) {
			if (mSoundSwitch == null) {
		        mSoundSwitch = new SoundSwitch(this);
		        mSoundSwitch.setOnVolumeReachedListener(new OnReachedVolumeListener() {
					@Override
					public void OnReachedVolum(final short volume) {
						mSoundHandler.post(new Runnable() {
							@Override
							public void run() {
								if (volume != 0) {
									mSoundFlg = true;
								}
							}
						});
					}
				});
		        new Thread(mSoundSwitch).start();
			}
		}

		return scene;
	}

	@Override
	public void onLoadResources() {

		// 重力取得
        getGravity();

        // 出現設定取得
        getDisp();

		/* Textures. */
		this.mTexture = new BitmapTextureAtlas(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mBgTexture = new BitmapTextureAtlas(1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mTexture_Atlas = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		/* TextureRegions. */
		this.mTx_ac = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "ac.png", 0, 0);
		this.mTx_inu = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "inu.png", 50, 0);
		this.mTx_kita = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "kita.png", 100, 0);
		this.mTx_lion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "lion.png", 150, 0);
		this.mTx_mama = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "mama.png", 200, 0);
		this.mTx_manbo = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "manbo.png", 250, 0);
		this.mTx_migeru = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "migeru.png", 300, 0);
		this.mTx_nezumi = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "nezumi.png", 350, 0);
		this.mTx_onna = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "onna.png", 400, 0);
		this.mTx_otoko = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "otoko.png", 450, 0);
		this.mTx_sai = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "sai.png", 0, 100);
		this.mTx_sukanku = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "sukanku.png", 50, 100);
		this.mTx_takata = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "takata.png", 100, 100);
		this.mTx_unagi = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "unagi.png", 150, 100);
		this.mTx_usagi = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "usagi.png", 200, 100);
		this.mTx_wani = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "wani.png", 250, 100);
		this.mTx_greateusagi = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "greateusagi.png", 300, 100);
		this.mTx_kinglion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "kinglion.png", 400, 100);


		this.mParticleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture_Atlas, this, "particle.png", 0, 0);
		this.mParticle_heartTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture_Atlas, this, "particle_heart.png", 15, 0);
		this.mParticle_starTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture_Atlas, this, "particle_star.png", 30, 0);
		this.mBgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBgTexture, this, "background.png", 0, 0);
		this.mBgNightTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBgTexture, this, "background_night.png", 320, 0);


		mEngine.getTextureManager().loadTexture(mBgTexture);
		mEngine.getTextureManager().loadTexture(mTexture);
		mEngine.getTextureManager().loadTexture(mTexture_Atlas);

		//リソースファイルからSE
        sp = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0 );
        sp_japanet = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0 );
        sp_tokadho = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0 );
        sp_b = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0 );
        seID[0] = sp.load( this, R.raw.popopopoon, 1 );
        seID[1] = sp.load( this, R.raw.popopopoon, 1 );
        seID[2] = sp.load( this, R.raw.inu, 1 );
        seID[3] = sp.load( this, R.raw.usagi, 1 );
        seID[4] = sp.load( this, R.raw.wani, 1 );
        seID[5] = sp.load( this, R.raw.lion, 1 );
        seID[6] = sp.load( this, R.raw.unagi, 1 );
        seID[7] = sp.load( this, R.raw.mouse, 1 );
        seID[8] = sp.load( this, R.raw.sukanku, 1 );
        seID[9] = sp.load( this, R.raw.manbo, 1 );
        seID[10] = sp.load( this, R.raw.mouse_gochi, 1 );
        seID[11] = sp.load( this, R.raw.sai, 1 );
        seID[12] = sp.load( this, R.raw.ac, 1 );
        seID[13] = sp.load( this, R.raw.usagi, 1 );
        seID[14] = sp.load( this, R.raw.lion, 1 );
        seID[15] = sp.load( this, R.raw.migeru, 1 );

        // ジャパネットたかた用
        seID_Japanet[0] = sp_japanet.load( this, R.raw.bega, 1 );
        seID_Japanet[1] = sp_japanet.load( this, R.raw.bikkuri, 1 );
        seID_Japanet[2] = sp_japanet.load( this, R.raw.camera, 1 );
        seID_Japanet[3] = sp_japanet.load( this, R.raw.dejitaru, 1 );
        seID_Japanet[4] = sp_japanet.load( this, R.raw.dondon, 1 );
        seID_Japanet[5] = sp_japanet.load( this, R.raw.douga, 1 );
        seID_Japanet[6] = sp_japanet.load( this, R.raw.ehehe, 1 );
        seID_Japanet[7] = sp_japanet.load( this, R.raw.gorira, 1 );
        seID_Japanet[8] = sp_japanet.load( this, R.raw.hai, 1 );
        seID_Japanet[9] = sp_japanet.load( this, R.raw.internet, 1 );
        seID_Japanet[10] = sp_japanet.load( this, R.raw.jet, 1 );
        seID_Japanet[11] = sp_japanet.load( this, R.raw.jikkuri, 1 );
        seID_Japanet[12] = sp_japanet.load( this, R.raw.kanarimuri, 1 );
        seID_Japanet[13] = sp_japanet.load( this, R.raw.kasya, 1 );
        seID_Japanet[14] = sp_japanet.load( this, R.raw.kinri, 1 );
        seID_Japanet[15] = sp_japanet.load( this, R.raw.koredemo, 1 );
        seID_Japanet[16] = sp_japanet.load( this, R.raw.megapix, 1 );
        seID_Japanet[17] = sp_japanet.load( this, R.raw.ne, 1 );
        seID_Japanet[18] = sp_japanet.load( this, R.raw.renzu, 1 );
        seID_Japanet[19] = sp_japanet.load( this, R.raw.saa, 1 );
        seID_Japanet[20] = sp_japanet.load( this, R.raw.scanmedia, 1 );
        seID_Japanet[21] = sp_japanet.load( this, R.raw.sdcard, 1 );
        seID_Japanet[22] = sp_japanet.load( this, R.raw.set, 1 );
        seID_Japanet[23] = sp_japanet.load( this, R.raw.sony, 1 );
        seID_Japanet[24] = sp_japanet.load( this, R.raw.tensu, 1 );
        seID_Japanet[25] = sp_japanet.load( this, R.raw.wakatenai, 1 );
        seID_Japanet[26] = sp_japanet.load( this, R.raw.yarimasyo, 1 );
        seID_Japanet[27] = sp_japanet.load( this, R.raw.yumenoyou, 1 );

        // トーカ堂用
        seID_Tokadho[0] = sp_tokadho.load( this, R.raw.girigiri, 1 );
        seID_Tokadho[1] = sp_tokadho.load( this, R.raw.kakaku_a, 1 );
        seID_Tokadho[2] = sp_tokadho.load( this, R.raw.kakaku_b, 1 );
        seID_Tokadho[3] = sp_tokadho.load( this, R.raw.kakaku_c, 1 );
        seID_Tokadho[4] = sp_tokadho.load( this, R.raw.kazuganai, 1 );
        seID_Tokadho[5] = sp_tokadho.load( this, R.raw.kita, 1 );
        seID_Tokadho[6] = sp_tokadho.load( this, R.raw.oyasukusite, 1 );
        seID_Tokadho[7] = sp_tokadho.load( this, R.raw.set_b, 1 );
        seID_Tokadho[8] = sp_tokadho.load( this, R.raw.shinjyu, 1 );
        seID_Tokadho[9] = sp_tokadho.load( this, R.raw.syouhizei, 1 );
        seID_Tokadho[10] = sp_tokadho.load( this, R.raw.tugihaitu, 1 );
        seID_Tokadho[11] = sp_tokadho.load( this, R.raw.utidesikakaenai, 1 );

        // 花火音
        seID_B[0] = sp_b.load( this, R.raw.bon, 1);

        waitSoundSet();

	}


	private void waitSoundSet() {
		  //再生テスト
		  int streamID = 0;
		  for (int ii=0; ii<seID_Japanet.length; ii++) {
			  do {
			   //少し待ち時間を入れる
			   try {
			    Thread.sleep(10);
			   } catch (InterruptedException e) {
			   }
			   //ボリュームをゼロにして再生して戻り値をチェック
			   streamID = sp_japanet.play(seID_Japanet[ii], 0.0f, 0.0f, 1, 0, 1.0f);
			  } while(streamID == 0);
		  }

		  for (int ii=0; ii<seID_Tokadho.length; ii++) {
			  do {
			   //少し待ち時間を入れる
			   try {
			    Thread.sleep(10);
			   } catch (InterruptedException e) {
			   }
			   //ボリュームをゼロにして再生して戻り値をチェック
			   streamID = sp_tokadho.play(seID_Tokadho[ii], 0.0f, 0.0f, 1, 0, 1.0f);
			  } while(streamID == 0);
		  }

	}

	@Override
	protected void onResume() {
		super.onResume();
		mShakeListener.onResume();
        // センサーの準備などをします。
		mProximityManager.onResume();

		if (PreferenceActivity.isSound(this)) {
			if (mSoundSwitch != null) {
				mSoundSwitch.stop();
		        mSoundSwitch = new SoundSwitch(this);
		        mSoundSwitch.setOnVolumeReachedListener(new OnReachedVolumeListener() {
					@Override
					public void OnReachedVolum(final short volume) {
						mSoundHandler.post(new Runnable() {
							@Override
							public void run() {
								if (volume != 0) {
									mSoundFlg = true;
								}
							}
						});
					}
				});
		        new Thread(mSoundSwitch).start();
			}
		}

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// スリープ禁止
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // ShakeListenerのインスタンスを作って
        mShakeListener = new ShakeListener(this);
        // リスナーをセット
        mShakeListener.setOnShakeListener(new OnShakeListener() {
            // シェイクを検知すると
            // 以下のonShakeメソッドが呼び出されます。
            public void onShake() {
            	mShakeFlg = true;
            }
        });

		mProximityManager = new ProximityManager(this);
		if (PreferenceActivity.isProximity(this)) {
			mProximityManager.setOnProximityListener(new OnProximityListener() {

	            // 近接センサーの値が変わる度に呼び出されます。
	            public void onSensorChanged(SensorEvent event) {
	            }

	            // 近接センサーに近づいたら呼び出されます。
	            public void onNear(float value) {
	            	mProximityFlg = true;
	            }

	            // 近接センサーから遠ざかったら呼び出されます。
	            public void onFar(float value) {
	            	mProximityFlg = true;
	            }
	        });
		}
	}

	@Override
	protected void onPause() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();
		if (PreferenceActivity.isSound(this))
			mSoundSwitch.stop();
        mShakeListener.onPause();
        // センサーのリスナーを解放します。
        mProximityManager.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (PreferenceActivity.isSound(this))
			mSoundSwitch.stop();
	}

	/**
	 * 重力取得
	 */
	private void getGravity() {
		switch (Integer.parseInt(PreferenceActivity.getGravity(this))) {
		case 0:
			mGravity = SensorManager.GRAVITY_SUN;
			break;
		case 1:
			mGravity = SensorManager.GRAVITY_MERCURY;
			break;
		case 2:
			mGravity = SensorManager.GRAVITY_VENUS;
			break;
		case 3:
			mGravity = SensorManager.GRAVITY_EARTH;
			break;
		case 4:
			mGravity = SensorManager.GRAVITY_MOON;
			break;
		case 5:
			mGravity = SensorManager.GRAVITY_MARS;
			break;
		case 6:
			mGravity = SensorManager.GRAVITY_JUPITER;
			break;
		case 7:
			mGravity = SensorManager.GRAVITY_SATURN;
			break;
		case 8:
			mGravity = SensorManager.GRAVITY_URANUS;
			break;
		case 9:
			mGravity = SensorManager.GRAVITY_NEPTUNE;
			break;
		case 10:
			mGravity = SensorManager.GRAVITY_PLUTO;
			break;
		case 11:
			mGravity = SensorManager.GRAVITY_DEATH_STAR_I;
			break;
		}
	}

	/**
	 * 出現設定取得
	 */
	private void getDisp() {
		for (int ii=0; ii<PrefDispFlg.DISP_MAX; ii++) {
			if (PreferenceActivity.getDispFlg(this, ii+1)) {
				String flg = Integer.toString(ii);
				dispFlg.add(flg);
			}
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PREF_FORM) {
			if (resultCode == RESULT_CANCELED) {
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		} else if (requestCode == BARCODE_FORM) {
			// バーコード取得
			if (resultCode == Activity.RESULT_OK) {
	            m_Code = data.getStringExtra("SCAN_RESULT");

			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuItem menu1 = menu.add(
    			1,
    			1,
    			Menu.NONE,
    			R.string.menu_reset);
    	menu1.setIcon(android.R.drawable.ic_menu_delete);

    	MenuItem menu2 = menu.add(
    			1,
    			2,
    			Menu.NONE,
    			R.string.menu_barcode);
    	menu2.setIcon(R.drawable.ic_menu_barcode);

    	MenuItem menu3 = menu.add(
    			1,
    			3,
    			Menu.NONE,
    			R.string.menu_setting);
    	menu3.setIcon(android.R.drawable.ic_menu_preferences);

    	return true;
	}



	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_FOCUS) {
			exeBarcode();
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		super.onOptionsItemSelected(menuItem);
		Intent intent = new Intent();
		switch(menuItem.getItemId()){
			case 1 :
	    		intent = new Intent(this, MainActivity.class);
	    		startActivity(intent);
	    		finish();
				break;
			case 2 :
				exeBarcode();
				break;
			case 3 :
	    		intent = new Intent(this, PreferenceActivity.class);
	    		startActivityForResult(intent, PREF_FORM);
				break;
		}
		return true;
	}

	private void exeBarcode() {
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		try {
			startActivityForResult(intent, BARCODE_FORM);
		} catch (ActivityNotFoundException e) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setTitle(getString(R.string.dialog_barcode_title));
			alertDialogBuilder.setMessage(getString(R.string.dialog_barcode_message));
			alertDialogBuilder.setPositiveButton("インストール", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Uri uri=Uri.parse("market://details?id=com.google.zxing.client.android");
					Intent intent=new Intent(Intent.ACTION_VIEW,uri);
					startActivity(intent);
				}
			});

			alertDialogBuilder.setNegativeButton("キャンセル", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});

			alertDialogBuilder.setCancelable(true);
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}

	}

	@Override
	protected void onDestroy() {
		try {
			for (int ii=0; ii < seID.length; ii++) {
				sp.stop(seID[ii]);
				sp.unload(seID[ii]);
			}
			for (int ii=0; ii < seID_Japanet.length; ii++) {
				sp_japanet.stop(seID_Japanet[ii]);
				sp_japanet.unload(seID_Japanet[ii]);
			}
			for (int ii=0; ii < seID_Tokadho.length; ii++) {
				sp_tokadho.stop(seID_Tokadho[ii]);
				sp_tokadho.unload(seID_Tokadho[ii]);
			}

		} finally {
			sp.release();
			sp_japanet.release();
			sp_tokadho.release();
			super.onDestroy();
			finish();
		}
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			switch(pSceneTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					int no = (int)(Math.random()*dispFlg.size());
					int id = Integer.valueOf(dispFlg.get(no));
					float speed = PreferenceActivity.getSoundSpeed(MainActivity.this);
					if (id == JAPANET_ID - 1) {
						int japanet_id = (int)(Math.random()*JAPANET_CNT);
						sp_japanet.play(seID_Japanet[japanet_id], 1.0F, 1.0F, 0, 0, speed);
					} else if (id == TOKADHO_ID - 1){
						int tokadho_id = (int)(Math.random()*TOKADHO_CNT);
						sp_tokadho.play(seID_Tokadho[tokadho_id], 1.0F, 1.0F, 0, 0, speed);
					} else {
						sp.play(seID[id], 1.0F, 1.0F, 0, 0, speed);
					}

					this.addFace(id,  (int)pSceneTouchEvent.getX(), (int)pSceneTouchEvent.getY());
					break;
				case TouchEvent.ACTION_MOVE:
					if(this.mMouseJointActive != null) {
						final Vector2 vec = Vector2Pool.obtain(pSceneTouchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, pSceneTouchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
						this.mMouseJointActive.setTarget(vec);
						Vector2Pool.recycle(vec);
					}
					return true;
				case TouchEvent.ACTION_UP:
					if(this.mMouseJointActive != null) {
						this.mPhysicsWorld.destroyJoint(this.mMouseJointActive);
						this.mMouseJointActive = null;
						removeJointFace();
					}
					return true;

			}
		}

		return false;
	}

	private void addFace(int id, int pX, int pY) {
		final Scene scene = this.mEngine.getScene();
		final Body body;

		Sprite face = null;
		switch (id) {
		default:

			break;
		case mID_ac:
			face = new Sprite(pX, pY, this.mTx_ac);
			break;
		case mID_greateusagi:
			face = new Sprite(pX, pY, this.mTx_greateusagi);
			break;
		case mID_inu:
			face = new Sprite(pX, pY, this.mTx_inu);
			break;
		case mID_kinglion:
			face = new Sprite(pX, pY, this.mTx_kinglion);
			break;
		case mID_kita:
			face = new Sprite(pX, pY, this.mTx_kita);
			break;
		case mID_lion:
			face = new Sprite(pX, pY, this.mTx_lion);
			break;
		case mID_mama:
			face = new Sprite(pX, pY, this.mTx_mama);
			break;
		case mID_manbo:
			face = new Sprite(pX, pY, this.mTx_manbo);
			break;
		case mID_migeru:
			face = new Sprite(pX, pY, this.mTx_migeru);
			break;
		case mID_nezumi:
			face = new Sprite(pX, pY, this.mTx_nezumi);
			break;
		case mID_onna:
			face = new Sprite(pX, pY, this.mTx_onna);
			break;
		case mID_otoko:
			face = new Sprite(pX, pY, this.mTx_otoko);
			break;
		case mID_sai:
			face = new Sprite(pX, pY, this.mTx_sai);
			break;
		case mID_sukanku:
			face = new Sprite(pX, pY, this.mTx_sukanku);
			break;
		case mID_takata:
			face = new Sprite(pX, pY, this.mTx_takata);
			break;
		case mID_unagi:
			face = new Sprite(pX, pY, this.mTx_unagi);
			break;
		case mID_usagi:
			face = new Sprite(pX, pY, this.mTx_usagi);
			break;
		case mID_wani:
			face = new Sprite(pX, pY, this.mTx_wani);
			break;
		}
		body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face, BodyType.DynamicBody, FIXTURE_DEF);
		body.setUserData(new BodyInfo(getString(R.string.pg_char), id, face));

		scene.registerTouchArea(face);

		scene.attachChild(face);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));

	}

	private IUpdateHandler updateHandler = new IUpdateHandler() {

		@Override
		public void onUpdate(float pSecondsElapsed) {
			if (m_Code.length() > 0) {
				int code = 0;
				int code_9 = 0;

				try {
					switch (Integer.valueOf(m_Code.substring(m_Code.length() -5, m_Code.length() - 4))) {
					case 1:
					case 3:
					case 5:
					case 7:
					case 9:
						code_9 = 1;
						break;
					}
					int code_11 = Integer.valueOf(m_Code.substring(m_Code.length() - 3, m_Code.length() - 2));
					code = code_9*10 + code_11;
					if (code >= 13) {
						code = code_11;
					}

				} catch (NumberFormatException e) {
				}
				float speed = PreferenceActivity.getSoundSpeed(MainActivity.this);
				sp.play(seID[code], 1.0F, 1.0F, 0, 0, speed);
				addFace(code, CAMERA_WIDTH / 2, 0);
				m_Code = "";
			}

			// シェイクで降ってくる
			if (mShakeFlg) {
				mShakeFlg = false;

				setUpdate();
			}

			// 音感センサー
			if (mSoundFlg) {
				mSoundFlg = false;
				setUpdate();
			}

			// 近接センサー
			if (mProximityFlg) {
				mProximityFlg = false;
				setUpdate();
			}

			// 花火エフェクト
			if (PreferenceActivity.isFireworks(MainActivity.this)) {
				try {
					Iterator<Body>bodyIte = mPhysicsWorld.getBodies();
					while( bodyIte.hasNext() ) {
					    Body buf = bodyIte.next();
					    Object obj = buf.getUserData();
					    if ( (BodyInfo)obj instanceof BodyInfo ) {
					        String name = ((BodyInfo)obj).getName();
					        if (name.equals(getString(R.string.pg_char))
					        		&& !((BodyInfo)obj).getAliveFlag()) {
					        	Shape bufShape = ((BodyInfo)obj).getShape();
					        	int id = (int)(Math.random()*3);
					        	startFireworksExplosion(id, bufShape.getX() + bufShape.getWidth()/2, bufShape.getY() + bufShape.getHeight()/2, 50, 110.0, 80.0, 75);
					        	removeFace(bufShape);
					        }
					    }
					}
				} catch (Exception e) {
				}
			}
		}

		@Override
		public void reset() {
			// TODO 自動生成されたメソッド・スタブ

		}

	};


	private void setUpdate() {
		try {

			int no = (int)(Math.random()*dispFlg.size());
			int id = Integer.valueOf(dispFlg.get(no));
			float speed = PreferenceActivity.getSoundSpeed(MainActivity.this);
			if (id == JAPANET_ID - 1) {
				int japanet_id = (int)(Math.random()*JAPANET_CNT);
				sp_japanet.play(seID_Japanet[japanet_id], 1.0F, 1.0F, 0, 0, speed);
			} else if (id == TOKADHO_ID - 1) {
				int tokadho_id = (int)(Math.random()*TOKADHO_CNT);
				sp_tokadho.play(seID_Tokadho[tokadho_id], 1.0F, 1.0F, 0, 0, speed);
			} else {
				sp.play(seID[id], 1.0F, 1.0F, 0, 0, speed);
			}
			// every event in the world must be handled by the update thread.
			addFace(id, CAMERA_WIDTH / 2, 0);

		} catch (NumberFormatException e) {
		} catch (Exception e) {
		}

	}

	@Override
	public void onLoadComplete() {
		// every event in the world must be handled by the update thread.
		int no = (int)(Math.random()*dispFlg.size());
		int id = Integer.valueOf(dispFlg.get(no));
		addFace(id, CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2);

		setAdView();
	}


	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {

		if(pSceneTouchEvent.isActionDown()) {
			final IShape face = (IShape) pTouchArea;

			// タッチの挙動
			if (Integer.parseInt(PreferenceActivity.getTouch(this)) == 0) {
				// 跳ねる
				jumpFace(face);
			} else {
				// つかむ
				if(this.mMouseJointActive == null) {
					this.mMouseJointActive = this.createMouseJoint(face, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			}
			return true;
		}
		return false;

	}

	/**
	 * ジャンプさせる
	 * @param face
	 */
	private void jumpFace(final IShape face) {
		final Scene scene = this.mEngine.getScene();
		final PhysicsConnector facePhysicsConnector = this.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(face);
		Body body = facePhysicsConnector.getBody();

		Object obj = body.getUserData();

		int pop = PreferenceActivity.getPopValue(MainActivity.this);

		float speed = PreferenceActivity.getSoundSpeed(MainActivity.this);

	    if ( (BodyInfo)obj instanceof BodyInfo ) {
	        int id = ((BodyInfo)obj).getId();

			if (id == JAPANET_ID - 1) {
				int japanet_id = (int)(Math.random()*JAPANET_CNT);
				sp_japanet.play(seID_Japanet[japanet_id], 1.0F, 1.0F, 0, 0, speed);
			} else if (id == TOKADHO_ID - 1) {
				int tokadho_id = (int)(Math.random()*TOKADHO_CNT);
				sp_tokadho.play(seID_Tokadho[tokadho_id], 1.0F, 1.0F, 0, 0, speed);
			} else {
				sp.play(seID[id], 1.0F, 1.0F, 0, 0, speed);
			}

			final Vector2 velocity = Vector2Pool.obtain(0, this.mGravity * -pop);
			body.setLinearVelocity(velocity);
			Vector2Pool.recycle(velocity);
	    }
	}

	/**
	 * 花火打ち上げ
	 * @param scene
	 * @param fCoordX
	 * @param fCoordY
	 * @param iParticleCount
	 * @param dInitVel
	 * @param dSpreading
	 * @param iDecelerationPercent
	 */
    private void startFireworksExplosion(int id, float fCoordX, float fCoordY, int iParticleCount, double dInitVel, double dSpreading, int iDecelerationPercent)
    {

    	TextureRegion texture = null;
    	switch (id) {
    	case 0:
    		texture = mParticleTextureRegion.clone();
    		break;
    	case 1:
    		texture = mParticle_heartTextureRegion.clone();
    		break;
    	case 2:
    		texture = mParticle_starTextureRegion.clone();
    		break;

    	}
		final CircleOutlineParticleEmitter particleEmitter = new CircleOutlineParticleEmitter(CAMERA_WIDTH * 0.5f, CAMERA_HEIGHT * 0.5f , 0, 0);
		particleEmitter.setCenter(fCoordX, fCoordY);

        final Fireworks fword = new Fireworks(particleEmitter, texture, 100, CAMERA_HEIGHT, CAMERA_WIDTH);
        //fwPF.start();
        sp_b.play(seID_B[0], 1.0F, 1.0F, 0, 0, 1);
        getEngine().getScene().attachChild(fword);
        this.getEngine().registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
    	    @Override
    	    public void onTimePassed(final TimerHandler pTimerHandler) {
    		final RunnableHandler runnableRemoveHandler = new RunnableHandler();
    		getEngine().getScene().registerUpdateHandler(runnableRemoveHandler);

    		runnableRemoveHandler.postRunnable(new Runnable() {
    		    @Override
    		    public void run() {
    		    	getEngine().getScene().detachChild(fword);
    		    }
    		});
    	    }
    	}));
    }

	private void removeJointFace() {
		Iterator<Body>bodyIte = mPhysicsWorld.getBodies();
		try {

			while( bodyIte.hasNext() ) {
			    Body buf = bodyIte.next();
			    Object obj = buf.getUserData();
			    if ( (BodyInfo)obj instanceof BodyInfo ) {
			        String name = ((BodyInfo)obj).getName();
			        if ( name.equals(getString(R.string.pg_char)) &&
			        		((BodyInfo)obj).getJointFlag()) {
			        	((BodyInfo)obj).setJointFlag( false );
			        }
			    }
			}
		} catch (Exception e) {

		}
	}

	public MouseJoint createMouseJoint(final IShape pFace, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		//final Body body = (Body) pFace.getUserData();
		final PhysicsConnector facePhysicsConnector = this.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(pFace);

		final Body body = facePhysicsConnector.getBody();

		final MouseJointDef mouseJointDef = new MouseJointDef();

		final Vector2 localPoint = Vector2Pool.obtain((pTouchAreaLocalX - pFace.getWidth() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (pTouchAreaLocalY - pFace.getHeight() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		this.mGroundBody.setTransform(localPoint, 0);

		((BodyInfo)body.getUserData()).setJointFlag(true);

		mouseJointDef.bodyA = this.mGroundBody;
		mouseJointDef.bodyB = body;
		mouseJointDef.dampingRatio = 0.95f;
		mouseJointDef.frequencyHz = 30;
		mouseJointDef.maxForce = (200.0f * body.getMass());
		mouseJointDef.collideConnected = true;

		mouseJointDef.target.set(body.getWorldPoint(localPoint));
		Vector2Pool.recycle(localPoint);

        int id = ((BodyInfo)body.getUserData()).getId();
		float speed = PreferenceActivity.getSoundSpeed(MainActivity.this);

		if (id == JAPANET_ID - 1) {
			int japanet_id = (int)(Math.random()*JAPANET_CNT);
			sp_japanet.play(seID_Japanet[japanet_id], 1.0F, 1.0F, 0, 0, speed);
		} else if (id == TOKADHO_ID - 1) {
			int tokadho_id = (int)(Math.random()*TOKADHO_CNT);
			sp_tokadho.play(seID_Tokadho[tokadho_id], 1.0F, 1.0F, 0, 0, speed);
		} else {
			sp.play(seID[id], 1.0F, 1.0F, 0, 0, speed);
		}

		return (MouseJoint) this.mPhysicsWorld.createJoint(mouseJointDef);
	}

	private void removeFace(final Shape face) {
		final Scene scene = this.mEngine.getScene();

		final PhysicsConnector facePhysicsConnector = this.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(face);

		this.mPhysicsWorld.unregisterPhysicsConnector(facePhysicsConnector);
		this.mPhysicsWorld.destroyBody(facePhysicsConnector.getBody());

		scene.unregisterTouchArea(face);
		scene.detachChild(face);
	}

	@Override
	protected int getLayoutID() {
		// TODO 自動生成されたメソッド・スタブ
		return R.layout.main;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		// TODO 自動生成されたメソッド・スタブ
		return R.id.xmllayoutexample_rendersurfaceview;
	}

	private void setAdView() {
        libAdMaker ad = (libAdMaker)findViewById(R.id.admakerview);
        ad.setActivity(MainActivity.this);
        ad.siteId = ADMAKER_SITEID;
        ad.zoneId = ADMAKER_ZONEID;
        ad.setUrl(ADMAKER_URL);
        ad.start();

	}
}
