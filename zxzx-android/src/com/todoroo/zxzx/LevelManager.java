package com.todoroo.zxzx;

import android.graphics.Point;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.todoroo.zxzx.entity.AlienShip;
import com.todoroo.zxzx.general.CollisionGeometry;

public class LevelManager {

    abstract public static class LevelData {
        public int level;
        public float parTime = 10f;

        public LevelData(int level) {
            this.level = level;
        }

        abstract public AlienShip createAlienShip();
    }

    private final LevelData[] levels;

    public LevelManager() {
        levels = new LevelData[] {
            new LevelData(1) {
                @Override
                public AlienShip createAlienShip() {
                    AlienShip alienShip = new AlienShip(Assets.alien1,
                            new String[] { "[Ikaruga]_r1_mdl.xml", "[Ikaruga]_drc2.xml" },
                            new Point[] { new Point(136, 200), new Point(376, 200) },
                            50);

                    Array<Rectangle> rectangles = new Array<Rectangle>();
                    rectangles.add(new Rectangle(30, 150, 60, 144));
                    rectangles.add(new Rectangle(415, 150, 60, 144));
                    rectangles.add(new Rectangle(220, 130, 66, 144));
                    rectangles.add(new Rectangle(115, 195, 270, 92));
                    rectangles.add(new Rectangle(30, 250, 445, 116));
                    rectangles.add(new Rectangle(70, 340, 380, 70));
                    alienShip.geometry = new CollisionGeometry(rectangles);

                    return alienShip;
                }
            },

            new LevelData(2) {
                @Override
                public AlienShip createAlienShip() {
                    AlienShip alienShip = new AlienShip(Assets.alien2,
                            new String[] { "[G_DARIUS]_homing_laser.xml", "[daiouzyou]_r1_boss_1.xml" },
                            new Point[] { new Point(71, 271), new Point(437, 271) },
                            70);

                    Array<Rectangle> rectangles = new Array<Rectangle>();
                    rectangles.add(new Rectangle(184, 126, 137, 291));
                    rectangles.add(new Rectangle(23, 271, 458, 124));
                    alienShip.geometry = new CollisionGeometry(rectangles);

                    return alienShip;
                }
            },
        };
    }

    public AlienShip initAlienShip(int level) {
        LevelData levelData = levels[level];

        AlienShip alienShip = levelData.createAlienShip();

        return alienShip;
    }

    public boolean wonTheGame(int level) {
        return level >= levels.length;
    }

}
