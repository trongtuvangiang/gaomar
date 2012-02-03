package jp.gaomar.astronumbertouch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import jp.gaomar.astronumbertouch.db.DBCommon;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.runnable.RunnableHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.IEntityModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.LayoutGameActivity;
import org.anddev.andengine.util.modifier.IModifier;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

public class GameActivity extends LayoutGameActivity implements
	IOnMenuItemClickListener
{
	// ===========================================================
	// Constants
	// ===========================================================	
	public static final int WIDTH_AREA = 320;
	public static final int HEIGHT_AREA = 480;

	/** 設置レイヤー*/
	public static final int LAYER_COUNT = 4;
	public static final int LAYER_BACK = 0;
	public static final int LAYER_ASTRO_BACK = LAYER_BACK + 1;
	public static final int LAYER_ASTRO = LAYER_ASTRO_BACK + 1;
	public static final int LAYER_TOP = LAYER_ASTRO + 1;

	/** メニュー*/
	protected static final int MENU_RETRY = 0;
	protected static final int MENU_TWITTER = MENU_RETRY + 1;
	protected static final int MENU_QUIT = MENU_TWITTER + 1;

	/** 設置座標*/
	private List<Position> mPosList = new ArrayList<Position>();

	/** ゲームモード*/
	private int mMode;
	
	/** 次のNo*/
	private int mNextNo = 0;
	
	/** 開始タイム*/
	private long mStartTime;

	/** タイム*/
	private long mTime;
	
	/** ベストタイム*/
	private long mBestTime;
	
	/** タイマーフォーマット*/
	private final SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS");
	
	/** 広告*/
	private LinearLayout mAd_Top, mAd_Bottom;
	
	/** ゲームオーバーフラグ*/
	private boolean mGameOver;
	
	// ===========================================================
	// Fields
	// ===========================================================
	protected Scene mMainScene;
	protected MenuScene mMenuScene;

	// ===========================================================
	// Texture
	// ===========================================================
	private BitmapTextureAtlas mAstroAtlas, mNoAtlas, mBackAtlas, mMenuAtlas;	
	private BitmapTextureAtlas mFontAtlas;
	private TiledTextureRegion mAstroOnTex, mAstroOffTex, mNoTex;
	private TextureRegion mBackTex, mQuitTex, mTwitterTex, mRetryTex;

	private Font mFont;
	private ChangeableText mTimerTxt, mBestTimerTxt;
	private Text mNewTxt;
	
	@Override
	public void onLoadComplete() {
		// ゲーム開始
		gameStart();
	}

	@Override
	public Engine onLoadEngine() {
		final Camera camera = new Camera(0, 0, WIDTH_AREA, HEIGHT_AREA);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT, new FillResolutionPolicy(), camera).setNeedsSound(true);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {
		// ゲームモード取得
		mMode = getIntent().getIntExtra("mode", 0);
		
		this.mAstroAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mNoAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuAtlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mBackAtlas = new BitmapTextureAtlas(32, 32, TextureOptions.REPEATING_NEAREST_PREMULTIPLYALPHA);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		// texture
		this.mAstroOnTex = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mAstroAtlas, this, "on.png", 0, 0, 5, 5);
		this.mAstroOffTex = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mAstroAtlas, this, "off.png", 0, 250, 5, 5);
		this.mNoTex = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mNoAtlas, this, "no.png", 0, 0, 5, 5);
		this.mBackTex = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackAtlas, this, "back.png", 0, 0);
		this.mBackTex.setWidth(WIDTH_AREA); this.mBackTex.setHeight(HEIGHT_AREA);
		this.mRetryTex = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuAtlas, this, "retry.png", 0, 0);
		this.mTwitterTex = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuAtlas, this, "twitter.png", 0, 50);
		this.mQuitTex = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuAtlas, this, "quit.png", 0, 150);

		// font
		this.mFontAtlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.mFontAtlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 14, true, Color.WHITE);
		this.mEngine.getFontManager().loadFonts(mFont);


		this.mEngine.getTextureManager().loadTextures(mAstroAtlas, mFontAtlas, mNoAtlas, mBackAtlas, mMenuAtlas);

		for (int ii = 0; ii < 5; ii++) {			
			for (int jj = 0; jj < 5; jj++) {
				int pX = jj * 60 + 10;
				int pY = ii * 60 + 117;
				Position pos = new Position(pX, pY);
				mPosList.add(pos);				
			}			
		}
		
        // 広告をランダムに表示する
		mAd_Top = (LinearLayout) findViewById(R.id.ads_parent_top);		
		mAd_Bottom = (LinearLayout) findViewById(R.id.ads_parent_bottom);
		mAd_Top.setVisibility(View.INVISIBLE);
		mAd_Bottom.setVisibility(View.INVISIBLE);
		GameActivity.this.runOnUiThread(new Runnable() {			
			@Override
			public void run() {
		        new Ads(GameActivity.this, mAd_Top).setAdsDisp(0); 
		        new Ads(GameActivity.this, mAd_Bottom).setAdsDisp(1);				
			}
		});
	}

	@Override
	public Scene onLoadScene() {
		mMainScene = new Scene();

		for(int i = 0; i < LAYER_COUNT; i++) {
			mMainScene.attachChild(new Entity());
		}

		/** ゲーム初期化*/
		initGame();
		
		return mMainScene;
	}

	/**
	 * ゲーム初期化
	 */
	private void initGame() {
		try {
			// ナンバープレートの配置
			for (int ii = 0; ii < 25; ii++) {
				while (true) {
					final Position pos = mPosList.get((int)(Math.random() * mPosList.size()));
					if (pos.getNo() == -1) {
						pos.setNo(ii);
						final AnimatedSprite astro_back = new AnimatedSprite(pos.getX(), pos.getY(), mAstroOffTex.deepCopy());
						final AnimatedSprite astro = new AnimatedSprite(pos.getX(), pos.getY(), mAstroOnTex.deepCopy()) {

							@Override
							public boolean onAreaTouched(
									TouchEvent pSceneTouchEvent,
									float pTouchAreaLocalX, float pTouchAreaLocalY) {
								if (pSceneTouchEvent.isActionDown() && mStartTime != 0) {
									if ((Integer)this.getUserData() == mNextNo) {
										final AnimatedSprite face = this;
										final IEntityModifier modifier = new ParallelEntityModifier (new RotationModifier(0.2f, 0f, 360f, new IEntityModifierListener() {			
											@Override
											public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {				
											}
											
											@Override
											public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
									    		final RunnableHandler runnableRemoveHandler = new RunnableHandler();
									    		mMainScene.registerUpdateHandler(runnableRemoveHandler);

									    		runnableRemoveHandler.postRunnable(new Runnable() {
									    		    @Override
									    		    public void run() {
									    		    	mMainScene.getChild(LAYER_ASTRO).detachChild(face);
									    		    	if (mNextNo > 24) {
									    		    		if (mTime < mBestTime) {
									    		    			mNewTxt.setVisible(true);
									    		    			mBestTimerTxt.setText("Best Record " + sdf.format(mTime));
									    		    			DBCommon.updateScore(GameActivity.this, mMode, mTime);
									    		    		}									    					
															/* Attach the menu. */
									    		    		mGameOver = true;
															mMainScene.setChildScene(mMenuScene, false, true, true);
									    		    	}
									    		    }
									    		});
											}
										}));
										this.registerEntityModifier(modifier);
										mNextNo++;
										if (mNextNo > 24) {
											mMainScene.unregisterUpdateHandler(gameTimer);
										}
										return true;
									}
								}
								return false;
							}
							
						};					
						astro.setCurrentTileIndex(ii); astro_back.setCurrentTileIndex(ii);					
						astro.setUserData(ii);
						mMainScene.registerTouchArea(astro);
						mMainScene.getChild(LAYER_ASTRO_BACK).attachChild(astro_back);
						mMainScene.getChild(LAYER_ASTRO).attachChild(astro);
						
						
						break;
					}
					boolean flg = true;
					for (Position po : mPosList) {
						if (po.getNo() == -1) {
							flg = false;
							break;
						}
					}
					if (flg) break;
				}
				
			}
			// 背景設置
			final Sprite back = new Sprite(0, 0, this.mBackTex.getWidth(), this.mBackTex.getHeight(), this.mBackTex);
			mMainScene.getChild(LAYER_BACK).attachChild(back);

			// タイマー設置
			mTimerTxt = new ChangeableText(-WIDTH_AREA, -HEIGHT_AREA, mFont, "00:00 000");
			mTimerTxt.setColor(0, 0, 0);
			mTimerTxt.setPosition(WIDTH_AREA - mTimerTxt.getWidth() - 15, 100);
			mMainScene.getChild(LAYER_BACK).attachChild(mTimerTxt);

			// ベストタイム設置
			mBestTime = DBCommon.getScore(this, mMode);
			mBestTimerTxt = new ChangeableText(-WIDTH_AREA, -HEIGHT_AREA, mFont, "Best Record " + sdf.format(mBestTime));
			mBestTimerTxt.setPosition(WIDTH_AREA - mBestTimerTxt.getWidth() - 20, mTimerTxt.getY() - mBestTimerTxt.getHeight() - 10);
			mBestTimerTxt.setColor(0, 0, 0);
			mMainScene.getChild(LAYER_BACK).attachChild(mBestTimerTxt);

			mNewTxt = new Text(-WIDTH_AREA, -HEIGHT_AREA, mFont, "New Record!");
			mNewTxt.setColor(1, 0, 0);
			mNewTxt.setPosition(mTimerTxt.getX() - mNewTxt.getWidth() - 20, 100);
			mNewTxt.setVisible(false);
			mMainScene.getChild(LAYER_BACK).attachChild(mNewTxt);

			// ヒント設置
			final Text hint = new Text(10, 100, mFont, "Next:");
			hint.setColor(0, 0, 0);
			mMainScene.getChild(LAYER_BACK).attachChild(hint);
			
			AnimatedSprite hintNo = new AnimatedSprite(-WIDTH_AREA, -HEIGHT_AREA, mNoTex) {

				@Override
				protected void onManagedUpdate(float pSecondsElapsed) {
					super.onManagedUpdate(pSecondsElapsed);
					if (mNextNo < 25) {
						this.setCurrentTileIndex(mNextNo);
					} else {						
						mMainScene.getChild(LAYER_BACK).detachChild(hint);
						mMainScene.getChild(LAYER_BACK).detachChild(this);						
					}
				}				
			};
			hintNo.setScale(0.35f);
			hintNo.setPosition(hint.getX() - 40, hint.getY() - 90);
			mMainScene.getChild(LAYER_BACK).attachChild(hintNo);
			
			// メニュー初期化
			createMenuScene();
			
		} catch (Exception e) {
		}
	}
	
	/**
	 * ゲームタイマー
	 */
	private TimerHandler gameTimer = new TimerHandler(0.01f, true, new ITimerCallback() {		
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			long time =System.currentTimeMillis();
			mTime = time - mStartTime;
			mTimerTxt.setText(sdf.format(mTime));			
		}
	});

	/**
	 * メニュー作成
	 */
	protected void createMenuScene() {		
		this.mMenuScene = new MenuScene(this.getEngine().getCamera());

		final SpriteMenuItem retryMenuItem = new SpriteMenuItem(MENU_RETRY, mRetryTex);
		retryMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mMenuScene.addMenuItem(retryMenuItem);

		final SpriteMenuItem twMenuItem = new SpriteMenuItem(MENU_TWITTER, this.mTwitterTex);
		twMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mMenuScene.addMenuItem(twMenuItem);

		final SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, this.mQuitTex);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mMenuScene.addMenuItem(quitMenuItem);

		this.mMenuScene.buildAnimations();

		this.mMenuScene.setBackgroundEnabled(false);

		this.mMenuScene.setOnMenuItemClickListener(this);
	}
	
	@Override
	protected int getLayoutID() {
		return R.layout.game;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.xmllayoutexample_rendersurfaceview;
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		Intent intent;
		switch(pMenuItem.getID()) {
		case MENU_RETRY:
			intent = new Intent(this, GameActivity.class);
			intent.putExtra("mode", mMode);
			startActivity(intent);
			finish();
			return true;
		case MENU_TWITTER:
			try {
				intent = new Intent();							
				intent.setAction(Intent.ACTION_SEND);
				intent.setType("text/plain");											
				intent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.format_twitter), mTimerTxt.getText()));
				startActivity(intent);
			} catch (Exception e) {
			}										
			return true;
		case MENU_QUIT:
			this.finish();
			return true;
		default:
			return false;
		}
	}

	private void gameStart() {
		mNextNo = 2;
		
		final Sprite back = new Sprite(0, 0, this.mBackTex.getWidth(), this.mBackTex.getHeight(), this.mBackTex);
		mMainScene.attachChild(back);
		
		final AnimatedSprite countDown = new AnimatedSprite(-WIDTH_AREA, -HEIGHT_AREA, mNoTex);
		countDown.setScale(1.5f);
		countDown.setCurrentTileIndex(mNextNo);
		countDown.setPosition((WIDTH_AREA - countDown.getBaseWidth()) / 2, (HEIGHT_AREA - countDown.getBaseHeight()) / 2);
		mMainScene.attachChild(countDown);
		
		
		final TimerHandler startHandler = new TimerHandler(1f, true, new ITimerCallback() {			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if (mNextNo > 0) {
					countDown.setCurrentTileIndex(mNextNo);
					mNextNo--;
				} else {
					mMainScene.detachChild(back);
					mMainScene.detachChild(countDown);

					GameActivity.this.runOnUiThread(new Runnable() {						
						@Override
						public void run() {
							mAd_Top.setVisibility(View.VISIBLE);
							mAd_Bottom.setVisibility(View.VISIBLE);							
						}
					});
					
					mMainScene.unregisterUpdateHandler(pTimerHandler);
					mStartTime = System.currentTimeMillis();
					mMainScene.registerUpdateHandler(gameTimer);
					
				}
			}
		});
		
		mMainScene.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() {			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mMainScene.registerUpdateHandler(startHandler);				
			}
		}));
		

	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN && !mGameOver) {
			GameActivity.this.runOnUiThread(new Runnable() {				
				@Override
				public void run() {
					// TODO 自動生成されたメソッド・スタブ
					if(mMainScene.hasChildScene()) {
						/* Remove the menu and reset it. */
						mMenuScene.back();

					} else {
						/* Attach the menu. */
						mMainScene.setChildScene(mMenuScene, false, true, true);
					}					
				}
			});
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

}