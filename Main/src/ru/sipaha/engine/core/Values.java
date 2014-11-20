package ru.sipaha.engine.core;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ru.sipaha.engine.graphics.renderlayers.RenderLayer;
import ru.sipaha.engine.utils.Shaders;

import java.util.Arrays;

/**
 * Created on 02.11.2014.
 */

public class Values {

    private Values(){}

    public static class Float {
        protected float value;
        private final Flag changed;

        public Float(Flag changed) {
            this.changed = changed;
        }

        public Float(Flag changed, float initialValue) {
            this(changed);
            this.value = initialValue;
        }

        public void add(float amount) {
            this.value = value + amount;
            changed.value = true;
        }

        public void set(float value) {
            this.value = value;
            changed.value = true;
        }

        public void set(Float value) {
            this.value = value.value;
            changed.value = true;
        }

        public void div(float value) {
            this.value = this.value / value;
            changed.value = true;
        }

        public float get() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public static class Int {
        protected int value;
        private final Flag changed;

        public Int(Flag changed) {
            this.changed = changed;
        }

        public Int(Flag changed, int initialValue) {
            this(changed);
            this.value = initialValue;
        }

        public void add(int amount) {
            set(value+amount);
        }

        public void set(Int value) {
            set(value.value);
        }

        public void div(int value) {
            set(this.value / value);
        }

        public void set(int value) {
            if(this.value != value) {
                this.value = value;
                changed.value = true;
            }
        }

        public int get() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Int && ((Int)obj).value == value;
        }
    }

    public static class FloatArray {
        protected float[] value;
        private final Flag changed;

        public FloatArray(Flag changed, float[] initialValue) {
            this.changed = changed;
            this.value = initialValue;
        }

        public void set(float[] value) {
            this.value = value;
            changed.value = true;
        }

        public void setValues(float[] values) {
            System.arraycopy(values, 0, value, 0, value.length);
            changed.value = true;
        }

        public void set(FloatArray value) {
            set(value.value);
        }

        public void setValues(FloatArray values) {
            setValues(values.value);
        }

        public float[] get() {
            return value;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("[");
            for (float val : value) builder.append(val + ' ');
            builder.setCharAt(builder.length() - 1, ']');
            return builder.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FloatArray && Arrays.equals(((FloatArray)obj).value, value);
        }
    }

    public static class Bool {
        protected boolean value;
        private final Flag changed;

        public Bool(boolean initialValue) {
            value = initialValue;
            changed = null;
        }

        public Bool(Flag changed, boolean initialValue) {
            value = initialValue;
            this.changed = changed;
        }

        public boolean check() {
            return value;
        }

        public void set() {
            set(true);
        }

        public void drop() {
            set(false);
        }

        public boolean get() {
            return value;
        }

        public boolean and(boolean arg) {
            set(value && arg);
            return value;
        }

        public boolean or(boolean arg) {
            set(value || arg);
            return value;
        }

        /**@return true if state changed*/
        public boolean set(boolean value) {
            if(value != this.value) {
                this.value = value;
                if(changed != null) {
                    changed.value = true;
                }
                return true;
            } else {
                return false;
            }
        }

        public void set(Bool value) {
            set(value.value);
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Bool && ((Bool)obj).value == value;
        }
    }

    public static class RenderLayerValue {
        protected String name;
        protected RenderLayer layer;
        private final Flag changed;

        public RenderLayerValue(Flag changed, String initialValue) {
            this.changed = changed;
            this.name = initialValue;
        }

        public void set(String value) {
            if(!value.equals(name)) {
                this.name = value;
                changed.value = true;
            }
        }

        public void set(RenderLayer layer) {
            if(layer == null) {
                this.layer = null;
                changed.value = true;
            } else {
                if(layer != this.layer) {
                    this.name = layer.name;
                    this.layer = layer;
                    changed.value = true;
                }
            }
        }

        public void set(RenderLayerValue value) {
            if(value.layer != layer || !value.name.equals(name)) {
                this.name = value.name;
                this.layer = value.layer;
                changed.value = true;
            }
        }

        public String getName() {
            return name;
        }

        public RenderLayer getLayer() {
            return layer;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof RenderLayerValue && ((RenderLayerValue)obj).name.equals(name);
        }
    }

    public static class ShaderValue {
        protected ShaderProgram value;
        protected String name;
        private final Flag changed;

        public ShaderValue(Flag changed, String initialValue) {
            this.changed = changed;
            set(initialValue);
        }

        public void set(String value) {
            ShaderProgram newProgram = Shaders.get(value);
            if(newProgram != this.value) {
                this.value = Shaders.get(value);
                this.name = value;
                changed.value = true;
            }
        }

        public void set(ShaderValue value) {
            set(value.name);
        }

        public ShaderProgram get() {
            return value;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ShaderValue && ((ShaderValue)obj).value == value;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    public static class BlendFunction {
        public enum Function {
            GL_ZERO(GL20.GL_ZERO),
            GL_ONE(GL20.GL_ONE),
            GL_SRC_COLOR(GL20.GL_SRC_COLOR),
            GL_ONE_MINUS_SRC_COLOR(GL20.GL_ONE_MINUS_SRC_COLOR),
            GL_DST_COLOR(GL20.GL_DST_COLOR),
            GL_ONE_MINUS_DST_COLOR(GL20.GL_ONE_MINUS_DST_COLOR),
            GL_SRC_ALPHA(GL20.GL_SRC_ALPHA),
            GL_ONE_MINUS_SRC_ALPHA(GL20.GL_ONE_MINUS_SRC_ALPHA),
            GL_DST_ALPHA(GL20.GL_DST_ALPHA),
            GL_ONE_MINUS_DST_ALPHA(GL20.GL_ONE_MINUS_DST_ALPHA),
            GL_CONSTANT_COLOR(GL20.GL_CONSTANT_COLOR),
            GL_ONE_MINUS_CONSTANT_COLOR(GL20.GL_ONE_MINUS_CONSTANT_COLOR),
            GL_CONSTANT_ALPHA(GL20.GL_CONSTANT_ALPHA),
            GL_ONE_MINUS_CONSTANT_ALPHA(GL20.GL_ONE_MINUS_CONSTANT_ALPHA);

            public int value;

            private Function(int value) {
                this.value = value;
            }
        }

        protected Function function;
        private final Flag changed;

        public BlendFunction(Flag changed, String initialValue) {
            this.changed = changed;
            set(initialValue);
        }

        public void set(String function) {
            Function newFunction = Function.valueOf(function);
            if(newFunction != this.function) {
                this.function = newFunction;
                changed.value = true;
            }
        }

        public void set(BlendFunction function) {
            if(this.function != function.function) {
                this.function = function.function;
                changed.value = true;
            }
        }

        public void set(Function function) {
            if(this.function != function) {
                this.function = function;
                changed.value = true;
            }
        }

        public int get() {
            return function.value;
        }

        public int getIndex() {
            return function.ordinal();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof BlendFunction && ((BlendFunction)obj).function == function;
        }

        @Override
        public String toString() {
            return function.name();
        }
    }

    public static class Flag {
        public boolean value = false;

        public Flag() {}

        public Flag(boolean initialValue) {
            value = initialValue;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
