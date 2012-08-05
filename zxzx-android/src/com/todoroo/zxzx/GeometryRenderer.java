package com.todoroo.zxzx;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.todoroo.zxzx.general.GameObject;

public class GeometryRenderer {

    private ShapeRenderer shapeRenderer;

    public GeometryRenderer(OrthographicCamera camera) {
        shapeRenderer = new ShapeRenderer(500);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    public void render(GameObject go) {
        if(go.geometry == null)
            return;

        shapeRenderer.begin(ShapeType.FilledRectangle);
        shapeRenderer.setColor(1, 0, 0, 0.5f);

        for(Rectangle rect : go.geometry.getRuns()) {
            shapeRenderer.filledRect(go.x + rect.x,
                    go.y + rect.y, rect.width, rect.height);
        }

        shapeRenderer.end();
    }

}
