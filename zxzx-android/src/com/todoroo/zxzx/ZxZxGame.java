package com.todoroo.zxzx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.todoroo.zxzx.helpers.Assets;

public class ZxZxGame extends Game {

    Screen mainMenuScreen;
    Screen playingScreen;

    /** Creates all the screens that the game will need, then switches to the main menu. */
    public void create() {
        Assets.load();
        // mainMenuScreen = new MainMenuScreen(this);
        playingScreen = new WorldPresenter(this);
        setScreen(playingScreen);
    }

}
