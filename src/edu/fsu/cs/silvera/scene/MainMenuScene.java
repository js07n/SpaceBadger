package edu.fsu.cs.silvera.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import edu.fsu.cs.silvera.manager.SceneManager;
import edu.fsu.cs.silvera.manager.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{

	

	private MenuScene menuChildScene;
	private final int MENU_PLAY = 0;
	private final int MENU_SCORE = 1;

	
	
	private void createMenuChildScene()
	{
		menuChildScene = new MenuScene(camera);		

		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.2f, 1);		
		final IMenuItem scoreMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SCORE, resourcesManager.highScores_region, vbom), 1.2f, 1);
    
		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(scoreMenuItem);
    
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
    
		playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() - 20);
		scoreMenuItem.setPosition(scoreMenuItem.getX(), scoreMenuItem.getY() );
    
		menuChildScene.setOnMenuItemClickListener(this);
    
		setChildScene(menuChildScene);
	}

	
	
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
	        switch(pMenuItem.getID())
	        {
	        case MENU_PLAY:
	        	//Load Game Scene!
	            SceneManager.getInstance().loadGameScene(engine);
	            return true;
	        case MENU_SCORE:
	        	showScoreDialog();
	            return true;
	        default:
	            return false;
	    }
	}
	
	


	@Override
	public void createScene()
	{
	     createBackground();
	     createMenuChildScene();
	}

	private void createBackground()
	{
	    attachChild(new Sprite(0, 0, resourcesManager.menu_background_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	}
	
	@Override
	public void onBackKeyPressed()
	{
	    System.exit(0);
	}

	@Override
	public SceneType getSceneType()
	{
	    return SceneType.SCENE_MENU;
	}
	
	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}

	
	private Runnable showScoreDialog() {
	   	 activity.runOnUiThread(new Runnable() {
		     @Override
		     public void run() {				
		         AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		         alert.setTitle("HIGH SCORES!");

		         ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(activity, "GameScore");
		         adapter.setTextKey("all");

		         final ListView listView = new ListView(activity);
		         LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		        		 LinearLayout.LayoutParams.MATCH_PARENT,
		        		 LinearLayout.LayoutParams.MATCH_PARENT);
		         listView.setLayoutParams(lp);
		     	 listView.setAdapter(adapter);
		         alert.setView(listView);
		         
		         alert.setPositiveButton("MAIN MENU", new OnClickListener() {
		                 @Override
		                 public void onClick(DialogInterface arg0, int arg1) {

		                 }
		         });		        	        
		         
		         alert.show();
		     }
	   	 });
	   	 
	   	 return null;
	}
		
}
