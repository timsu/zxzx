package com.todoroo.zxzx.entity;



import com.todoroo.zxzx.Assets;
import com.todoroo.zxzx.general.Config;

public class PlayerShot extends BaseShot {

	private static final float SHOT_SPEED = Config.asFloat("PlayerShot.speed", 31.25f);

	public PlayerShot () {
		width = Assets.playerShotWidth;
		height = Assets.playerShotHeight;
		setShotSpeed(SHOT_SPEED);
	}
}
