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
        rectangles.add(new Rectangle(30, 140, 60, 154));
        rectangles.add(new Rectangle(415, 140, 60, 154));
        rectangles.add(new Rectangle(220, 120, 66, 154));
        rectangles.add(new Rectangle(115, 195, 270, 92));
        rectangles.add(new Rectangle(30, 250, 445, 116));
        rectangles.add(new Rectangle(70, 340, 380, 70));
        alienShip.geometry = new CollisionGeometry(rectangles);

        return alienShip;
    }

}
