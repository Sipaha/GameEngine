package ru.sipaha.engine.core;

import ru.sipaha.engine.utils.Array;

import java.lang.reflect.Field;

/**
 * Created on 02.11.2014.
 */

public class LinkedValue<T> {

    public T value;
    private Address address;

    public LinkedValue(LinkedValue prototype) {
        address = prototype.address;
    }

    public LinkedValue(T value) {
        this.value = value;
    }

    public boolean findLink(Array objects) {
        for(int i = 0; i < objects.size; i++) {
            Object o = objects.get(i);
            Field[] fields = findField(o, value, 0);
            if(fields != null) {
                address = new Address();
                address.fields = fields;
                address.objectId = i;
                return true;
            }
        }
        return false;
    }

    private Field[] findField(Object obj, Object targetValue, int deepIdx) {
        for(Field field : obj.getClass().getFields()) {
            if (!field.getType().isPrimitive()) {
                try {
                    Object fieldValue = field.get(obj);
                    if(fieldValue != null) {
                        if(fieldValue == targetValue) {
                            Field[] result = new Field[deepIdx+1];
                            result[deepIdx] = field;
                            return result;
                        } else {
                            Field[] result = findField(fieldValue, targetValue, deepIdx + 1);
                            if(result != null) {
                                result[deepIdx] = field;
                                return result;
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void setLinkedValue(Array objects) {
        Object value = objects.get(address.objectId);
        try {
            for (Field field : address.fields) {
                value = field.get(value);
            }
            this.value = (T) value;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static class Address {
        int objectId = -1;
        Field[] fields;
    }
}
