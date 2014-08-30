package ru.sipaha.engine.utils;

import ru.sipaha.engine.core.GameObject;

public interface ContactListener {
    void beginContact(GameObject gameObject);
    void endContact(GameObject gameObject);
}
