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

import com.badlogic.gdx.utils.Array;

class WorldNotifier implements WorldListener {

	private final Array<WorldListener> listeners;

	public WorldNotifier () {
		listeners = new Array<WorldListener>();
	}

	public void addListener (WorldListener listener) {
		listeners.add(listener);
	}
    public void onPlayerFired() {
        for (WorldListener listener : listeners)
            listener.onPlayerFired();
    }

    public void onPlayerHit() {
        for (WorldListener listener : listeners)
            listener.onPlayerHit();
    }

    public void onPlayerSpawned() {
        for (WorldListener listener : listeners)
            listener.onPlayerSpawned();
    }

    public void onWorldReset() {
        for (WorldListener listener : listeners)
            listener.onWorldReset();
    }

    public void onAlienFired() {
        for (WorldListener listener : listeners)
            listener.onAlienFired();
    }

    public void onAlienHit() {
        for (WorldListener listener : listeners)
            listener.onAlienHit();
    }

    public void onAlienDestroyed() {
        for (WorldListener listener : listeners)
            listener.onAlienDestroyed();
    }

    public void onLevelChange(int newLevel) {
        for (WorldListener listener : listeners)
            listener.onLevelChange(newLevel);
    }
}
