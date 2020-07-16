package json;

import exceptions.JSONException;

import java.util.Queue;

public abstract class JSON {

    /**
     * Last accessed key when traversing the JSON object.
     * Used for generating the exceptions' message.
     */
    private String lastAccessedNode;

    public JSON() {
        this.lastAccessedNode = "root";
    }

    public abstract Object get(String key);

    public Object get(Queue<String> keys) throws JSONException {
        Object node = traverseUpToLastNode(keys);
        checkValidKey(node, keys.peek());
        return ((JSON) node).get(keys.poll());
    }

    private Object traverseUpToLastNode(Queue<String> keys) throws JSONException {
        this.lastAccessedNode = "root";
        Object node = this;
        String currentKey;

        while (keys.size() > 1) {
            currentKey = keys.poll();
            checkValidKey(node, currentKey);
            node = ((JSON) node).get(currentKey);
            this.lastAccessedNode = currentKey;
        }
        return node;
    }

    protected void checkValidKey(Object node, String key) throws JSONException {
        if (isIndex(key)) {
            if (!(node instanceof JSONArray)) {
                throw new JSONException("ERROR_NOT_ARRAY " + this.lastAccessedNode);
            } else if (!((JSONArray) node).containsIndex(Integer.parseInt(key))) {
                throw new JSONException("ERROR_INDEX_OUT_OF_RANGE " + this.lastAccessedNode);
            }
        } else if (isKey(key)) {
            if (!(node instanceof JSONObject)) {
                throw new JSONException("ERROR_NOT_OBJECT " + this.lastAccessedNode);
            } else if (!((JSONObject) node).containsKey(key)) {
                throw new JSONException("KEY_NOT_FOUND " + key);
            }
        }
    }

    public abstract void put(String key, Object value);

    public void put(Queue<String> keys, Object value) {
        Object node = traverseUpToLastNode(keys);
        String key = keys.poll();
        if (!(node instanceof JSONObject)) {
            throw new JSONException("ERROR_NOT_OBJECT " + this.lastAccessedNode);
        }
        ((JSON) node).put(key, value);
    }

    public abstract Object delete(String key);

    public void delete(Queue<String> keys) {
        Object node = traverseUpToLastNode(keys);
//        checkValidKeyForNode(node, keys.peek());
        String key = keys.poll();
        if (isIndex(key)) {
            if (!(node instanceof JSONArray)) {
                throw new JSONException("ERROR_NOT_ARRAY " + this.lastAccessedNode);
            }
        } else if (isKey(key)) {
            if (!(node instanceof JSONObject)) {
                throw new JSONException("ERROR_NOT_OBJECT " + this.lastAccessedNode);
            }
        }
        ((JSON) node).delete(key);
    }

    private static boolean isKey(String s) {
        return s.startsWith("\"") && s.endsWith("\"");
    }

    private static boolean isIndex(String s) {
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

}
