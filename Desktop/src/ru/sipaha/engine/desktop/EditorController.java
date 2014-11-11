package ru.sipaha.engine.desktop;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.EngineUnit;
import ru.sipaha.engine.graphics.Camera;
import ru.sipaha.engine.utils.Array;

/**
 * Created on 12.11.2014.
 */

public class EditorController implements InputProcessor {

    private Array<EngineUnit> selection = new Array<>(EngineUnit.class);
    private EditorRenderLayer editorLayer;
    private int oldX, oldY;
    private final Vector2 downPoint = new Vector2();
    private final Vector2 currentPoint = new Vector2();
    private boolean selectMode = false;

    public EditorController(EditorRenderLayer layer) {
        this.editorLayer = layer;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                downPoint.set(editorLayer.camera.unproject(screenX, screenY));
                selectMode = true;
                break;
            case Input.Buttons.RIGHT:
                oldX = screenX;
                oldY = screenY;
                break;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(selectMode) {
            currentPoint.set(editorLayer.camera.unproject(screenX, screenY));
            editorLayer.selectionBounds.set(downPoint, currentPoint);
        } else {
            int deltaX = oldX - screenX;
            int deltaY = screenY - oldY;
            editorLayer.camera.moveWithZoom(deltaX, deltaY);
            oldX = screenX;
            oldY = screenY;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        switch (pointer) {
            case Input.Buttons.LEFT:
                selectMode = false;
                editorLayer.selectionBounds.reset();
                break;
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }



    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
