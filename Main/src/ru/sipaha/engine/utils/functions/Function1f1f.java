package ru.sipaha.engine.utils.functions;

/** Created on 13.09.2014.*/

public abstract class Function1f1f extends Function1f {
    public Function1f1f() {}
    public Function1f1f(Function1f prototype) {
        super(prototype);
    }
    public abstract float get(float arg);
}
