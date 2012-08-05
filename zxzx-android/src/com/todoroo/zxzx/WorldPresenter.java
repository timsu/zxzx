/*
 * Copyright 2011 Rod Hyde (rod@badlydrawngames.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.todoroo.zxzx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.todoroo.zxzx.entity.Player;
import com.todoroo.zxzx.general.GameScreen;

/** <p>
 * It is the role of the <code>WorldPresenter</code> to glue together the {@link World} and the {@link WorldView}. It passes on
 * information from the controls in the WorldView to the World, updates it, then asks the WorldView to draw everything on its
 * behalf. The {@link WorldView} tells this <code>WorldPresenter</code> what to do via the methods provided by the
 * {@link WorldView#Presenter} interface.
 * </p>
 *
 * @author Rod */
public class WorldPresenter extends GameScreen<ZxZxGame> implements WorldView.Presenter {

	private static final float MAX_DELTA = 0.1f;

	private final World world;
	private final WorldView worldView;
	private final SoundManager soundManager;

	private int score;
	private boolean isDead;
	private boolean wasBackPressed;

	/** Constructs a new WorldPresenter.
	 *
	 * @param game the game, used primarily for switching between screens. */
	public WorldPresenter (ZxZxGame game) {
		super(game);
		world = new World();
		worldView = new WorldView(world, this);
		soundManager = new SoundManager();
		world.addWorldListener(soundManager);
	}

	@Override
	public void show () {
		Gdx.input.setCatchBackKey(true);
		wasBackPressed = false;
		world.reset();
		resumeGame();
	}

	@Override
	public void pause () {
		pauseGame();
	}

	@Override
	public void resume () {
		Gdx.input.setCatchBackKey(true);
		pauseGame();
	}

	@Override
	public void hide () {
		Gdx.input.setCatchBackKey(false);
	}

	/** Called by libgdx when this screen should render itself. It responds to a request to render by updating the controls,
	 * updating the world and the managers, then drawing the views.
	 *
	 * @param delta the time in seconds since the last time <code>render</code> was called. */
	@Override
	public void render (float delta) {
		// Update time.
		if (delta >= MAX_DELTA) delta = MAX_DELTA;

		// Ask the view to update the controls.
		worldView.updateControls(delta);

		// If we're not paused then update the world and the subsystems.
		world.update(delta);
		if (!world.isPaused()) {
			worldView.update(delta);
			soundManager.update(delta);
		}

		// Clear the screen and draw the views.
		Gdx.gl.glClearColor(8/255.0f, 54/255.0f, 129/255.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		worldView.render(delta);

		boolean isBackPressed = Gdx.input.isKeyPressed(Input.Keys.BACK);
		if (!wasBackPressed && isBackPressed) {
			if (!world.isPaused()) {
				world.pause();
			}
		}
		wasBackPressed = isBackPressed;
	}

	/** Called by the {@link WorldView} when the player wants to move.
	 *
	 * @param x the x value of the controller.
	 * @param y the y value of the controller. */
	public void setController (float x, float y) {
		Player player = world.getPlayer();
		player.setController(x, y);
	}

	public void onScoreChanged (int score) {
		this.score = score;
	}

	public void onLivesChanged (int lives) {
		isDead = (lives == 0);
	}

	public int getScore() {
        return score;
    }

	public boolean isDead() {
        return isDead;
    }

    public void pauseGame() {
        world.pause();
    }

    public void resumeGame() {
        world.resume();
    }
}
