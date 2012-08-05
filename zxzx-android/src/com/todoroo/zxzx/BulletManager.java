/*
 * $Id: GameManager.java,v 1.5 2001/06/03 00:19:12 ChoK Exp $
 *
 * Copyright 2001 Kenta Cho. All rights reserved.
 */
package com.todoroo.zxzx;

import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import jp.gr.java_conf.abagames.bulletml.Action;
import jp.gr.java_conf.abagames.bulletml.Bullet;
import jp.gr.java_conf.abagames.bulletml.Bulletml;
import jp.gr.java_conf.abagames.bulletml.Fire;
import jp.gr.java_conf.abagames.bulletml.IActionElmChoice;
import jp.gr.java_conf.abagames.bulletml.IBulletmlChoice;
import jp.gr.java_conf.abagames.bulletml_demo.noiz.ActionImpl;
import jp.gr.java_conf.abagames.bulletml_demo.noiz.BulletImpl;
import jp.gr.java_conf.abagames.bulletml_demo.noiz.BulletmlNoizUtil;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.util.Log;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.todoroo.zxzx.general.GameObject;


/**
 * Handle game status.
 *
 * @version $Revision: 1.5 $
 */
public class BulletManager
{

    public static class StateData {
        public float controlX = 0;
        public float controlY = 0;
    }

    private final int BULLET_NOT_EXIST = BulletImpl.NOT_EXIST;
    private final int BULLET_MAX = 256;
    private BulletImpl[] bullet = new BulletImpl[BULLET_MAX];
    private int bulletIndex = 0;
    private final int ACTION_MAX = 1024;
    private ActionImpl[] action = new ActionImpl[ACTION_MAX];
    private int actionIndex = 0;

    private int screenHeight = 0;
	private int screenWidth = 0;

    // A curse on three letter variable names!
    public int xPosition;
    public int yPosition;

    private int pxPosition;
    private int pyPosition;
    private final int CLS_WIDTH = 32;

    // BulletML handler.
    private IActionElmChoice[] topAction;
    private BulletImpl topBullet;
    private int shotCnt = 0;

    private StateData state = new StateData();

    /**
     * @param context
     * @param screenHeight
     * @param screenWidth
     */
    public BulletManager(int screenWidth, int screenHeight)
    {
    	this.screenWidth = screenWidth;
    	this.screenHeight = screenHeight;

    	initGame();
	}

    /**
     * @param y
     * @param x
     *
     */
    public void initBullets(float x, float y)
    {
        state.controlX = x;
        state.controlY = y;

        for (int i = 0; i < bullet.length; i++)
        {
            bullet[i] = new BulletImpl(this);
        }

        for (int i = 0; i < action.length; i++)
        {
            action[i] = new ActionImpl(this);
        }

    }

    /**
     *
     * @return
     */
    public BulletImpl getBulletImplInstance()
    {
        for (int i = BULLET_MAX - 1; i >= 0; i--)
        {
            bulletIndex++;
            bulletIndex &= (BULLET_MAX - 1);

            if (bullet[bulletIndex].x == BULLET_NOT_EXIST)
            {
                return bullet[bulletIndex];
            }
        }

        return null;
    }

    /**
     * @return
     */
    public ActionImpl getActionImplInstance()
    {
        for (int i = ACTION_MAX - 1; i >= 0; i--)
        {
            actionIndex++;
            actionIndex &= (ACTION_MAX - 1);

            if (action[actionIndex].pc == ActionImpl.NOT_EXIST)
            {
                return action[actionIndex];
            }
        }

        return null;
    }

    /**
     *
     */
    private void initGame()
    {
        xPosition = (int)state.controlX;
        yPosition = (int)state.controlY;
        pxPosition = xPosition + 1;
        pyPosition = yPosition;
    }

    /**
     * Load ML definition file.
     *
     * @param The document name.
     */
    public void loadBulletML(FileHandle bullets)
    {
        try
        {
            Document doc = getDocument(bullets);

            Bulletml bulletML = new Bulletml(doc);

            // String type = bulletML.getType();
            // NOTE: Do we need to use the type or can we leave it to the defn.

            IBulletmlChoice[] bmc = bulletML.getContent();
            Vector<Action> aecVct = new Vector<Action>();
            BulletmlNoizUtil.clear();

            for (int i = 0; i < bmc.length; i++)
            {
                IBulletmlChoice be = bmc[i];

                if (be instanceof Action)
                {
                    Action act = (Action) be;

                    if (act.getLabel().startsWith("top"))
                    {
                        aecVct.addElement(act);
                    }

                    BulletmlNoizUtil.addAction(act);
                }
                else if (be instanceof Bullet)
                {
                    BulletmlNoizUtil.addBullet((Bullet) be);
                }
                else if (be instanceof Fire)
                {
                    BulletmlNoizUtil.addFire((Fire) be);
                }
            }

            topAction = new IActionElmChoice[aecVct.size()];
            aecVct.copyInto(topAction);
        }
        catch (Exception e)
        {
        	Log.e("loadBulletML", e.toString());

            e.printStackTrace();
        }
    }

    /**
     * Build DOM document from file.
     *
     * @param The file name.
     *
     * @return DOM Document.
     *
     * @throws IOException
     * @throws FactoryConfigurationError
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    Document getDocument(FileHandle handle) throws IOException, ParserConfigurationException, FactoryConfigurationError, SAXException
    {
    	Document document = null;

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        document = builder.parse(handle.read(1024));

    	 return document;
    }

    /**
     *
     */
    private void addBullets()
    {
        if ((topBullet != null) && (topBullet.x != BulletImpl.NOT_EXIST) && !topBullet.isAllActionFinished())
        {
            return;
        }

        shotCnt--;

        if (shotCnt > 0)
        {
            return;
        }

        shotCnt = 60;
        topBullet = getBulletImplInstance();

        if (topBullet == null)
        {
            return;
        }

        topBullet.set(topAction, (int)state.controlX, (int)state.controlY, 0);
        topBullet.speed = 0;
        topBullet.direction = 0;
    }

    /**
     * Move bullet elements.
     *
     * @param The draw canvas
     */
    private void moveBullets()
    {
        for (int i = BULLET_MAX - 1; i >= 0; i--)
        {
            if (bullet[i].x != BULLET_NOT_EXIST)
            {
                bullet[i].move();
            }
        }
    }

    /**
     * Draw bullet elements.
     *
     * @param The draw canvas
     */
    private void drawBullets(AbstractBulletRenderer screen)
    {
        for (int i = BULLET_MAX - 1; i >= 0; i--)
        {
            if (bullet[i].x != BULLET_NOT_EXIST)
            {
                bullet[i].draw(screen);
            }
        }
    }

    /**
     * Update visual items.
     *
     */
    public void update()
    {
        if(topAction == null)
            return;
        addBullets();
        moveBullets();
    }

    /**
     * Draw managed items.
     *
     * @param canvas
     */
    public void draw(AbstractBulletRenderer screen)
    {
	    drawBullets(screen);
    }

    /**
     * Return screen height.
     *
     * @return Screen height.
     */
    public int getScreenHeight()
    {
		return screenHeight;
	}

	/**
	 * Return screen width.
	 *
	 * @return Screen width
	 */
	public int getScreenWidth()
	{
		return screenWidth;
	}

	private Rectangle rectangle = new Rectangle();

    public void collide(GameObject go) {
        for (int i = BULLET_MAX - 1; i >= 0; i--) {
            if (bullet[i].x != BULLET_NOT_EXIST) {
                rectangle.x = (bullet[i].x >> 4) - 3;
                rectangle.y = (bullet[i].y >> 4) - 3;
                rectangle.height = 6;
                rectangle.width = 6;

                if(go.intersects(rectangle)) {
                    go.inCollision = true;
                    break;
                }
            }
        }

    }

}
