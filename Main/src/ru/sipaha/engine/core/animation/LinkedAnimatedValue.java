package ru.sipaha.engine.core.animation;

import ru.sipaha.engine.utils.Array;

/**
 * Created on 02.11.2014.
 */

public interface LinkedAnimatedValue {

    /**current time in animation*/
    public void update(float time);
    public float getMaxDefinedTime();
    public boolean findLink(Array objects);
    public void setLinkedValue(Array objects);
    public LinkedAnimatedValue copy();
}
