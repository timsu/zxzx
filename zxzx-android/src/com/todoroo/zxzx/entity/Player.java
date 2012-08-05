package com.todoroo.zxzx.entity;
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



import com.todoroo.zxzx.Assets;
import com.todoroo.zxzx.general.Config;
import com.todoroo.zxzx.general.GameObject;

public class Player extends GameObject {

	public static final int FLYING = INACTIVE + 1;
	public static final int FLYING_LEFT = FLYING + 1;
	public static final int FLYING_RIGHT = FLYING_LEFT + 1;

	private static final float SPEED = Config.asFloat("Player.accel", 40f);
	private static final float DRAG = Config.asFloat("Player.drag", 0.95f);

	private float ax = 0, ay = 0, vx = 0, vy = 0;

	public Player () {
		width = Assets.playerWidth;
		height = Assets.playerHeight;
		geometry = Assets.playerGeometry;
	}

	@Override
	public void update (float delta) {
		if (vx > 2f) {
			if (state != FLYING_RIGHT) {
				setState(FLYING_RIGHT);
			}
		} else if (vx < -2f) {
			if (state != FLYING_LEFT) {
				setState(FLYING_LEFT);
			}
		} else {
			setState(FLYING);
		}
		stateTime += delta;
		float d = delta * SPEED;

		vx = vx * (DRAG) + ax * d;
		vy = vy * (DRAG) + ay * d;

		x += vx;
		y += vy;
	}

	public void setController (float x, float y) {
		ax = x;
		ay = y;
	}
}
