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
import com.todoroo.zxzx.entity.Player;
import com.todoroo.zxzx.general.ParticleManager;

class ParticleAdapter implements WorldListener {

	final int PARTICLES = 32;

	final private World world;
	final private ParticleManager particleManager;

	public ParticleAdapter (World world, ParticleManager particleManager) {
		this.world = world;
		this.particleManager = particleManager;
	}

	public void update (float delta) {
		particleManager.update(delta);
	}


	public void onEnteredRoom (float time, int robots) {
		particleManager.clear();
	}

	public void onPlayerHit () {
		Player player = world.getPlayer();
		float x = player.x + player.width / 2;
		float y = player.y + player.height / 2;
		particleManager.add(x, y, 2 * PARTICLES, Color.WHITE);
	}

	public void onPlayerFired () {
	}

	public void onPlayerSpawned () {
	}

	public void onWorldReset () {
	}

}
