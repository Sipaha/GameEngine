package ru.sipaha.engine.utils.signals;

import com.badlogic.gdx.utils.Array;

public class Signal<T> {
    private final Array<Listener<T>> listeners;

	public Signal(){
		listeners = new Array<>(false, 4, Listener.class);
	}

	public void add(Listener<T> listener){
		listeners.add(listener);
	}

    public void addAll(Signal<T> signal) {
        listeners.addAll(signal.listeners);
    }

    public void set(Signal<T> signal) {
        listeners.clear();
        listeners.addAll(signal.listeners);
    }

	public void remove(Listener<T> listener){
		listeners.removeValue(listener, true);
	}

    public void clear() {
        listeners.clear();
    }

	public void dispatch(T object){
        Listener<T>[] listenersAr = listeners.items;
		for(int i = 0, s = listeners.size; i < s; i++){
			listenersAr[i].receive(object);
		}
	}
}
