package jp.gaomar.magicofgreeting;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.e3roid.E3Activity;
import com.e3roid.E3Engine;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Background;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.texture.AssetTexture;
import com.e3roid.drawable.texture.TiledTexture;
import com.e3roid.event.SceneUpdateListener;
import com.e3roid.physics.PhysicsShape;
import com.e3roid.physics.PhysicsWorld;
import com.e3roid.util.MathUtil;

/*
 *  This class shows the example of applying velocity using physics.
 *  Some of the functionality was inspired by the code by Nicolas Gramlich from AndEngine(www.andengine.org).
 */
public class MainActivity extends E3Activity implements SceneUpdateListener {

	private final static int WIDTH  = 320;
	private final static int HEIGHT = 480;

	private final static int PREF_FORM = 1;
	private final static int BARCODE_FORM = 2;
	private String m_Code = "";

	private PhysicsWorld world;
	private float mGravity;
	private SoundPool sp;
    int[] seID = new int[15];
    int MAX = 0;
    int cnt = 0;

	@Override
	public E3Engine onLoadEngine() {
		E3Engine engine = new E3Engine(this, WIDTH, HEIGHT);
		engine.requestFullScreen();
		engine.requestPortrait();
		return engine;
	}

	@Override
	public E3Scene onLoadScene() {
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		E3Scene scene = new E3Scene();

		scene.addEventListener(this);
		scene.registerUpdateListener(60, this);
		scene.registerUpdateListener(60, world);

		// create physics box
		int size = 2;
		Shape ground = new Shape(0, getHeight() - size, getWidth(), size);
		Shape roof   = new Shape(0, 0, getWidth(), size);
		Shape left   = new Shape(0, 0, size, getHeight());
		Shape right  = new Shape(getWidth() - size, 0, size, getHeight());

		final FixtureDef wallFixtureDef = createFixtureDef(0.0f, 0.0f, 0.5f);
		createBoxBody(this.world, ground, BodyType.StaticBody, wallFixtureDef);
		createBoxBody(this.world, roof, BodyType.StaticBody, wallFixtureDef);
		createBoxBody(this.world, left, BodyType.StaticBody, wallFixtureDef);
		createBoxBody(this.world, right, BodyType.StaticBody, wallFixtureDef);

		scene.getTopLayer().add(ground);
		scene.getTopLayer().add(roof);
		scene.getTopLayer().add(left);
		scene.getTopLayer().add(right);

		// every event in the world must be handled by the update thread.
		postUpdate(new AddShapeImpl(scene, getWidth() / 2, getHeight() / 2, 0));

		scene.setBackgroundColor(0.94f, 1.00f, 0.94f, 1);

		Background background = new Background(
				new TiledTexture("background.png", getWidth(), getHeight(), this));
		scene.getTopLayer().setBackground(background);

		return scene;
	}

	@Override
	public void onLoadResources() {
        // 重力取得
        getGravity();

        // 出現設定取得
        getDisp();

		world = new PhysicsWorld(new Vector2(0, mGravity), false);
        //リソースファイルからSE
        sp = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0 );
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
		if (PreferenceActivity.isDisp(this)) {
			MAX = 15;
		} else {
			MAX = 13;
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PREF_FORM) {
			if (resultCode == RESULT_CANCELED) {
				Intent intent = new Intent(this, LayoutActivity.class);
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
	    		intent = new Intent(this, LayoutActivity.class);
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
		} finally {
			sp.release();
			super.onDestroy();
			finish();
		}
	}

	@Override
	public boolean onSceneTouchEvent(final E3Scene scene, final MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			int x = getTouchEventX(scene, motionEvent);
			int y = getTouchEventY(scene, motionEvent);
			if (scene.findDrawableAt(x, y) == null) {
				int id = (int)(Math.random()*MAX);
				float speed = PreferenceActivity.getSoundSpeed(MainActivity.this);
				sp.play(seID[id], 1.0F, 1.0F, 0, 0, speed);
				// every event in the world must be handled by the update thread.
				postUpdate(new AddShapeImpl(scene, x, y, id));
			}
		}
		return false;
	}

	private Sprite newSprite(int x, int y, int id) {
		AssetTexture texture = new AssetTexture("otoko.png", this);
		switch (id) {
		case 0:
			texture = new AssetTexture("otoko.png", this);
			break;
		case 1:
			texture = new AssetTexture("onna.png", this);
			break;
		case 2:
			texture = new AssetTexture("inu.png", this);
			break;
		case 3:
			texture = new AssetTexture("usagi.png", this);
			break;
		case 4:
			texture = new AssetTexture("wani.png", this);
			break;
		case 5:
			texture = new AssetTexture("lion.png", this);
			break;
		case 6:
			texture = new AssetTexture("unagi.png", this);
			break;
		case 7:
			texture = new AssetTexture("mama.png", this);
			break;
		case 8:
			texture = new AssetTexture("sukanku.png", this);
			break;
		case 9:
			texture = new AssetTexture("manbo.png", this);
			break;
		case 10:
			texture = new AssetTexture("nezumi.png", this);
			break;
		case 11:
			texture = new AssetTexture("sai.png", this);
			break;
		case 12:
			texture = new AssetTexture("ac.png", this);
			break;
		case 13:
			texture = new AssetTexture("greateusagi.png", this);
			break;
		case 14:
			texture = new AssetTexture("kinglion.png", this);
			break;


		}
		return new Sprite(texture, x, y) {
			@Override
			public boolean onTouchEvent(final E3Scene scene, final Shape shape,
					MotionEvent motionEvent, int localX, int localY) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					// every event in the world must be handled by the update thread.
					postUpdate(new Runnable() {
						@Override
						public void run() {
							PhysicsShape pShape = world.findShape(shape);
							pShape.getBody().setLinearVelocity(new Vector2(0, -mGravity));
							int id = Integer.valueOf(pShape.getBody().getUserData().toString());
							float speed = PreferenceActivity.getSoundSpeed(MainActivity.this);
							sp.play(seID[id], 1.0F, 1.0F, 0, 0, speed);
						}
					});
					return true;
				}
				return false;
			}
		};
	}

	class AddShapeImpl implements Runnable {
		private final E3Scene scene;
		private final int x;
		private final int y;
		private final int id;
		AddShapeImpl(E3Scene scene, int x, int y, int _id) {
			this.scene = scene;
			this.x = x;
			this.y = y;
			this.id = _id;
		}
		@Override
		public void run() {
			FixtureDef objectFixtureDef = createFixtureDef(1.0f, 0.0f, 0.5f);

			Sprite sprite = newSprite(x, y, id);

			Body body = createBoxBody(
					world, sprite, BodyType.DynamicBody, objectFixtureDef);
			PhysicsShape pShape = new PhysicsShape(sprite, body);
			pShape.getBody().setUserData(id);
			world.addShape(pShape);
			scene.addEventListener(sprite);

			scene.getTopLayer().add(sprite);
		}
	}


	private FixtureDef createFixtureDef(float density, float restitution, float friction) {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;
		fixtureDef.friction = friction;
		fixtureDef.isSensor = false;
		return fixtureDef;
	}

	private Body createBoxBody(PhysicsWorld physicsWorld, Shape shape,
			BodyType bodyType, FixtureDef fixtureDef) {
		float pixelToMeterRatio = PhysicsWorld.PIXEL_TO_METER_RATIO_DEFAULT;
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = bodyType;

		float[] sceneCenterCoordinates = shape.getGlobalCenterCoordinates();
		boxBodyDef.position.x = sceneCenterCoordinates[0] / (float)pixelToMeterRatio;
		boxBodyDef.position.y = sceneCenterCoordinates[1] / (float)pixelToMeterRatio;

		Body boxBody = physicsWorld.createBody(boxBodyDef);
		PolygonShape boxPoly = new PolygonShape();

		float halfWidth = shape.getWidthScaled() * 0.5f / pixelToMeterRatio;
		float halfHeight = shape.getHeightScaled() * 0.5f / pixelToMeterRatio;

		boxPoly.setAsBox(halfWidth, halfHeight);
		fixtureDef.shape = boxPoly;
		boxBody.createFixture(fixtureDef);
		boxPoly.dispose();

		boxBody.setTransform(boxBody.getWorldCenter(), MathUtil.degToRad(shape.getAngle()));

		return boxBody;
	}

	@Override
	public void onUpdateScene(E3Scene scene, long arg1) {
		if (m_Code.length() > 0) {
			int code = 0;
			int code_9 = 0;

			try {
				switch (Integer.valueOf(m_Code.substring(8, 9))) {
				case 1:
				case 3:
				case 5:
				case 7:
				case 9:
					code_9 = 1;
					break;
				}
				int code_11 = Integer.valueOf(m_Code.substring(10, 11));
				code = code_9*10 + code_11;
				if (code >= 13) {
					code = code_11;
				}

			} catch (NumberFormatException e) {
			}
			float speed = PreferenceActivity.getSoundSpeed(MainActivity.this);
			sp.play(seID[code], 1.0F, 1.0F, 0, 0, speed);
			postUpdate(new AddShapeImpl(scene, getWidth() / 2, 0, code));
			m_Code = "";
		}

	}
}
