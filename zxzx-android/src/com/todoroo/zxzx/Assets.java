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

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.todoroo.zxzx.general.CollisionGeometry;
import com.todoroo.zxzx.general.Config;


public class Assets {

    private static final String FLYUP_FONT = Config.asString("Global.flyupFont", "ocr_a_small.fnt");
    private static final String SCORE_FONT = Config.asString("Global.scoreFont", "wellbutrin.fnt");
    private static final String TEXT_FONT = Config.asString("Global.textFont", "ocr_a.fnt");

    private static final float PLAYER_FRAME_DURATION = Config.asFloat("Player.frameDuration", 0.1f);

	private static TextureAtlas atlas;

    public static AtlasRegion pureWhiteTextureRegion;
    public static TextureRegion playerFly1;
	public static TextureRegion playerFly2;
	public static TextureRegion playerFly3;
	public static TextureRegion playerLeft;
	public static TextureRegion playerRight;
	public static TextureRegion playerShot;
	public static TextureRegion alienShot;
	public static TextureRegion water1;
	public static TextureRegion water2;
	public static TextureRegion alien1;
	public static TextureRegion alien2;

	public static Animation playerAnimation;
	public static Animation playerDeathAnimation;

    public static BitmapFont scoreFont;
    public static BitmapFont textFont;
    public static BitmapFont flyupFont;

    public static Sound damage;
    public static Sound destroyed;
    public static Sound explosion1;
    public static Sound explosion2;
    public static Sound grz;
    public static Sound extend;
    public static Sound shot;

	public static float playerWidth;
	public static float playerHeight;
	public static float playerShotWidth;
	public static float playerShotHeight;

	public static CollisionGeometry playerGeometry;

	public static void load () {
		String textureDir = "sprites";
		String textureFile = textureDir + "/pack";
		atlas = new TextureAtlas(Gdx.files.internal(textureFile), Gdx.files.internal(textureDir));
		loadTextures();
		createAnimations();
		loadFonts();
		loadSounds();
		initialiseGeometries();
	}

	private static void loadTextures () {
		playerFly1 = atlas.findRegion("PlayerAnim1");
		playerFly2 = atlas.findRegion("PlayerAnim2");
		playerFly3 = atlas.findRegion("PlayerAnim3");
		playerLeft = atlas.findRegion("PlayerLeft");
		playerRight = atlas.findRegion("PlayerRight");

		water1 = atlas.findRegion("Water1");
		water2 = atlas.findRegion("Water2");

		playerShot = atlas.findRegion("PlayerShot");
		alienShot = atlas.findRegion("AlienShotSmall");

		pureWhiteTextureRegion = atlas.findRegion("8x8");

		Texture enemies = new Texture(Gdx.files.internal("sprites/enemies.png"));
		alien1 = new TextureRegion(enemies, 5, 5, 500, 500);
		alien2 = new TextureRegion(enemies, 518, 5, 500, 500);

		System.err.println("loaded textures");
	}

	private static void createAnimations () {
		playerAnimation = new Animation(PLAYER_FRAME_DURATION, playerFly1, playerFly2, playerFly3);
		playerDeathAnimation = new Animation(0.15f,
		        atlas.findRegion("PlayerDeath1"),
		        atlas.findRegion("PlayerDeath2"),
		        atlas.findRegion("PlayerDeath3"),
		        atlas.findRegion("PlayerDeath4"),
		        atlas.findRegion("PlayerDeath5"),
		        atlas.findRegion("PlayerDeath6"),
		        new TextureRegion(playerFly1, 0, 0, 0, 0));
	}

    private static void loadFonts () {
        String fontDir = "fonts/";

        scoreFont = new BitmapFont(Gdx.files.internal(fontDir + SCORE_FONT), false);
        textFont = new BitmapFont(Gdx.files.internal(fontDir + TEXT_FONT), false);
        flyupFont = new BitmapFont(Gdx.files.internal(fontDir + FLYUP_FONT), false);

        scoreFont.setScale(1.0f);
        textFont.setScale(1.0f);
        flyupFont.setScale(1.0f);
    }

    private static void loadSounds() {
        damage = loadSound("damage.wav");
        destroyed = loadSound("destroyed.wav");
        explosion1 = loadSound("explosion1.wav");
        explosion2 = loadSound("explosion2.wav");
        grz = loadSound("grz.wav");
        extend = loadSound("extend.wav");
        shot = loadSound("shot.wav");
    }

	public static Sound[] loadSounds (String dir) {
		FileHandle dh = Gdx.files.internal("sounds/" + dir);
		FileHandle[] fhs = dh.list();
		List<Sound> sounds = new ArrayList<Sound>();
		for (int i = 0; i < fhs.length; i++) {
			String name = fhs[i].name();
			if (name.endsWith(".ogg")) {
				sounds.add(loadSound(dir + "/" + name));
			}
		}
		Sound[] result = new Sound[0];
		return sounds.toArray(result);
	}

	private static Sound loadSound (String filename) {
		return Gdx.audio.newSound(Gdx.files.internal("sounds/" + filename));
	}

	private static void initialiseGeometries () {
		playerWidth = toWidth(playerFly1);
		playerHeight = toHeight(playerFly1);
		playerShotWidth = toWidth(playerShot);
		playerShotHeight = toHeight(playerShot);

		// Configure player collision geometry.
		Array<Rectangle> playerRectangles = new Array<Rectangle>();
		playerRectangles.add(new Rectangle(8, 36, 49, 12));
		playerRectangles.add(new Rectangle(19, 13, 27, 40));
		playerRectangles.add(new Rectangle(15, 14, 35, 7));
		playerGeometry = new CollisionGeometry(playerRectangles);
	}

	private static float toWidth (TextureRegion region) {
		return region.getRegionWidth();
	}

	private static float toHeight (TextureRegion region) {
		return region.getRegionHeight();
	}

	public static void playSound (Sound sound) {
		sound.play(1);
	}
}
