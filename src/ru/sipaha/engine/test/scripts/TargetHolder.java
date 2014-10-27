package ru.sipaha.engine.test.scripts;

import ru.sipaha.engine.core.GameObject;

/**
 * Created on 07.10.2014.
 */

public interface TargetHolder {

    GameObject getTarget();
    float getDistanceToTarget();
}
