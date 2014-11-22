package ru.sipaha.engine.desktop;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.graphics.Camera;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.signals.Signal;
import ru.sipaha.engine.utils.structures.Bounds;

/**
 * Created on 12.11.2014.
 */

public class EditorController implements InputProcessor {

    enum Mode {NONE, SELECT, DOWN, CAMERA_MOVE, UNITS_MOVE}

    public final Signal<Array<GameObject>> onSelect = new Signal<>();
    public final Signal<Array<GameObject>> onChange = new Signal<>();

    private static final int SELECT_MODE_BACKLASH = 30;

    private Array<GameObject> selection = new Array<>(GameObject.class);
    private Iterable<GameObject> editableUnits;
    private final Vector2 prevPoint = new Vector2();
    private final Vector2 currentPoint = new Vector2();
    private Mode mode = Mode.NONE;

    private final Bounds selectionBounds = new Bounds();
    private final Camera gameCamera;

    public EditorController(Camera gameCamera, Iterable<GameObject> editableUnits) {
        this.editableUnits = editableUnits;
        this.gameCamera = gameCamera;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        prevPoint.set(screenX, screenY);
        switch (button) {
            case Input.Buttons.LEFT:
                for(GameObject g : selection) {
                    Vector2 worldPos = gameCamera.unproject(screenX, screenY);
                    Bounds objectBounds = g.getBounds();
                    if(objectBounds != null && objectBounds.pointIn(worldPos)) {
                        setMode(Mode.UNITS_MOVE, screenX, screenY);
                        return true;
                    }
                }
                setMode(Mode.DOWN, screenX, screenY);
                break;
            case Input.Buttons.RIGHT:
                setMode(Mode.CAMERA_MOVE, screenX, screenY);
                break;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        switch (mode) {
            case DOWN:
                currentPoint.set(screenX, screenY);
                if(prevPoint.dst(currentPoint) >= SELECT_MODE_BACKLASH) {
                    setMode(Mode.SELECT, screenX, screenY);
                }
                break;
            case CAMERA_MOVE:
                float deltaX = prevPoint.x - screenX;
                float deltaY = screenY - prevPoint.y;
                gameCamera.moveWithZoom(deltaX, deltaY);
                prevPoint.set(screenX, screenY);
                break;
            case SELECT:
                currentPoint.set(screenX, screenY);
                selectionBounds.set(prevPoint, currentPoint);
                break;
            case UNITS_MOVE:
                currentPoint.set(screenX, screenY);
                Vector2 delta = gameCamera.distance(prevPoint, currentPoint);
                delta.y = -delta.y;
                for(GameObject unit : selection) {
                    unit.transform.translate(delta);
                }
                onChange.dispatch(selection);
                prevPoint.set(currentPoint);
                break;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        setMode(Mode.NONE, screenX, screenY);
        return false;
    }

    public void setMode(Mode mode, int x, int y) {
        if(mode == this.mode) return;
        if(this.mode == Mode.SELECT) {
            selection.clear();
            Bounds unprojectSelectionBounds = gameCamera.unproject(selectionBounds);
            for(GameObject unit : editableUnits) {
                if(unprojectSelectionBounds.pointIn(unit.transform.getPosition())) {
                    selection.add(unit);
                }
            }
            onSelect.dispatch(selection);
            selectionBounds.reset();
        }
        if(this.mode == Mode.DOWN && mode == Mode.NONE) {
            selection.clear();
            Vector2 pos = gameCamera.unproject(x, y);
            for(GameObject unit : editableUnits) {
                if(unit.getBounds().pointIn(pos)) {
                    selection.add(unit);
                    break;
                }
            }
            onSelect.dispatch(selection);
        }
        this.mode = mode;
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
        gameCamera.zoomChange(amount/10f);
        return false;
    }

    public Bounds getSelectionBounds() {
        return selectionBounds;
    }

    public Array<GameObject> getSelection() {
        return selection;
    }
}
