package ru.sipaha.engine.desktop.propertieseditor.property;

import com.badlogic.gdx.utils.ObjectMap;

import java.lang.reflect.Field;

/**
 * Created on 20.11.2014.
 */

public class PrimitiveProperty extends FieldProperty {

    private static ObjectMap<Class<?>, Parser> parsers = new ObjectMap<>();

    static {
        parsers.put(byte.class, new Parser() {
            @Override
            public Object parse(String value) {
                return Byte.valueOf(value);
            }
        });
        parsers.put(short.class, new Parser() {
            @Override
            public Object parse(String value) {
                return Short.valueOf(value);
            }
        });
        parsers.put(int.class, new Parser() {
            @Override
            public Object parse(String value) {
                return Integer.valueOf(value);
            }
        });
        parsers.put(long.class, new Parser() {
            @Override
            public Object parse(String value) {
                return Long.valueOf(value);
            }
        });
        parsers.put(float.class, new Parser() {
            @Override
            public Object parse(String value) {
                return Float.valueOf(value);
            }
        });
        parsers.put(double.class, new Parser() {
            @Override
            public Object parse(String value) {
                return Double.valueOf(value);
            }
        });
        parsers.put(boolean.class, new Parser() {
            @Override
            public Object parse(String value) {
                if(value.equalsIgnoreCase("true")) return true;
                if(value.equalsIgnoreCase("false")) return false;
                return null;
            }
        });
    }

    public PrimitiveProperty(Object object, Field field) {
        super(object, field);
    }

    protected Object parse(String value) {
        try {
            Parser parser = parsers.get(field.getType());
            if(parser == null) {
                System.out.println("There are not parser for type \"" + field.getType().getName()+"\"");
                return null;
            }
            return parser.parse(value);
        } catch (Exception e) {
            System.out.println("\""+value+"\" is not correct value!");
            return null;
        }
    }

    private static interface Parser {
        public Object parse(String value);
    }
}
