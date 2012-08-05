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


public class SoundManager implements WorldListener {

	private float now = 0;
	private float lastAlienFireTime = 0;
	private float lastAlienHitTime = 0;
	private float lastPlayerHitTime = 0;

    public void onPlayerFired() {

    }

    public void onPlayerHit() {
        if (lastPlayerHitTime != now) {
            Assets.playSound(Assets.explosion1);
            lastPlayerHitTime = now;
        }
    }

    public void onPlayerSpawned() {
        Assets.playSound(Assets.extend);
    }

    public void onWorldReset() {
    }

    public void onAlienFired() {
        if (now - lastAlienFireTime > 0.2f) {
            Assets.shot.play(0.1f);
            lastAlienFireTime = now;
        }
    }

    public void onAlienHit() {
        if (lastAlienHitTime != now) {
            Assets.playSound(Assets.damage);
            lastAlienHitTime = now;
        }
    }

    public void onAlienDestroyed() {
        Assets.playSound(Assets.destroyed);
    }

    public void onLevelChange(int newLevel) {
    }

    public void update (float delta) {
        now += delta;
    }

}
