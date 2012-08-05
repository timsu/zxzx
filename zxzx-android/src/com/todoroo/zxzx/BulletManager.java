/*
 * $Id: GameManager.java,v 1.5 2001/06/03 00:19:12 ChoK Exp $
 *
 * Copyright 2001 Kenta Cho. All rights reserved.
 */
package com.todoroo.zxzx;

import java.io.IOException;
import java.io.StringReader;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.todoroo.zxzx.entity.AlienShip;
import com.todoroo.zxzx.general.GameObject;


/**
 * Handle game status.
 *
 * @version $Revision: 1.5 $
 */
public class BulletManager
{

    private final int BULLET_NOT_EXIST = BulletImpl.NOT_EXIST;
    private final int BULLET_MAX = 256;
    private BulletImpl[] bullet = new BulletImpl[BULLET_MAX];
    private int bulletIndex = 0;
    private final int ACTION_MAX = 1024;
    private ActionImpl[] action = new ActionImpl[ACTION_MAX];
    private int actionIndex = 0;

    private int screenHeight = 0;
	private int screenWidth = 0;

    // BulletML handler.
    private IActionElmChoice[] topAction;
    private BulletImpl topBullet;
    private int shotCnt = 0;

    private AlienShip ship;
    private GameObject player;
    private float offsetX, offsetY;

    /**
     * @param context
     * @param screenHeight
     * @param screenWidth
     */
    public BulletManager(int screenWidth, int screenHeight)
    {
    	this.screenWidth = screenWidth;
    	this.screenHeight = screenHeight;

    	initBullets();
	}

    /**
     * @param y
     * @param x
     *
     */
    public void initGameObjects(AlienShip ship, GameObject player, float offsetX, float offsetY)
    {
        this.ship = ship;
        this.player = player;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void initBullets() {

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

    public void loadBulletML(FileHandle fileHandle) {
        String stringData = fileHandle.readString();
        loadBulletML(stringData);
    }

    /**
     * Load ML definition file.
     *
     * @param The document name.
     */
    public void loadBulletML(String mlContents)
    {
        try
        {
            Document doc = getDocument(mlContents);

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
    Document getDocument(String mlContents) throws IOException, ParserConfigurationException, FactoryConfigurationError, SAXException
    {
    	Document document = null;

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        document = builder.parse(new InputSource(new StringReader(mlContents)));

    	 return document;
    }

    /**
     * @return
     *
     */
    private boolean addBullets()
    {
        if ((topBullet != null) && (topBullet.x != BulletImpl.NOT_EXIST) && !topBullet.isAllActionFinished())
        {
            return true;
        }

        ship.setState(AlienShip.MOVING);

        shotCnt--;

        if (shotCnt > 0)
        {
            return false;
        }

        shotCnt = 60;
        topBullet = getBulletImplInstance();

        if (topBullet == null)
        {
            return false;
        }

        ship.setState(AlienShip.SHOOTING);

        topBullet.set(topAction, (int)getShipX(), (int)getShipY(), 0);
        topBullet.ix = topBullet.x;
        topBullet.iy = topBullet.y;
        topBullet.speed = 0;
        topBullet.direction = 0;

        return true;
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
    public boolean update()
    {
        boolean shot = false;

        if(topAction == null)
            return shot;

        if(ship.state != AlienShip.DEATH)
            shot = addBullets();

        moveBullets();

        return shot;
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

	public float getShipX() {
	    return (ship.x + offsetX) * 16;
	}

	public float getShipY() {
	    return (ship.y + offsetY) * 16;
	}

	public float getPlayerX() {
	    return (player.x + player.width / 2) * 16;
	}

	public float getPlayerY() {
	    return (player.y + player.height / 2) * 16;
	}

	private Rectangle rectangle = new Rectangle();

    public void collide(GameObject go) {
        for (int i = BULLET_MAX - 1; i >= 0; i--) {
            if (bullet[i].x != BULLET_NOT_EXIST) {
                rectangle.x = (bullet[i].x >> 4) - 2;
                rectangle.y = (bullet[i].y >> 4) - 2;
                rectangle.height = 4;
                rectangle.width = 4;

                if(go.intersects(rectangle)) {
                    bullet[i].x = BULLET_NOT_EXIST;
                    go.inCollision = true;
                    break;
                }
            }
        }

    }

    public void clear() {
        for (int i = BULLET_MAX - 1; i >= 0; i--)
            bullet[i].x = BULLET_NOT_EXIST;

    }

}
