package ru.sipaha.engine.core;

import com.badlogic.gdx.InputProcessor;
import ru.sipaha.engine.utils.Array;

/**
 * Created on 07.10.2014.
 */

public class Input implements InputProcessor {

    private Array<InputProcessor> processors = new Array<>(InputProcessor.class);

    public void addProcessor(InputProcessor processor) {
        processors.add(processor);
    }

    @Override
    public boolean keyDown(int keycode) {
        for(InputProcessor listener : processors) if(listener.keyDown(keycode)) break;
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        for(InputProcessor listener : processors) if(listener.keyUp(keycode)) break;
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        for(InputProcessor listener : processors) if(listener.keyTyped(character)) break;
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for(InputProcessor listener : processors) if(listener.touchDown(screenX, screenY, pointer, button)) break;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for(InputProcessor listener : processors) if(listener.touchUp(screenX, screenY, pointer, button)) break;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        for(InputProcessor listener : processors) if(listener.touchDragged(screenX, screenY, pointer)) break;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        for(InputProcessor listener : processors) if(listener.mouseMoved(screenX, screenY)) break;
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        for(InputProcessor listener : processors) if(listener.scrolled(amount)) break;
        return true;
    }
}
