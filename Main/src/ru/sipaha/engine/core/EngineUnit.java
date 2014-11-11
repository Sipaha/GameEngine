package ru.sipaha.engine.core;

import java.util.BitSet;

/**
 * Created on 08.11.2014.
 */

public interface EngineUnit<T extends EngineUnit> {
    void update(float delta);
    void fixedUpdate(float delta);
    void initialize(Engine engine);
    void start(Engine engine);
    BitSet getTagBits();
    boolean isEnable();
    T copy();
}
