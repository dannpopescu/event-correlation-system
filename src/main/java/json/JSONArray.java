package json;

import java.util.ArrayList;

public class JSONArray extends JSON {

    private final ArrayList<Object> array;

    public JSONArray() {
        this.array = new ArrayList<>();
    }

    @Override
    public boolean isValidKey(String key) {
        if (key.length() > 10) {
            return false;
        }
        for (char c : key.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return Integer.parseInt(key) < this.array.size();
    }

    public Object get(int index) {
        return this.array.get(index);
    }

    @Override
    public Object get(String index) {
        return get(Integer.parseInt(index));
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

    @Override
    public String toString() {
        return this.array.toString();
    }
}
