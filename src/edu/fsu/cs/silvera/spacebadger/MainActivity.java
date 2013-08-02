package edu.fsu.cs.silvera.spacebadger;


import java.io.IOException;
import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import edu.fsu.cs.silvera.manager.ResourcesManager;
import edu.fsu.cs.silvera.manager.SceneManager;

import android.view.KeyEvent;


public class MainActivity extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;


	private ResourcesManager resourcesManager;

	
	private Scene splashScene;
	private Scene mainScene;
	
	private BitmapTextureAtlas splashTextureAtlas;
	private ITextureRegion splashTextureRegion;
	private Sprite splash;
	
	

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) 
	{
		
		 Parse.initialize(this, "Y8hyUj7wKrbGODfJMkckH2dRl9pwUoY7QJATekPA", "rFqOu8SFi7QVNmcUtBzHE3V8LjtayNzQwEqEOax3");
		 ParseAnalytics.trackAppOpened(getIntent());
		 
//		 ParseObject testObject = new ParseObject("TestObject");
//		 testObject.put("foo", "bar");
//		 testObject.saveInBackground();
		 
	    return new LimitedFPSEngine(pEngineOptions, 60);
	    
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {

		mCamera = new BoundCamera(0, 0, 480, 800);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(480, 800), this.mCamera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
		
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

	    ResourcesManager.prepareManager(mEngine, this, mCamera, getVertexBufferObjectManager());
	    resourcesManager = ResourcesManager.getInstance();
	   
		
		splashTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 512, 512, TextureOptions.NEAREST);
		splashTextureRegion =BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas,
		this,"kiss.png", 0, 0);
		splashTextureAtlas.load();
		pOnCreateResourcesCallback.onCreateResourcesFinished();

	}

	private void initSplashScene()
	{
	    splashScene = new Scene();
	    splash = new Sprite(0, 0, splashTextureRegion, mEngine.getVertexBufferObjectManager())
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera)
	        {
	            super.preDraw(pGLState, pCamera);
	           pGLState.enableDither();
	        }
	    };

	    splash.setScale(1.5f);
	    splash.setPosition((CAMERA_WIDTH - splash.getWidth()) * 0.5f, (CAMERA_HEIGHT - splash.getHeight()) * 0.5f);
	    splashScene.attachChild(splash);
	}
	
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		
		
		

	    SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);

	}

	
	 public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) 
			 throws IOException {
				mEngine.registerUpdateHandler(new TimerHandler(3f, new ITimerCallback() 
				{
					public void onTimePassed(final TimerHandler pTimerHandler) 
					{
						mEngine.unregisterUpdateHandler(pTimerHandler);
						SceneManager.getInstance().createMenuScene();
						
//						loadResources();
	//					loadScenes();         
	//					splash.detachSelf();
	//					mEngine.setScene(mainScene);			            
			            
						   // load menu resources, create menu scene
		                // set menu scene using scene manager
		                // disposeSplashScene();
					}
				}));
			 
				
			pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	public void loadResources() 
	{
	    // Load your game resources here!
	}

	private void loadScenes()
	{
	    // load your game here, you scenes
	    mainScene = new Scene();
	    mainScene.setBackground(new Background(50, 50, 50));
	}


	@Override 
	public void onPause(){
		super.onPause();
		this.getEngine().getMusicManager().setMasterVolume(0);
	}
	
	@Override 
	public void onResumeGame(){
		super.onResumeGame();
		this.getEngine().getMusicManager().setMasterVolume(1);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
	    }
	    return false; 
	}

	
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
