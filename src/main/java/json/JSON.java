package json;

import exceptions.NotArrayException;
import exceptions.NotObjectException;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class JSON {

    private String lastAccessedKey;

    public JSON() {
        this.lastAccessedKey = "root";
    }

    public abstract boolean isValidKey(String key);

    public boolean isValidKeySequence(Queue<String> keys) {
        Queue<String> k = new ArrayDeque<>(keys);
        Object obj = this;
        while (!k.isEmpty()) {
            if (obj instanceof JSON && ((JSON) obj).isValidKey(k.peek())) {
                obj = ((JSON) obj).get(k.poll());
            } else {
                return false;
            }
        }
        return true;
    }

    public abstract Object get(String key);

    public Object get(Queue<String> keys) {
        try {
            Object structure = traverseUpToLastNode(keys);
            checkValidKeyForStructure(structure, keys.peek());
            return ((JSON) structure).get(keys.poll());
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("ERROR_INDEX_OUT_OF_RANGE " + lastAccessedKey);
        }
    }

    private Object traverseUpToLastNode(Queue<String> keys) {
        Object obj = this;
        lastAccessedKey = "root";
        while (keys.size() > 1) {
            checkValidKeyForStructure(obj, keys.peek());
            try {
                obj = ((JSON) obj).get(keys.peek());
            } catch (IndexOutOfBoundsException e) {
                throw new IndexOutOfBoundsException("ERROR_INDEX_OUT_OF_RANGE " + lastAccessedKey);
            }
            lastAccessedKey = keys.poll();
        }
        return obj;
    }

    private void checkValidKeyForStructure(Object structure, String key) {
        if (isIndex(key) && !(structure instanceof JSONArray)) {
            throw new NotArrayException("ERROR_NOT_ARRAY " + lastAccessedKey);
        } else if (isKey(key) && !(structure instanceof JSONObject)) {
            throw new NotObjectException("ERROR_NOT_OBJECT " + lastAccessedKey);
        }
    }

    protected boolean isKey(String s) {
        return s.startsWith("\"") && s.endsWith("\"");
    }

    protected boolean isIndex(String s) {
        if (s.length() > 10) {
            return false;
        }
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public abstract void put(String key, Object value);

    public void put(Queue<String> keys, Object value) {
//        try {
            Object structure = traverseUpToLastNode(keys);
            checkValidKeyForStructure(structure, keys.peek());
            ((JSON) structure).put(keys.poll(), value);
//        } catch (IndexOutOfBoundsException e) {
//            throw new IndexOutOfBoundsException("ERROR_INDEX_OUT_OF_RANGE " + lastAccessedKey);
//        }
    }

    public abstract Object delete(String key);

    public void delete(Queue<String> keys) {
//        try {
            Object structure = traverseUpToLastNode(keys);
            checkValidKeyForStructure(structure, keys.peek());
            ((JSON) structure).delete(keys.poll());
//        } catch (IndexOutOfBoundsException e) {
//            throw new IndexOutOfBoundsException("ERROR_INDEX_OUT_OF_RANGE " + lastAccessedKey);
//        }
    }

}
