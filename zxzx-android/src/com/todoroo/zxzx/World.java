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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.todoroo.zxzx.entity.Player;
import com.todoroo.zxzx.entity.PlayerShot;
import com.todoroo.zxzx.general.Config;
import com.todoroo.zxzx.general.GameObject;

/** The <code>World</code> is the representation of the game world of <b>Very Angry Robots</b>. It knows nothing about how it will
 * be displayed, neither does it know about how the player is controlled, particle effects, sounds, nor anything else. It purely
 * knows about the {@link Player}, {@link Robot}s and the walls of the room that the player is in.
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

	private static final int MAX_PLAYER_SHOTS = Config.asInt("Player.maxShots", 4);
	private static final float FIRING_INTERVAL = Config.asFloat("Player.firingInterval", 0.25f);
	public static final float ROOM_TRANSITION_TIME = Config.asFloat("Global.roomTransitionTime", 0.5f);

	// Game states.
	public static final int RESETTING = 1;
	public static final int ENTERED_ROOM = 2;
	public static final int PLAYING = 3;
	public static final int PLAYER_DEAD = 4;

	private final Pool<PlayerShot> shotPool;
	private final Rectangle roomBounds;
	private float playingTime;
	private float nextFireTime;
	private Vector2 playerPos;
	private float now;
	private Array<Rectangle> doorRects;
	private Array<Rectangle> wallRects;
	private int doorPosition;
	private Player player;
	private Array<PlayerShot> playerShots;
	private int state;
	private float stateTime;
	private Color robotColor;
	private boolean isPaused;
	private float pausedTime;

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

	public Color getRobotColor () {
		return robotColor;
	}

	public int getDoorPosition () {
		return doorPosition;
	}

	public Array<Rectangle> getDoorRects () {
		return doorRects;
	}

	public Array<Rectangle> getWallRects () {
		return wallRects;
	}

	public Rectangle getRoomBounds () {
		return roomBounds;
	}

	public Player getPlayer () {
		return player;
	}

	public Array<PlayerShot> getPlayerShots () {
		return playerShots;
	}

	/** Constructs a new {@link World}. */
	public World() {
		roomBounds = new Rectangle(0, 0, 800, 1280);
		player = new Player();
		playerPos = new Vector2();

		shotPool = new Pool<PlayerShot>(MAX_PLAYER_SHOTS, MAX_PLAYER_SHOTS) {
			@Override
			protected PlayerShot newObject () {
				return new PlayerShot();
			}
		};
	}

	/** Resets the {@link World} to its starting state. */
	public void reset () {
		setState(RESETTING);
	}

	/** Called when the {@link World} is to be updated.
	 *
	 * @param delta the time in seconds since the last render. */
	public void update (float delta) {
		if (!isPaused) {
			now += delta;
			stateTime += delta;
			switch (state) {
			case PLAYING:
				updatePlaying(delta);
				break;
			}
		} else {
			pausedTime += delta;
		}
	}

    private void updateMobiles (float delta) {
        update(playerShots, delta);
    }

    private void updatePlaying (float delta) {
        player.update(delta);
        updateMobiles(delta);
    }

	private void update (Array<? extends GameObject> gos, float delta) {
		for (GameObject go : gos) {
			go.update(delta);
		}
	}

}
