/*
 * $Id: BulletImpl.java,v 1.4 2001/05/14 14:21:58 ChoK Exp $
 *
 * Copyright 2001 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.bulletml_demo.noiz;

import jp.gr.java_conf.abagames.bulletml.Bullet;
import jp.gr.java_conf.abagames.bulletml.Direction;
import jp.gr.java_conf.abagames.bulletml.IActionElmChoice;
import jp.gr.java_conf.abagames.bulletml.Speed;
import jp.gr.java_conf.abagames.util.DegUtil;
import jp.gr.java_conf.abagames.util.SCTable;

import com.todoroo.zxzx.BulletManager;
import com.todoroo.zxzx.AbstractBulletRenderer;

/**
 * Bullet implementation.
 *
 * @version $Revision: 1.4 $
 */
public class BulletImpl
{
    public static final int NOT_EXIST = -9999;

    private int SCREEN_WIDTH_16 = 0;
    private int SCREEN_HEIGHT_16 = 0;

    private final int ACTION_MAX = 8;
    private ActionImpl[] action = new ActionImpl[ACTION_MAX];
    private int actionIndex;
    public int x;
    public int y;
    public int px;
    public int py;
    public Direction directionElement;
    public Speed speedElement;
    public float direction;
    public float speed;
    public float mx;
    public float my;
    public int colorIndex;
    private int count;
    private float[] params;

    private BulletManager gameManager;

    public BulletImpl(BulletManager gm)
    {
        gameManager = gm;
        x = NOT_EXIST;

        SCREEN_WIDTH_16 = gm.getScreenWidth() << 4;
        SCREEN_HEIGHT_16 = gm.getScreenHeight()<< 4;

    }

    public void changeAction(ActionImpl bfr, ActionImpl aft)
    {
        for (int i = 0; i < actionIndex; i++)
        {
            if (action[i].equals(bfr))
            {
                action[i] = aft;

                return;
            }
        }
    }

    public void set(IActionElmChoice[] aec, int x, int y, int ci)
    {
        this.x = px = x;
        this.y = py = y;
        mx = my = 0;
        colorIndex = ci & 3;
        count = 0;
        actionIndex = 0;

        for (int i = 0; i < aec.length; i++)
        {
            action[actionIndex] = gameManager.getActionImplInstance();

            if (action[actionIndex] == null)
            {
                break;
            }

            action[actionIndex].set(BulletmlNoizUtil.getActionElm(aec[i]), this);

            float[] actPrms = BulletmlNoizUtil.getActionParams(aec[i], params);

            if (actPrms == null)
            {
                action[actionIndex].setParams(params);
            }
            else
            {
                action[actionIndex].setParams(actPrms);
            }

            actionIndex++;

            if (actionIndex >= ACTION_MAX)
            {
                break;
            }
        }
    }

    public void set(Bullet bullet, int x, int y, int ci)
    {
        directionElement = bullet.getDirection();
        speedElement = bullet.getSpeed();

        IActionElmChoice[] aec = bullet.getActionElm();
        set(aec, x, y, ci);
    }

    public void setParams(float[] prms)
    {
        this.params = prms;
    }

    public float getAimDeg()
    {
        //return (float)DegUtil.getDeg(gameManager.xp - x, gameManager.yp - y)
        // * (float)Math.PI / 128;
        return ((float) DegUtil.getDeg(gameManager.xPosition - x, gameManager.yPosition - y) * 360) / SCTable.TABLE_SIZE;
    }

    public void vanish()
    {
        for (int i = 0; i < actionIndex; i++)
        {
            action[i].vanish();
        }

        x = NOT_EXIST;
    }

    public boolean isAllActionFinished()
    {
        for (int i = 0; i < actionIndex; i++)
        {
            if (action[i].pc != ActionImpl.NOT_EXIST)
            {
                return false;
            }
        }

        return true;
    }

    public void move()
    {
        for (int i = 0; i < actionIndex; i++)
        {
            action[i].move();
        }

        count++;

        //int d = (int)(direction*SCTable.TABLE_SIZE/Math.PI/2);
        int d = (int) ((direction * SCTable.TABLE_SIZE) / 360);
        d &= (SCTable.TABLE_SIZE - 1);

        int mvx = ((int) (speed * SCTable.sintbl[d]) >> 3) + (int) (mx * 32);
        int mvy = ((int) (-speed * SCTable.costbl[d]) >> 3) + (int) (my * 32);
        x += mvx;
        y += mvy;

        if (count < 4)
        {
            px = x - mvx;
            py = y - mvy;
        }
        else if (count < 8)
        {
            px = x - (mvx << 1);
            py = y - (mvy << 1);
        }
        else
        {
            px = x - (mvx << 2);
            py = y - (mvy << 2);
        }

        switch (gameManager.hvStat)
        {
        case 0:

            if ((px < 0) || (px >= SCREEN_WIDTH_16) || (py < -(SCREEN_HEIGHT_16 >> 2)) || (py >= SCREEN_HEIGHT_16))
            {
                vanish();
            }

            break;

        case 1:

            if ((px < 0) || (px >= (SCREEN_WIDTH_16 + (SCREEN_WIDTH_16 >> 2))) || (py < 0) || (py >= SCREEN_HEIGHT_16))
            {
                vanish();
            }

            break;
        }
    }

    public void draw(AbstractBulletRenderer screen)
    {
        screen.drawBullet((x >> 4), (y >> 4), (px >> 4), (py >> 4));
    }

}
