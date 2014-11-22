package ru.sipaha.engine.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.graphics.Camera;
import ru.sipaha.engine.test.GameScreen;

import java.util.Iterator;

/**
 * Created on 19.11.2014.
 */

public class EditorApplication implements ApplicationListener {

    private final Engine editorEngine = new Engine();
    private final Engine testEngine = new Engine();
    private boolean isTestRunning = false;

    private EditorController controller;
    private EditorRenderLayer renderLayer;

    private volatile boolean testRunRequest = false;
    private volatile boolean testStopRequest = false;

    @Override
    public void create() {
        editorEngine.setEditorMode(true);
        GameScreen.createTestObjects(editorEngine);
        testEngine.initialize();
        editorEngine.awake();

        Camera gameCamera = editorEngine.renderer.getRenderLayer().camera;
        renderLayer = new EditorRenderLayer(gameCamera);
        controller = new EditorController(gameCamera, editorEngine.tagManager.getUnitsWithTag("Editable"));
        renderLayer.setSelectedUnits(controller.getSelection());
        renderLayer.setSelectionBounds(controller.getSelectionBounds());
        editorEngine.input.addProcessor(controller);
        editorEngine.renderer.addRenderLayer(renderLayer);

        final Camera camera = testEngine.renderer.getRenderLayer().camera;
        testEngine.input.addProcessor(new InputAdapter() {
            Vector2 oldVec = new Vector2();
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                oldVec.set(screenX, screenY);
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                float dx = oldVec.x - screenX;
                float dy = screenY - oldVec.y;
                camera.moveWithZoom(dx, dy);
                oldVec.set(screenX, screenY);
                return true;
            }
        });


    }

    /*public GameObject getGameObject() {
        Iterable<GameObject> objects = editorEngine.tagManager.getUnitsWithTag("Editable");
        Iterator<GameObject> it = objects.iterator();
        it.hasNext();
        it.next();
        it.hasNext();
        return it.next();
    }*/

    @Override
    public void resize(int width, int height) {
        editorEngine.renderer.resize(width, height);
        testEngine.renderer.resize(width, height);
    }

    @Override
    public void render() {
        if(testRunRequest) {
            testEngine.set(editorEngine);
            testEngine.renderer.getRenderLayer().camera.set(
                    editorEngine.renderer.getRenderLayer().camera);
            testEngine.awake();
            testRunRequest = false;
            isTestRunning = true;
        }
        if(testStopRequest) {
            editorEngine.awake();
            testStopRequest = false;
            isTestRunning = false;
        }
        if(!isTestRunning) {
            editorEngine.update(Gdx.graphics.getDeltaTime());
        } else {
            testEngine.update(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    public void setTestRunning(boolean run) {
        if(isTestRunning != run) {
            if(run) {
                testRunRequest = true;
                testStopRequest = false;
            } else {
                testRunRequest = false;
                testStopRequest = true;
            }
        }
    }

    public Engine getEditorEngine() {
        return editorEngine;
    }

    public Engine getTestEngine() {
        return testEngine;
    }

    public EditorRenderLayer getRenderLayer() {
        return renderLayer;
    }

    public EditorController getController() {
        return controller;
    }
}
