package ru.sipaha.engine.utils.functions;

/**
 * Created on 04.11.2014.
 */

public abstract class Function1f1i extends Function1f {
    public Function1f1i(){}
    public Function1f1i(Function1f1i prototype) {
        super(prototype);
    }
    public abstract int get(float arg);
}
