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
import com.todoroo.zxzx.World;
import com.todoroo.zxzx.general.GameObject;

public class AlienShip extends GameObject {

    public static int MOVING = INACTIVE + 1;
    public static int SHOOTING = INACTIVE + 2;
    public static int DEATH = INACTIVE + 3;

    private TextureRegion sprite;

    private BulletManager[] bulletManagers;

    private String[] bulletPatterns;

    public float speed = 30.0f;

    private Random random = new Random();

    private int alienHealth, maxAlienHealth;

    private Point[] bulletSources;

    public AlienShip(TextureRegion sprite, String[] bulletPatternFiles,
            Point[] bulletSources, int health) {
        this.sprite = sprite;
        this.width = sprite.getRegionWidth();
        this.height = sprite.getRegionHeight();
        this.bulletSources = bulletSources;

        bulletPatterns = new String[bulletPatternFiles.length];
        for(int i = 0; i < bulletPatterns.length; i++)
            bulletPatterns[i] = Gdx.files.internal("bulletml/" + bulletPatternFiles[i]).readString();

        maxAlienHealth = alienHealth = health;
    }

    public void initBulletManagers(World world, Rectangle roomBounds) {
        bulletManagers = new BulletManager[bulletSources.length];
        for(int i = 0; i < bulletManagers.length; i++) {
            bulletManagers[i] = new BulletManager((int)(roomBounds.width),
                    (int)(roomBounds.height));
            bulletManagers[i].initGameObjects(this, world.getPlayer(), bulletSources[i].x,
                    bulletSources[i].y);
        }
    }

    public void loadBulletPattern() {
        int i = 0;
        for(; i < Math.min(2, bulletManagers.length); i++)
            bulletManagers[i].loadBulletML(bulletPatterns[bulletSwitchIndex]);

        for(; i < bulletManagers.length; i++)
            bulletManagers[i].loadBulletML(bulletPatterns[(bulletSwitchIndex + 1) % bulletPatterns.length]);
    }

    //

    private float bulletSwitchCounter = 100;
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
            loadBulletPattern();
            bulletSwitchCounter = 0;
        }

        if(state == DEATH)
            return;

        if(state == SHOOTING) {
            return;
        }

        if (velocityChangeCounter > velocityChangeTarget) {
            velocityChangeCounter = 0;
            velocityChangeTarget = 2 + 3 * random.nextFloat();
            velocityX = (3 + 3 * random.nextFloat()) * -Math.signum(velocityX);
            velocityY = -3 + 6 * random.nextFloat();
        }

        float d = delta * speed;
        x += velocityX * d;
        y += velocityY * d;
    }

    /**
     * @return true if new bullets were shot
     */
    public boolean updateBulletManagers() {
        boolean shot = false;
        for(BulletManager bm : bulletManagers)
            shot = bm.update() || shot;
        return shot;
    }

    /**
     * Check if any of our bullets hit the player
     * @param player
     */
    public void checkBulletCollision(GameObject player) {
        for(int i = 0; i < bulletManagers.length; i++)
            bulletManagers[i].collide(player);
    }

    public TextureRegion getSprite() {
        return sprite;
    }

    public BulletManager[] getBulletManagers() {
        return bulletManagers;
    }

    /**
     * mark a hit on the alien
     *
     * @param damage
     * @return true if alien is dead
     */
    public boolean hit(int damage) {
        alienHealth -= damage;
        if(alienHealth <= 0) {
            setState(DEATH);
            return true;
        }
        return false;
    }

    public float getAlienHealthPercentage() {
        return alienHealth * 1.0f / maxAlienHealth;
    }
}
