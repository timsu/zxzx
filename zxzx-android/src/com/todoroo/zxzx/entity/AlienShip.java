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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.todoroo.zxzx.BulletManager;
import com.todoroo.zxzx.general.GameObject;

public class AlienShip extends GameObject {

    private TextureRegion sprite;

    private BulletManager bulletManager;

    private String[] bulletPattern;

    private static final float SPEED = 40.0f;

    private Random random = new Random();

    public AlienShip(BulletManager bulletManager, TextureRegion sprite, String[] bulletPatternFiles) {
        this.bulletManager = bulletManager;
        this.sprite = sprite;

        bulletPattern = new String[bulletPatternFiles.length];
        for(int i = 0; i < bulletPattern.length; i++)
            bulletPattern[i] = Gdx.files.internal("bulletml/" + bulletPatternFiles[i]).readString();

        bulletManager.loadBulletML(bulletPattern[0]);
    }

    //

    private float bulletSwitchCounter = 0;
    private int bulletSwitchIndex = 0;

    private float velocityChangeCounter = 0;
    private float velocityChangeTarget = 0;
    private float velocityX = 3, velocityY = 0;

    @Override
    public void update(float delta) {

        bulletSwitchCounter += delta;
        velocityChangeCounter += delta;

        if (bulletSwitchCounter > 10) {
            bulletSwitchIndex = (bulletSwitchIndex + 1) % bulletPattern.length;
            bulletManager.loadBulletML(bulletPattern[bulletSwitchIndex]);
            bulletSwitchCounter = 0;
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
    };
}
