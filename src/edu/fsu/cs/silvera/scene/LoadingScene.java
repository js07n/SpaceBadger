package edu.fsu.cs.silvera.scene;


import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

import edu.fsu.cs.silvera.scene.BaseScene;
import edu.fsu.cs.silvera.manager.SceneManager.SceneType;

public class LoadingScene extends BaseScene
{
    @Override
    public void createScene()
    {
        setBackground(new Background(Color.WHITE));
        attachChild(new Text(125, 400, resourcesManager.font, "LOADING...", vbom));
    }

    @Override
    public void onBackKeyPressed()
    {
        return;
    }

    @Override
    public SceneType getSceneType()
    {
        return SceneType.SCENE_LOADING;
    }

    @Override
    public void disposeScene()
    {

    }
}