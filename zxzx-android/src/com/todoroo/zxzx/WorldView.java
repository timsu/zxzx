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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.todoroo.zxzx.entity.AlienShip;
import com.todoroo.zxzx.entity.Player;
import com.todoroo.zxzx.entity.PlayerShot;
import com.todoroo.zxzx.general.Config;
import com.todoroo.zxzx.general.GameObject;
import com.todoroo.zxzx.general.Particle;
import com.todoroo.zxzx.general.ParticleManager;

/** The <code>WorldView</code> displays the {@link World} on screen. It also provides the means by which the player can control the
 * game.
 *
 * @author Rod */
public class WorldView {

	/** The <code>Presenter</code> interface is how the <code>WorldView</code> communicates the state of its controls. */
	public static interface Presenter {
		void setController (float x, float y);

		void pauseGame ();

		void resumeGame ();
	}

	private static final float PARTICLE_SIZE = Config.asFloat("particle.size", 0.1875f);

	private static final int MAX_PARTICLES = 256;

	private final World world;
	private final Presenter presenter;
	private OrthographicCamera worldCam;
	private BackgroundRenderer backgroundRenderer;
	private Matrix4 cacheTransform;
	private SpriteBatch spriteBatch;
	private Vector3 touchPoint;
	private Vector3 dragPoint;
	private Vector2 joystick;
	private final ParticleManager particleManager;
	private final ParticleAdapter particleAdapter;

	/** Constructs a new WorldView.
	 *
	 * @param world the {@link World} that this is a view of.
	 * @param presenter the interface by which this <code>WorldView</code> communicates the state of its controls. */
	public WorldView (World world, Presenter presenter) {
		this.world = world;
		this.presenter = presenter;

		worldCam = new OrthographicCamera();
		worldCam.setToOrtho(false, 800, 1280);
		worldCam.update();

		spriteBatch = new SpriteBatch();
		spriteBatch.setProjectionMatrix(worldCam.combined);
		backgroundRenderer = new BackgroundRenderer(worldCam);
		cacheTransform = new Matrix4();
		touchPoint = new Vector3();
		dragPoint = new Vector3();
		joystick = new Vector2();
		particleManager = new ParticleManager(MAX_PARTICLES, PARTICLE_SIZE);
		particleAdapter = new ParticleAdapter(world, particleManager);
	}

	public void update (float delta) {
	    backgroundRenderer.update(delta);
		particleAdapter.update(delta);
	}

	/** Called when the view should be rendered.
	 *
	 * @param delta the time in seconds since the last render. */
	public void render (float delta) {
        switch (world.getState()) {

        case World.RESETTING:
            break;

        case World.PLAYING:
            if (world.getStateTime() == 0.0f) {
                cacheTransform.idt();
                spriteBatch.setTransformMatrix(cacheTransform);
            }

        case World.PLAYER_DEAD:
        case World.ALIEN_DEAD:
        case World.VICTORY:
            drawBackground();
            drawMobiles();
        }

        if(world.getState() == World.ALIEN_DEAD)
            drawText("Stage cleared!");
        else if(world.getState() == World.PLAYER_DEAD)
            drawText("You died.");
        else if(world.getState() == World.VICTORY)
            drawText("YOU WIN. VICTORY!!!");
        else if(world.isPaused())
            drawText("Paused");
	}

	private void drawText(CharSequence string) {
	    BitmapFont font = Assets.textFont;
	    spriteBatch.begin();
        spriteBatch.setColor(Color.WHITE);
        font.drawWrapped(spriteBatch, string, worldCam.viewportWidth * 0.1f,
                worldCam.viewportHeight / 2, worldCam.viewportWidth * 0.8f,
                HAlignment.CENTER);
        spriteBatch.end();
    }

    private void drawBackground () {
	    backgroundRenderer.render(spriteBatch);
	}

	private void drawMobiles () {
		spriteBatch.setProjectionMatrix(worldCam.combined);
		spriteBatch.begin();
		spriteBatch.setColor(Color.WHITE);
		drawPlayersShots();
		drawBullets();
		drawBoss();
		drawPlayer();
		drawParticles();
		spriteBatch.end();
	}

	private void drawBoss() {
	    AlienShip boss = world.getAlienShip();
	    if(boss != null && boss.getSprite() != null) {
	        if(boss.state == AlienShip.DEATH)
	            draw(boss, Assets.playerDeathAnimation.getKeyFrame(boss.stateTime, false));
	        else
	            draw(boss, boss.getSprite());

	        spriteBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
            spriteBatch.setColor(1, 1, 0.5f, 0.8f);
            float width = boss.getAlienHealthPercentage() * worldCam.viewportWidth;
            spriteBatch.draw(Assets.pureWhiteTextureRegion,
                    0, worldCam.viewportHeight - 2, width, 2);
            spriteBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    }

    }

    private void drawPlayersShots () {
	    if(world.getPlayerShots() == null)
	        return;

		for (PlayerShot shot : world.getPlayerShots()) {
			draw(shot, Assets.playerShot);
		}
	}

	private void drawPlayer () {
		Player player = world.getPlayer();

		switch (player.state) {
		case Player.FLYING_LEFT:
			draw(player, Assets.playerLeft);
			break;
		case Player.FLYING_RIGHT:
		    draw(player, Assets.playerRight);
			break;
		case Player.DEATH:
		    draw(player, Assets.playerDeathAnimation.getKeyFrame(player.stateTime, false));
		    break;
		default:
		    draw(player, Assets.playerAnimation.getKeyFrame(player.stateTime, true));
		    break;
		}
	}

	private void draw (GameObject go, TextureRegion region) {
		spriteBatch.draw(region, go.x, go.y, go.width, go.height);
	}

	private void drawParticles () {
		spriteBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		for (Particle particle : particleManager.getParticles()) {
			if (particle.active) {
				spriteBatch.setColor(particle.color);
				spriteBatch.draw(Assets.pureWhiteTextureRegion, particle.x, particle.y, particle.size, particle.size);
			}
		}
		spriteBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}

	private void drawBullets() {
	    BulletManager[] manager = world.getBulletManagers();

	    renderer.begin();
	    for(int i = 0; i < manager.length; i++)
	        manager[i].draw(renderer);
	    renderer.end();
	}

	private class BulletRenderer implements AbstractBulletRenderer {

	    public void begin() {
	        spriteBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
	    }

        public void drawBullet(int x1, int y1, int x2, int y2) {
            spriteBatch.draw(Assets.alienShot, x1 - 16, y1 - 16);
        }

        public void end() {
            spriteBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        }

	}

	BulletRenderer renderer = new BulletRenderer();

	/** Updates the state of the on-screen controls.
	 *
	 * @param delta time in seconds since the last render. */
	public void updateControls (float delta) {
		presenter.setController(0.0f, 0.0f);
		if (Gdx.input.justTouched()) {
			worldCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			if (world.isPaused()) {
				presenter.resumeGame();
			}
		} else if (world.getState() == World.PLAYING && Gdx.input.isTouched()) {
			worldCam.unproject(dragPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			float dx = dragPoint.x - world.getPlayer().x - world.getPlayer().width / 2;
            float dy = dragPoint.y - (world.getPlayer().y - 60);

            joystick.set(dx, dy);
			float len = joystick.len();
			if (len > 1) {
				joystick.nor();
			}
			if (presenter != null) {
				presenter.setController(joystick.x, joystick.y);
			}
		}
	}
}
