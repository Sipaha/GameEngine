package ru.sipaha.engine.desktop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.graphics.Camera;
import ru.sipaha.engine.graphics.renderlayers.RenderLayer;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.structures.Bounds;

/**
 * Created on 12.11.2014.
 */

public class EditorRenderLayer extends RenderLayer {
    private static final float AXIS_LINE_LENGTH = 50;
    private static final float AXIS_CENTER_SIZE = 5;

    private final Camera gameCamera;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private Color yAxisColor = new Color(129/255f, 194/255f, 79/255f, 1);
    private Color xAxisColor = new Color(176/255f, 70/255f, 52/255f, 1);
    private Color centerAxisColor = new Color(82/255f, 160/255f, 1, 1);

    private Color selectionBoxBorderColor = new Color(1,1,1,0.6f);
    private Color selectionBoxFillColor = new Color(158/255f,196/255f,1,0.3f);

    private Bounds selectionBounds;
    private Array<GameObject> selectedUnits;

    public EditorRenderLayer(Camera gameCamera) {
        super("EditorLayer");
        setOrder(1000);
        this.gameCamera = gameCamera;
    }

    @Override
    protected void render() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawAxisLines(0, 0);
        for(GameObject g : selectedUnits) {
            drawAxisLines(g.transform.x.get(), g.transform.y.get());
        }
        if(selectionBounds != null && !selectionBounds.isEmpty()) {
            drawSelectionBox();
        }
        shapeRenderer.end();
    }

    private void drawAxisLines(float x, float y) {
        Vector2 screenPos = gameCamera.project(x, y);
        shapeRenderer.setColor(xAxisColor);
        shapeRenderer.rect(screenPos.x, screenPos.y-1f, AXIS_LINE_LENGTH, 2);
        shapeRenderer.setColor(yAxisColor);
        shapeRenderer.rect(screenPos.x-1f, screenPos.y, 2, AXIS_LINE_LENGTH);
        shapeRenderer.setColor(centerAxisColor);
        shapeRenderer.rect(screenPos.x-AXIS_CENTER_SIZE/2, screenPos.y-AXIS_CENTER_SIZE/2,
                                                    AXIS_CENTER_SIZE, AXIS_CENTER_SIZE);
    }

    private void drawSelectionBox() {
        Bounds bounds = camera.unproject(selectionBounds);
        float width = bounds.getWidth();
        float height = bounds.getHeight();
        shapeRenderer.setColor(selectionBoxFillColor);
        shapeRenderer.rect(bounds.min.x+1,bounds.min.y+1,width-2,height-2);
        shapeRenderer.setColor(selectionBoxBorderColor);
        shapeRenderer.rect(bounds.min.x-0.5f,bounds.min.y, 1, height);
        shapeRenderer.rect(bounds.min.x,bounds.max.y-0.5f, width, 1);
        shapeRenderer.rect(bounds.min.x,bounds.min.y-0.5f, width, 1);
        shapeRenderer.rect(bounds.max.x-0.5f,bounds.min.y, 1, height);
    }

    public void setSelectionBounds(Bounds bounds) {
        selectionBounds = bounds;
    }

    public void setSelectedUnits(Array<GameObject> units) {
        this.selectedUnits = units;
    }

    public Camera getGameCamera() {
        return camera;
    }
}
