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

package com.todoroo.zxzx.entity;

import java.util.Random;

import android.graphics.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.todoroo.zxzx.BulletManager;
import com.todoroo.zxzx.general.GameObject;

public class AlienShip extends GameObject {

    public static int MOVING = INACTIVE + 1;
    public static int SHOOTING = INACTIVE + 2;

    private TextureRegion sprite;

    private BulletManager[] bulletManagers;

    private String[] bulletPatterns;

    private static final float SPEED = 30.0f;

    private Random random = new Random();

    public AlienShip(TextureRegion sprite, String[] bulletPatternFiles) {
        this.sprite = sprite;

        bulletPatterns = new String[bulletPatternFiles.length];
        for(int i = 0; i < bulletPatterns.length; i++)
            bulletPatterns[i] = Gdx.files.internal("bulletml/" + bulletPatternFiles[i]).readString();
    }

    public void initBulletManagers(Rectangle roomBounds) {
        Point[] bulletSources = new Point[] { new Point(136, 200), new Point(376, 200) };

        bulletManagers = new BulletManager[bulletSources.length];
        for(int i = 0; i < bulletManagers.length; i++) {
            bulletManagers[i] = new BulletManager((int)(roomBounds.width * 16),
                    (int)(roomBounds.height * 16));
            bulletManagers[i].loadBulletML(bulletPatterns[0]);
            bulletManagers[i].initGameObject(this, bulletSources[i].x,
                    bulletSources[i].y);
        }
    };

    //

    private float bulletSwitchCounter = 0;
    private int bulletSwitchIndex = 0;

    private float velocityChangeCounter = 0;
    private float velocityChangeTarget = 0;
    private float velocityX = 3, velocityY = 0;

    @Override
    public void update(float delta) {
        stateTime += delta;

        bulletSwitchCounter += delta;
        velocityChangeCounter += delta;

        if (bulletSwitchCounter > 10) {
            bulletSwitchIndex = (bulletSwitchIndex + 1) % bulletPatterns.length;
            for(int i = 0; i < bulletManagers.length; i++)
                bulletManagers[i].loadBulletML(bulletPatterns[bulletSwitchIndex]);
            bulletSwitchCounter = 0;
        }

        for(int i = 0; i < bulletManagers.length; i++)
            bulletManagers[i].update();

        if(state == SHOOTING) {
            return;
        }

        if (velocityChangeCounter > velocityChangeTarget) {
            velocityChangeCounter = 0;
            velocityChangeTarget = 2 + 3 * random.nextFloat();
            velocityX = (3 + 3 * random.nextFloat()) * -Math.signum(velocityX);
            velocityY = -3 + 6 * random.nextFloat();
        }

        float d = delta * SPEED;
        x += velocityX * d;
        y += velocityY * d;
    }

    public TextureRegion getSprite() {
        return sprite;
    }

    public BulletManager[] getBulletManagers() {
        return bulletManagers;
    }
}
