package ru.sipaha.engine.desktop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ru.sipaha.engine.graphics.Camera;
import ru.sipaha.engine.graphics.renderlayers.RenderLayer;
import ru.sipaha.engine.utils.structures.Bounds;

/**
 * Created on 12.11.2014.
 */

public class EditorRenderLayer extends RenderLayer {
    private static final float AXIS_LINE_LENGTH = 100;
    private static final float AXIS_CENTER_SIZE = 10;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private Color yAxisColor = new Color(129/255f, 194/255f, 79/255f, 1);
    private Color xAxisColor = new Color(176/255f, 70/255f, 52/255f, 1);
    private Color centerAxisColor = new Color(82/255f, 160/255f, 1, 1);

    private Color selectionBoxBorderColor = new Color(1,1,1,0.6f);
    private Color selectionBoxFillColor = new Color(158/255f,196/255f,1,0.3f);

    private Bounds selectionBounds;

    public EditorRenderLayer(Camera gameCamera) {
        super("EditorLayer", gameCamera);
        setOrder(1000);
    }

    @Override
    protected void render() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawAxisLines();
        if(selectionBounds != null && !selectionBounds.isEmpty()) {
            drawSelectionBox();
        }
        shapeRenderer.end();
    }

    private void drawAxisLines() {
        shapeRenderer.setColor(xAxisColor);
        shapeRenderer.rect(0.5f, -2, AXIS_LINE_LENGTH, 4);
        shapeRenderer.setColor(yAxisColor);
        shapeRenderer.rect(-2, 0.5f, 4, AXIS_LINE_LENGTH);
        shapeRenderer.setColor(centerAxisColor);
        shapeRenderer.rect(-AXIS_CENTER_SIZE/2,-AXIS_CENTER_SIZE/2, AXIS_CENTER_SIZE, AXIS_CENTER_SIZE);
    }

    private void drawSelectionBox() {
        shapeRenderer.setColor(selectionBoxBorderColor);
        float width = selectionBounds.getWidth();
        float height = selectionBounds.getHeight();
        shapeRenderer.rect(selectionBounds.min.x-1,selectionBounds.min.y-1, 2, height+2);
        shapeRenderer.rect(selectionBounds.min.x-1,selectionBounds.max.y-1, width+2, 2);
        shapeRenderer.rect(selectionBounds.min.x-1,selectionBounds.min.y-1, width+2, 2);
        shapeRenderer.rect(selectionBounds.max.x-1,selectionBounds.min.y-1, 2, height+2);
        shapeRenderer.setColor(selectionBoxFillColor);
        shapeRenderer.rect(selectionBounds.min.x+1,selectionBounds.min.y+1,width-2,height-2);
    }

    public void setSelectionBounds(Bounds bounds) {
        selectionBounds = bounds;
    }

    public Camera getGameCamera() {
        return camera;
    }
}
