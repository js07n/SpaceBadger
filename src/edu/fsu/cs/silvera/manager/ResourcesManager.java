package edu.fsu.cs.silvera.manager;

import java.io.IOException;
import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;

import edu.fsu.cs.silvera.spacebadger.MainActivity;
//import org.andengine.opengl.texture.atlas.bitmap.source.IBi	tmapTextureAtlasSource;


public class ResourcesManager
{
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------
    
    private static final ResourcesManager INSTANCE = new ResourcesManager();
    
    public Engine engine;
    public MainActivity activity;
    public Camera camera;
    public VertexBufferObjectManager vbom;
    
	public final int CAMERA_WIDTH = 480;
	public final int CAMERA_HEIGHT = 800;
    
	public Font font;
	
	EngineOptions eopts; 
	private Music bgmusic;
	
    //---------------------------------------------
    // TEXTURES & TEXTURE REGIONS
    //---------------------------------------------
    
    public ITextureRegion splash_region;
    private BitmapTextureAtlas splashTextureAtlas;
    
    public ITextureRegion menu_background_region;
    public ITextureRegion play_region;
    public ITextureRegion highScores_region;
        
    private BuildableBitmapTextureAtlas menuTextureAtlas;
    
    // Game Texture
    public BuildableBitmapTextureAtlas gameTextureAtlas;
        
    public TiledTextureRegion player_region;

    public ITextureRegion game_background_region;
    
    public ITextureRegion bee_region;
    public ITextureRegion comb_region;
    
//    private TexturePackTextureRegionLibrary texturePackLibrary;
//    private TexturePack texturePack;
    
    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------

    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadMenuAudio();
        loadMenuFonts();
    }
    
    public void loadGameResources()
    {
        loadGameGraphics();
        loadGameFonts();
        loadGameAudio();
    }
    
    private void loadMenuGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
    	menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
    	menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menubg1.png");
    	play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play1.png");
    	highScores_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "score1.png");
    	
    	try 
    	{
    	    this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    	    this.menuTextureAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e)
    	{
    	        Debug.e(e);
    	}
    }
    
    private void loadMenuAudio()
    {
        
    }

    private void loadGameGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
    	gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.NEAREST);
    	
    	game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "gamebg3.png");
    	
    	// 3 = col , 1 = row
    	player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "HB2.png", 3, 1);
    	bee_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "bee5.png");
 
    	comb_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "comb2.png");
    	
    	try 
    	{
    	    this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    	    this.gameTextureAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e)
    	{
    	        Debug.e(e);
    	}
    	
    	
  
    }
    
    private void loadGameFonts()
    {
        
    }
    
    private void loadGameAudio()
    {
    	
    	try
    	{
    	    bgmusic = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/DST-CyberOps.mp3");
    	    bgmusic.setLooping(true);
    	}
    	catch (IOException e)
    	{
    	    e.printStackTrace();
    	}

    }
    
    public void loadSplashScreen()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    	splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 480, 800, TextureOptions.BILINEAR);
    	splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash4.png", 0, 0);
    	splashTextureAtlas.load();
    	
    }
    
    public void unloadSplashScreen()
    {

    	splashTextureAtlas.unload();
    	splash_region = null;
    }
    
    /**
     * @param engine
     * @param activity
     * @param camera
     * @param vbom
     * <br><br>
     * We use this method at beginning of game loading, to prepare Resources Manager properly,
     * setting all needed parameters, so we can latter access them from different classes (eg. scenes)
     */
    public static void prepareManager(Engine engine, MainActivity activity, Camera camera, VertexBufferObjectManager vbom)
    {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
    
    

    private void loadMenuFonts()
    {
        FontFactory.setAssetBasePath("font/");
        final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "HoneyScript-Light.ttf", 50, true, android.graphics.Color.WHITE, 2, android.graphics.Color.BLACK);
        font.load();
    }
    
    public void unloadMenuTextures()
    {
        menuTextureAtlas.unload();
    }
        
    public void loadMenuTextures()
    {
        menuTextureAtlas.load();
    }
    
    public void unloadGameTextures()
    {
    	gameTextureAtlas.unload();

    }
    
    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------
    
    public static ResourcesManager getInstance()
    {
        return INSTANCE;
    }
}
