package com.todoroo.zxzx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.todoroo.zxzx.entity.AlienShip;
import com.todoroo.zxzx.general.CollisionGeometry;

public class LevelManager {

    public static class LevelData {
        public int level;
        public TextureRegion alienTexture;
        public int alienWidth;
        public int alienHeight;
        public String[] bulletSequences;

        public LevelData(int level, TextureRegion alienTexture, int alienWidth, int alienHeight,
                String[] bulletSequences) {
            this.level = level;
            this.alienTexture = alienTexture;
            this.alienWidth = alienWidth;
            this.alienHeight = alienHeight;
            this.bulletSequences = bulletSequences;
        }
    }

    private final LevelData[] levels;

    public LevelManager() {
        levels = new LevelData[] {
            new LevelData(1, Assets.alien1, 430, 130, new String[] { "grow.xml", "[Ikaruga]_drc2.xml" })
        };
    }

    public AlienShip initAlienShip(int level) {
        LevelData levelData = levels[level];

        AlienShip alienShip = new AlienShip(levelData.alienTexture,
                levelData.bulletSequences);
        alienShip.width = levelData.alienTexture.getRegionWidth();
        alienShip.height = levelData.alienTexture.getRegionHeight();

        Array<Rectangle> rectangles = new Array<Rectangle>();
        float x = (alienShip.width - levelData.alienWidth) / 2.0f;
        float y = (alienShip.height- levelData.alienHeight) / 2.0f;
        float w = alienShip.width - 2 * x;
        float h = alienShip.height - 2 * y;
        Rectangle r = new Rectangle(x, y, w, h);
        rectangles.add(r);
        alienShip.geometry = new CollisionGeometry(rectangles);

        return alienShip;
    }

}
