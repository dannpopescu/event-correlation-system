package com.danpopescu.eventcorrelation;

import java.util.ArrayList;

public class JSONArray extends JSON {

    private final ArrayList<Object> array;

    public JSONArray() {
        this.array = new ArrayList<>();
    }

    @Override
    public Object get(String index) {
        return get(Integer.parseInt(index));
    }

    public Object get(int index) {
        return this.array.get(index);
    }

    public boolean containsIndex(int index) {
        return index >= 0 && index < this.array.size();
    }

    public void put(Object object) {
        this.array.add(object);
    }

    @Override
    public void put(String index, Object value) {
        this.array.add(Integer.parseInt(index), value);
    }

    public Object delete(int index) {
        return this.array.remove(index);
    }

    @Override
    public Object delete(String key) {
        return delete(Integer.parseInt(key));
    }

    public int size() {
        return this.array.size();
    }

    @Override
    public String toString() {
        return this.array.toString();
    }
}
