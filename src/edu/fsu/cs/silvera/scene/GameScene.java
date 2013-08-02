package edu.fsu.cs.silvera.scene;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.math.MathUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.hardware.SensorManager;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.parse.ParseObject;
import edu.fsu.cs.silvera.scene.BaseScene;
import edu.fsu.cs.silvera.manager.SceneManager;
import edu.fsu.cs.silvera.manager.SceneManager.SceneType;


public class GameScene extends BaseScene implements  IOnSceneTouchListener
{
	
	private HUD gameHUD;
	private Text scoreText;
	private int score = 0;
	private Text healthText;
	final private int MAX_HEALTH = 3;
	private int health = MAX_HEALTH;
	
	private int secsSurvived = 0;
	private int gameTimer = 0;
	
	private PhysicsWorld mPhysicsWorld;
	
	private Music bgmusic;
	private Sound growl;
	private Sound chomp;
	
	//!!
	AnimatedSprite player;
	Body body;
	int sizeOfArray = 5;

	private float mSpawnDelay;

	int lastTimeHit = 0;
	int currentSecond = 0;
	
	
    @Override
    public void createScene()
    {
    	mSpawnDelay = 0.5f;
    	
        createBackground();
        createHUD();
       
        createPhysics();
        setOnSceneTouchListener(this);
        
        createPlayer();
        
        setMusic();
        setSounds();
        
        createSpriteSpawnTimeHandler();        
        difficultyTimeHandler();        
        gameTimeHandler();

    }



	private void createPlayer() {
		
		player = new AnimatedSprite(resourcesManager.CAMERA_WIDTH/2, resourcesManager.CAMERA_HEIGHT/2, resourcesManager.player_region, resourcesManager.vbom){
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				player.setPosition(pSceneTouchEvent.getX() - player.getWidth() / 2, pSceneTouchEvent.getY() - player.getHeight() / 2);
				
				return true;
			}
		};
		
		player.setScaleCenter(0, 0);
//		player.setScale(0.9f);
		
		FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		//!!
		body = PhysicsFactory.createBoxBody(mPhysicsWorld, player, BodyType.DynamicBody, objectFixtureDef);
		
		
		player.animate(200);
		
		attachChild(player);
		
		registerTouchArea(player); //!!
		setTouchAreaBindingOnActionDownEnabled(true);
		body.setUserData("player");
		
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(player, body, false, false));

	}
	
	

	
	private void createBee(final float pX, final float pY) {
		
		final Sprite bee;
		final Body beeBody;
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		
		bee = new Sprite(pX, pY, resourcesManager.bee_region, resourcesManager.vbom);
		
		beeBody = PhysicsFactory.createBoxBody(mPhysicsWorld, bee, BodyType.DynamicBody, objectFixtureDef);
		
//		bee.animate(100);
		
//		bee.setScale(0.8f, 0.8f);
				
		registerTouchArea(bee); //!!
		attachChild(bee);
		beeBody.setUserData("bee");

		final PhysicsConnector beePhysicsConnector = new PhysicsConnector(bee, beeBody, true, true);
		mPhysicsWorld.registerPhysicsConnector(beePhysicsConnector);
		
		//!!  NEEDS A LOT OF  WORK!!!
		registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override	
			public void onUpdate(final float pSecondsElapsed) {

				if(bee.isVisible()== true){
					
					if(bee.collidesWith(player))
					{	

						bee.setVisible(false);
						activity.runOnUiThread(removeBee(bee));
						
						//this is to let users have an invulnerable window
						//after getting hit 
						currentSecond = (int) System.currentTimeMillis(); 

						if(lastTimeHit == 0)
						{
							addToScore(-5);
							addToHealth(-1);
							lastTimeHit = currentSecond;
							growl.play();
						}else if (lastTimeHit + 0.00002E8  <= currentSecond){
							addToScore(-5);
							
							//player has no life
							//game over
							if(health == 1)
							{
								showGameOverDialog();
							}
							
							addToHealth(-1);
							lastTimeHit = currentSecond;
							growl.play();
						}else
							//do nothing
						;
										
					}
				}
				
				// get rid of bees when they reach the bottom of the screen				
				if(bee.getY() >= resourcesManager.CAMERA_HEIGHT - 70.0f)
				{
					bee.setVisible(false);
					activity.runOnUiThread(removeBee(bee));
					
					
				}	
			}
				
			
			
		});	
		
	}

	

	
	private Runnable removeBee(final Sprite bee) {
		final PhysicsConnector beePhysicsConnector = mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(bee);
		
		engine.runOnUpdateThread(new Runnable() 
		{
		    @Override
		    public void run() 
		    {
		        if (beePhysicsConnector != null)
		        {
		        	
		             mPhysicsWorld.unregisterPhysicsConnector(beePhysicsConnector);
		             body.setActive(false);
		             mPhysicsWorld.destroyBody(beePhysicsConnector.getBody());
		             detachChild(beePhysicsConnector.getShape());
		             
		             System.gc();
		        }
		    }
		});

		return null;
	}
	
	
	
	
	private void gameTimeHandler()
	{
		TimerHandler gameElapsedTimerHandler;
	       
	        registerUpdateHandler(gameElapsedTimerHandler = new TimerHandler(1.0f, true, new ITimerCallback()
	        {                      
	            public void onTimePassed(final TimerHandler mTimerHandler)
	            {   
	            	gameTimer++;	            	
	            }
	        }));
	}
	
	
	
	private void difficultyTimeHandler()
	{
		TimerHandler beeHellTimerHandler;
	       
	        registerUpdateHandler(beeHellTimerHandler = new TimerHandler(10.0f, true, new ITimerCallback()
	        {                      
	            public void onTimePassed(final TimerHandler mTimerHandler)
	            {   
	            	secsSurvived += 10;
	            		            	
	            	//increase score +10 for surviving every ten sec.
	            	addToScore(10);
	            	//increase spawn rate by 40%
	            	mSpawnDelay = mSpawnDelay * 0.40f;
	            	Log.d("SPAWN RATE", Float.toString(mSpawnDelay));
	            	
	            	//increase gravity aka travel speed of bees
//	            	Vector2 pGravity = mPhysicsWorld.getGravity();					
//	            	mPhysicsWorld.setGravity(pGravity * 0.95f);
	            	
	            	
	            	//comb spawns every 20 secs
	            	if(secsSurvived % 20 == 0)
	            		createComb();
	            	
	            }
	        }));
	}
	
	
	private void createSpriteSpawnTimeHandler()
	{
		
		TimerHandler spriteTimerHandler;
	       
	        registerUpdateHandler(spriteTimerHandler = new TimerHandler(mSpawnDelay, true, new ITimerCallback()
	        {                      
	            public void onTimePassed(final TimerHandler pTimerHandler)
	            {       
	                //Random Position Generator
	            	float xPos;
	            
	            	if((player.getX() + 150.0f) > (resourcesManager.CAMERA_WIDTH - 30.0f))
	            		xPos =MathUtils.random((player.getX() - 150.0f), (resourcesManager.CAMERA_WIDTH - 30.0f));
	            	else if((player.getX() - 150.0f) < 30.0f)
	            		xPos =MathUtils.random(30.0f, (player.getX() + 150.0f));
	            	else
	            		xPos =MathUtils.random((player.getX() - 150.0f), (player.getX() + 150.0f));	            	
	                       
	              createBee(xPos, 0);
              
	            }
	        }));
	}
	

	
	
	
	private void createComb() {
		
		final Sprite comb;
		final Body combBody;
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		
    	float xPos;
    	
		xPos = MathUtils.random(30.0f, (resourcesManager.CAMERA_WIDTH - 30.0f));
		
		comb = new Sprite(xPos, 0, resourcesManager.comb_region, resourcesManager.vbom);
		
		combBody = PhysicsFactory.createBoxBody(mPhysicsWorld, comb, BodyType.DynamicBody, objectFixtureDef);

		
		comb.setScale(0.9f, 0.9f);
			
		registerTouchArea(comb); //!!
		attachChild(comb);
		combBody.setUserData("comb");

		
		final PhysicsConnector combPhysicsConnector = new PhysicsConnector(comb, combBody, true, true);
		mPhysicsWorld.registerPhysicsConnector(combPhysicsConnector);
		
		//!!  NEEDS WORK!
		registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			
			public void onUpdate(final float pSecondsElapsed) {

				if(comb.isVisible()== true){
					
					if(comb.collidesWith(player))
					{	
						comb.setVisible(false);
						activity.runOnUiThread(removeComb(comb));

						addToScore(10);
						
						if(health >= MAX_HEALTH - 1)
						{
							health = MAX_HEALTH;
							addToHealth(0);
							chomp.play();
						}else 
						{
							addToHealth(1);
							chomp.play();
						}										
					}
				}
				
				
				if(comb.getY() >= resourcesManager.CAMERA_HEIGHT - 80.0f)
				{
					comb.setVisible(false);
					activity.runOnUiThread(removeComb(comb));	
				}	
			}
					
		});	
		
	}
	

	

	protected Runnable removeComb(Sprite comb) {
		final PhysicsConnector combPhysicsConnector = mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(comb);
		
		engine.runOnUpdateThread(new Runnable() 
		{
		    @Override
		    public void run() 
		    {
		        if (combPhysicsConnector != null)
		        {        	
		             mPhysicsWorld.unregisterPhysicsConnector(combPhysicsConnector);
		             body.setActive(false);
		             mPhysicsWorld.destroyBody(combPhysicsConnector.getBody());
		             detachChild(combPhysicsConnector.getShape());
		             
		             System.gc();
		        }
		    }
		});
		
		return null;
	}







	private void teleport(final float pX, final float pY) {
		player.setPosition(pX, pY);	
	}

	
	
	@Override
    public void onBackKeyPressed()
    {
		bgmusic.stop();
		
        SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneType getSceneType()
    {
        return SceneType.SCENE_GAME;
    }


    @Override
	public void disposeScene()
	{
    	camera.setHUD(null);
    	camera.setCenter(240, 400);
    	bgmusic.release();
    	growl.release();
    	chomp.release();
    	
    	// removing all game scene objects.
	}

    
    private void createBackground()
    {
	    attachChild(new Sprite(0, 0, resourcesManager.game_background_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
    }
    

    
    private void setMusic() {
    	
    	try {
			bgmusic = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/DST-CyberOps.mp3");
			bgmusic.setLooping(true);
			bgmusic.setVolume(0.75f);
			bgmusic.play();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
    }
    
    private void setSounds() {
    	
    	try {
			growl = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity,"mfx/growl.wav");
	//		growl.setVolume(1.25f);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	try {
			chomp = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity,"mfx/chomp.wav");
			chomp.setVolume(15.0f);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
}
    //for creating score text, etc.
    private void createHUD()
    {
        gameHUD = new HUD();
        // CREATE SCORE TEXT
        scoreText = new Text(20, 240, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
//        scoreText.setAnchorCenter(0, 0);
        scoreText.setPosition(0, 0);
        
        scoreText.setText("SCORE: 0");
        gameHUD.attachChild(scoreText);
        
        healthText = new Text(240, 800, resourcesManager.font, "Health: " + Integer.toString(MAX_HEALTH), new TextOptions(HorizontalAlign.LEFT), vbom );
        healthText.setPosition(240, 0);
        
        healthText.setText("HEALTH: " + Integer.toString(MAX_HEALTH));
        gameHUD.attachChild(healthText);
        camera.setHUD(gameHUD);
    }
    
    

    private void addToScore(int i)
    {
        score += i;
        scoreText.setText("SCORE: " + score);
    }
    
    private void addToHealth(int i)
    {
        health += i;
        healthText.setText("HEALTH: " + health);
    }
    
    

    private void createPhysics()
    {
    	mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_MOON), false);

		final VertexBufferObjectManager vertexBufferObjectManager = resourcesManager.vbom;
		final Rectangle ground = new Rectangle(0, resourcesManager.CAMERA_HEIGHT - 2, resourcesManager.CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, resourcesManager.CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, resourcesManager.CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(resourcesManager.CAMERA_WIDTH - 2, 0, 2, resourcesManager.CAMERA_HEIGHT, vertexBufferObjectManager);

		ground.setColor(Color.BLACK);
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		attachChild(ground);
		attachChild(roof);
		attachChild(left);
		attachChild(right);
		
        registerUpdateHandler(mPhysicsWorld);
    }
    
    
    

    
	private Runnable showPauseDialog() {
   	 activity.runOnUiThread(new Runnable() {
	     @Override
	     public void run() {
			engine.stop();
			bgmusic.pause();				
	         AlertDialog.Builder alert = new AlertDialog.Builder(activity);
	         alert.setTitle("PAUSE");

//	         alert.setMessage("PAUSE");
	         alert.setPositiveButton("READY!", new OnClickListener() {
	                 @Override
	                 public void onClick(DialogInterface arg0, int arg1) {
	                	 engine.start();
	                	 bgmusic.play();
	                 }
	         });

	         alert.show();
	     }
	    });
	return null;
	}
    
	
	private Runnable showGameOverDialog() {
	   	 activity.runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
				engine.stop();
				bgmusic.pause();				
		         AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		         alert.setTitle("GAME OVER!");
		         final String Time;
		         

		         if(secsSurvived%60 > 9){
		        	 Time = Integer.toString(secsSurvived/60) + ":" + Integer.toString(gameTimer%60);
		         }
		         else
		         {
			         Time = Integer.toString(secsSurvived/60) + ":0" + Integer.toString(gameTimer%60);
		         }
		         
		         alert.setMessage("Score: " + Integer.toString(score) + 
		        		 "   Time Survived: " + Time);

		         final EditText input = new EditText(activity);
		         LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		        		 LinearLayout.LayoutParams.MATCH_PARENT,
		        		 LinearLayout.LayoutParams.MATCH_PARENT);
		         input.setLayoutParams(lp);
		         input.setInputType(InputType.TYPE_CLASS_TEXT);
		         alert.setView(input);
		         
		         
		         alert.setPositiveButton("MAIN MENU", new OnClickListener() {
		                 @Override
		                 public void onClick(DialogInterface arg0, int arg1) {
		                	engine.start();
		             		bgmusic.stop();		            		
		                    SceneManager.getInstance().loadMenuScene(engine);
		                 }
		         });
		         

		         alert.setNeutralButton("SEND SCORE", new OnClickListener() {
	                 @Override
	                 public void onClick(DialogInterface arg0, int arg1) {
	            		
	                	 
	                	 if (input.getText().toString().matches(".*\\w.*"))
	                	 { 
	                		 ParseObject gameScore = new ParseObject("GameScore");
	                	 
	                		 gameScore.put("score", score);
	                		 gameScore.put("playerName", input.getText().toString());
	                		 gameScore.put("time", Time);
	                		 gameScore.put("all", input.getText().toString() + "        " + 
	                				 Integer.toString(score) + "        " + Time);
	                		 gameScore.saveEventually();
												
	                		 engine.start();
	                		 bgmusic.stop();	
	                		 SceneManager.getInstance().loadMenuScene(engine);
	                	 }
	                	 else
	                	 {
	                		 Toast.makeText(activity, "Please Enter a Name", Toast.LENGTH_SHORT).show();
	                		 showGameOverDialog();
	                	 }

	                 }
		         });

		         alert.show();
		     }
	   	 });
	   	 return null;
	}



	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		return false;
	}


}