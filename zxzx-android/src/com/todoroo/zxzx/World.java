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

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.todoroo.zxzx.entity.AlienShip;
import com.todoroo.zxzx.entity.BaseShot;
import com.todoroo.zxzx.entity.Player;
import com.todoroo.zxzx.entity.PlayerShot;
import com.todoroo.zxzx.general.Colliders;
import com.todoroo.zxzx.general.Colliders.ColliderHandler;
import com.todoroo.zxzx.general.Colliders.RemovalHandler;
import com.todoroo.zxzx.general.Config;
import com.todoroo.zxzx.general.GameObject;
import com.todoroo.zxzx.general.Pools;

/** The <code>World</code> is the representation of the game world of <b>Very Angry Robots</b>. It knows nothing about how it will
 * be displayed, neither does it know about how the player is controlled, particle effects, sounds, nor anything else. It purely
 * knows about the {@link Player}, {@link AlienShip}s and the walls of the room that the player is in.
 *
 * @author Rod */
public class World {

	/** The <code>FireCommand</code> interface is how the {@link World} is told that a {@link GameObject} wants to fire. */
	public static interface FireCommand {
		/** Tells the {@link World} that a {@link GameObject} wants to fire. Note that <code>dx</code> and <code>dy</code> must be
		 * normalised. The World does not have to fire just because it is asked to.
		 *
		 * @param firer the {@link GameObject} that wants to fire.
		 * @param dx the horizontal component of the bullet's direction.
		 * @param dy the vertical component of the bullet's direction. */
		void fire (GameObject firer, float dx, float dy);
	}

	// Wall sizes.
	public static final float WALL_HEIGHT = 0.25f;
	public static final float WALL_WIDTH = 6.0f;
	public static final float OUTER_WALL_ADJUST = WALL_HEIGHT;

	private static final int MAX_PLAYER_SHOTS = Config.asInt("Player.maxShots", 10);
	private static final float FIRING_INTERVAL = Config.asFloat("Player.firingInterval", 0.2f);
	private static final float SHOT_SPEED = Config.asFloat("Player.shotSpeed", 20f);

	// Game states.
	public static final int RESETTING = 1;
	public static final int PLAYING = 2;
	public static final int PLAYER_DEAD = 3;
	public static final int ALIEN_DEAD = 4;

	private final Pool<PlayerShot> shotPool;
	private final Rectangle roomBounds;
	private float nextFireTime;
	private float now, restartLevelTime = 0;
	private Player player;
	private AlienShip alienShip;
	private Array<PlayerShot> playerShots;
	private int state;
	private int level;
	private float stateTime;
	private boolean isPaused;
	private float pausedTime;

	private LevelManager levelManager;

	/** Constructs a new {@link World}. */
	public World() {
		roomBounds = new Rectangle(0, 0, 800, 1280);
		player = new Player();
		level = 0;

		shotPool = new Pool<PlayerShot>(MAX_PLAYER_SHOTS, MAX_PLAYER_SHOTS) {
			@Override
			protected PlayerShot newObject () {
				return new PlayerShot();
			}
		};

		levelManager = new LevelManager();
	}

	/** Resets the {@link World} to its starting state. */
	public void reset () {
		setState(RESETTING);
	}

	// -------- updating

	/** Called when the {@link World} is to be updated.
	 *
	 * @param delta the time in seconds since the last render. */
	public void update (float delta) {
		if (!isPaused) {
			now += delta;
			stateTime += delta;
            switch (state) {
            case RESETTING:
                updateResetting();
                break;
			case PLAYING:
				updatePlaying(delta);
				break;
			case PLAYER_DEAD:
                updatePlayerDead(delta);
                break;
			case ALIEN_DEAD:
                updateAlienDead(delta);
                break;
			}
		} else {
			pausedTime += delta;
		}
	}

    private void updateMobiles (float delta) {
        update(playerShots, delta);

        BulletManager[] bulletManagers = getBulletManagers();
        for(int i = 0; i < bulletManagers.length; i++)
            bulletManagers[i].update();
    }

    private void updatePlaying (float delta) {
        player.update(delta);
        alienShip.update(delta);

        if (now >= nextFireTime)
            addPlayerShot(0, SHOT_SPEED);

        updateMobiles(delta);
        checkForCollisions();
        clipBounds();
    }

    private void updatePlayerDead (float delta) {
        player.update(delta);

        updateMobiles(delta);
        checkForCollisions();

        if (now >= restartLevelTime) {
            reset();
        }
    }

    private void updateAlienDead (float delta) {
        player.update(delta);
        alienShip.update(delta);

        updateMobiles(delta);
        clipBounds();
    }

    private void updateResetting () {
        populateLevel();
    }

    private void update (Array<? extends GameObject> gos, float delta) {
        for (GameObject go : gos) {
            go.update(delta);
        }
    }

	// ------- position initialization

    private void populateLevel () {
        placePlayer();
        placeBoss();
        createPlayerShots();

        setState(PLAYING);
    }

    private void placeBoss() {
        alienShip = levelManager.initAlienShip(level);

        alienShip.initBulletManagers(roomBounds);

        alienShip.x = roomBounds.width / 2 - alienShip.width / 2;
        alienShip.y = roomBounds.height - alienShip.height;
    }

    private void placePlayer () {
        player.inCollision = false;

        player.setState(Player.FLYING);
    }


    private void createPlayerShots () {
        playerShots = Pools.makeArrayFromPool(playerShots, shotPool, MAX_PLAYER_SHOTS);
    }

	// ------- shot mechanics

    private void addPlayerShot (float dx, float dy) {
        if (state == PLAYING && playerShots.size < MAX_PLAYER_SHOTS) {
            PlayerShot shot = shotPool.obtain();
            shot.inCollision = false;
            float x = player.x + player.width / 2 - shot.width / 2;
            float y = player.y + player.height / 2 - shot.height / 2;
            shot.fire(x, y, dx, dy);
            playerShots.add(shot);
            nextFireTime = now + FIRING_INTERVAL;
        }
    }

    private final RemovalHandler<BaseShot> shotRemovalHandler = new RemovalHandler<BaseShot>() {
        public void onRemove (BaseShot shot) {
            //
        }
    };

    private void doPlayerHit() {
        setState(PLAYER_DEAD);
        restartLevelTime = now + 10;
    }

    // -------- collisions

    private void checkForCollisions () {
        checkMobileMobileCollisions();
        removeMarkedMobiles();

        if (state == PLAYING && player.inCollision) {
            doPlayerHit();
        }
    }

    private void checkMobileMobileCollisions () {
        BulletManager[] bulletManagers = getBulletManagers();
        for(int i = 0; i < bulletManagers.length; i++)
            bulletManagers[i].collide(player);
        Colliders.collide(alienShip, playerShots, playerShotCollisionHandler);
    }

    private void removeMarkedMobiles () {
        Colliders.removeOutOfBounds(shotPool, playerShots, roomBounds);
        Colliders.removeMarkedCollisions(shotPool, playerShots, shotRemovalHandler);
    }

    private void clipBounds () {
        player.x = Math.max(0, Math.min(roomBounds.width - player.width, player.x));
        player.y = Math.max(0, Math.min(roomBounds.height - player.height, player.y));

        alienShip.x = Math.max(0, Math.min(roomBounds.width - alienShip.width, alienShip.x));
        alienShip.y = Math.max(roomBounds.height - 2 * alienShip.height,
                Math.min(roomBounds.height - 2 * alienShip.height / 3, alienShip.y));
    }

    private ColliderHandler<AlienShip, PlayerShot> playerShotCollisionHandler = new ColliderHandler<AlienShip, PlayerShot>() {

        public void onCollision(AlienShip t, PlayerShot u) {
            if(t.hit(1)) {
                setState(ALIEN_DEAD);
            }
            u.inCollision = true;
        }

    };

    // -------- getters / setters

    public BulletManager[] getBulletManagers() {
        return alienShip.getBulletManagers();
    }

    public void pause () {
        isPaused = true;
        pausedTime = 0.0f;
    }

    public void resume () {
        isPaused = false;
    }

    public boolean isPaused () {
        return isPaused;
    }

    public float getPausedTime () {
        return pausedTime;
    }

    public int getState () {
        return state;
    }

    private void setState (int newState) {
        state = newState;
        stateTime = 0.0f;
    }

    public float getStateTime () {
        return stateTime;
    }

    public Player getPlayer () {
        return player;
    }

    public Array<PlayerShot> getPlayerShots () {
        return playerShots;
    }

    public AlienShip getAlienShip() {
        return alienShip;
    }

}
