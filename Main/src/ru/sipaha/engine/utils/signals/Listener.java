package ru.sipaha.engine.utils.signals;

public interface Listener<T> {

	public void receive(T object);
}
