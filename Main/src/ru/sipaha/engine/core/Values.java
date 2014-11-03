package ru.sipaha.engine.core;

/**
 * Created on 02.11.2014.
 */

public class Values {

    private Values(){}

    public static class Float {
        protected float value;
        private final Bool wasChanged;

        public Float(Bool wasChanged) {
            this.wasChanged = wasChanged;
        }

        public Float(Bool wasChanged, float initialValue) {
            this(wasChanged);
            this.value = initialValue;
        }

        public void add(float amount) {
            this.value = value + amount;
            wasChanged.value = true;
        }

        public void set(float value) {
            this.value = value;
            wasChanged.value = true;
        }

        public void set(Float value) {
            this.value = value.value;
            wasChanged.value = true;
        }

        public void div(float value) {
            this.value = this.value / value;
            wasChanged.value = true;
        }

        public float get() {
            return value;
        }

        @Override
        public String toString() {
            return "FloatValue{" +
                    "value=" + value +
                    '}';
        }
    }

    public static class Int {
        protected int value;
        private final Bool wasChanged;

        public Int(Bool wasChanged) {
            this.wasChanged = wasChanged;
        }

        public Int(Bool wasChanged, int initialValue) {
            this(wasChanged);
            this.value = initialValue;
        }

        public void add(int amount) {
            this.value = value + amount;
            wasChanged.value = true;
        }

        public void set(int value) {
            this.value = value;
            wasChanged.value = true;
        }

        public void set(Int value) {
            this.value = value.value;
            wasChanged.value = true;
        }

        public void div(int value) {
            this.value = this.value / value;
            wasChanged.value = true;
        }

        public float get() {
            return value;
        }

        @Override
        public String toString() {
            return "IntValue{" +
                    "value=" + value +
                    '}';
        }
    }

    public static class FloatArray {
        protected float[] value;
        private final Bool wasChanged;

        public FloatArray(Bool wasChanged, float[] initialValue) {
            this.wasChanged = wasChanged;
            this.value = initialValue;
        }

        public void set(float[] value) {
            this.value = value;
            wasChanged.value = true;
        }

        public void setValues(float[] values) {
            System.arraycopy(values, 0, value, 0, value.length);
            wasChanged.value = true;
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
            StringBuilder builder = new StringBuilder("FloatArrValue{");
            for (float val : value) builder.append(val + ' ');
            builder.setCharAt(builder.length() - 1, '}');
            return builder.toString();
        }
    }

    public static class Bool {
        protected boolean value;
        private final Bool wasChanged;

        public Bool(){
            wasChanged = null;
        }

        public Bool(boolean initialValue) {
            value = initialValue;
            wasChanged = null;
        }

        public Bool(Bool wasChanged, boolean initialValue) {
            value = initialValue;
            this.wasChanged = wasChanged;
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
                if(wasChanged != null) {
                    wasChanged.set();
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
