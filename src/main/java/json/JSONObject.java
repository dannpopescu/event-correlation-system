package json;

import exceptions.KeyNotFoundException;

import java.util.HashMap;

public class JSONObject extends JSON {

    private final HashMap<String, Object> map;

    public JSONObject() {
        this.map = new HashMap<>();
    }

    @Override
    public boolean isValidKey(String key) {
        return this.map.containsKey(key);
    }

    @Override
    public Object get(String key) {
        if (!this.map.containsKey(key)) {
            throw new KeyNotFoundException("KEY_NOT_FOUND " + key);
        }
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
