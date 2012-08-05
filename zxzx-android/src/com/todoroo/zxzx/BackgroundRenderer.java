package com.todoroo.zxzx;

import java.util.Random;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.todoroo.zxzx.general.Config;

public class BackgroundRenderer {

    private ShapeRenderer shapeRenderer;

    private OrthographicCamera camera;

    private static final float BG_SCROLL_VELOCITY = Config.asFloat("Background.velocity", 80f);
    private static final int BG_WAVE_COUNT = Config.asInt("Background.waves", 80);

    private float[] waveX = new float[BG_WAVE_COUNT];
    private float[] waveY = new float[BG_WAVE_COUNT];
    private int[] waveType = new int[BG_WAVE_COUNT];

    private Random random = new Random();

    public BackgroundRenderer(OrthographicCamera camera) {
        shapeRenderer = new ShapeRenderer(500);
        shapeRenderer.setProjectionMatrix(camera.combined);
        this.camera = camera;

        for(int i = 0; i < BG_WAVE_COUNT; i++) {
            waveX[i] = random.nextInt() % ((int) camera.viewportWidth);
            waveY[i] = random.nextInt() % ((int) camera.viewportHeight);
            waveType[i] = i % 2;
        }
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        for(int i = 0; i < BG_WAVE_COUNT; i++) {
            if(waveY[i] < 0 || waveY[i] > camera.viewportHeight)
                continue;

            TextureRegion texture = waveType[i] == 0 ? Assets.water1 : Assets.water2;
            spriteBatch.draw(texture, waveX[i], waveY[i]);
        }
        spriteBatch.end();
    }

    public void update(float delta) {
        for(int i = 0; i < BG_WAVE_COUNT; i++) {
            waveY[i] = waveY[i] - delta * BG_SCROLL_VELOCITY;
            if(waveY[i] < 0)
                waveY[i] += camera.viewportHeight;
        }
    }
}
