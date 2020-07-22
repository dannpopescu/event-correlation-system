package com.danpopescu.eventcorrelation;

import java.util.HashMap;

public class JSONObject extends JSON {

    private final HashMap<String, Object> map;

    public JSONObject() {
        this.map = new HashMap<>();
    }

    public boolean containsKey(String key) {
        return this.map.containsKey(key);
    }

    @Override
    public Object get(String key) {
        return this.map.get(key);
    }

    @Override
    public void put(String key, Object value) {
        this.map.put(key, value);
    }

    @Override
    public Object delete(String key) {
        return this.map.remove(key);
    }

    @Override
    public String toString() {
        return this.map.toString().replaceAll("\"=", "\":");
    }
}
