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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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

		void pause ();

		void resume ();
	}

	private static final float PARTICLE_SIZE = Config.asFloat("particle.size", 0.1875f);

	private static final int SPRITE_CACHE_SIZE = 128;

	private static final int MAX_PARTICLES = 256;

	private final World world;
	private final Presenter presenter;
	private OrthographicCamera worldCam;
	private SpriteCache spriteCache;
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
		spriteCache = new SpriteCache(SPRITE_CACHE_SIZE, true);
		spriteBatch.setProjectionMatrix(worldCam.combined);
		spriteCache.setProjectionMatrix(worldCam.combined);
		cacheTransform = new Matrix4();
		touchPoint = new Vector3();
		dragPoint = new Vector3();
		joystick = new Vector2();
		particleManager = new ParticleManager(MAX_PARTICLES, PARTICLE_SIZE);
		particleAdapter = new ParticleAdapter(world, particleManager);
	}

	public void update (float delta) {
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
                spriteCache.setTransformMatrix(cacheTransform);
                spriteBatch.setTransformMatrix(cacheTransform);
            }

        case World.PLAYER_DEAD:
            drawBackground();
            drawMobiles();
        }
	}

	private void drawBackground () {
		spriteCache.begin();
		// spriteCache.draw(cacheId);
		spriteCache.end();
	}

	private void drawMobiles () {
		spriteBatch.setProjectionMatrix(worldCam.combined);
		spriteBatch.begin();
		spriteBatch.setColor(Color.WHITE);
		drawPlayersShots();
		drawPlayer();
		drawParticles();
		drawBullets();
		spriteBatch.end();
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
	    BulletManager manager = world.getBulletManager();

	    renderer.begin();
	    manager.draw(renderer);
	    renderer.end();
	}

	private class BulletRenderer implements AbstractBulletRenderer {

	    private ShapeRenderer shapeRenderer = new ShapeRenderer(500);

	    public void begin() {
	        shapeRenderer.setProjectionMatrix(worldCam.combined);
	        shapeRenderer.begin(ShapeType.Line);
	    }

        public void drawBullet(int x1, int y1, int x2, int y2) {
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.line(x1, y1, x2, y2);
        }

        public void end() {
            shapeRenderer.end();
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
				presenter.resume();
			}
		} else if (Gdx.input.isTouched()) {
			worldCam.unproject(dragPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			float dx = dragPoint.x - world.getPlayer().x;
            float dy = dragPoint.y - world.getPlayer().y;

            if(Math.abs(dx) < 20)
                dx = 0;
            if(Math.abs(dy) < 20)
                dy = 0;

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
