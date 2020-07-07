package json;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;
import java.util.Set;

public class OldParser {

    private JSON finalJsonObject;

    private final Deque<JSONArray> arrayStack;
    private final Deque<JSONObject> objectStack;

    private String key;
    private Object value;

    private final StringBuilder keyAccumulator;
    private final StringBuilder valueAccumulator;

    private static final Set<String> START_AND_END_MARKERS = Set.of("[", "{", "}", "},", "]", "],");

    public OldParser() {
        this.arrayStack = new ArrayDeque<>();
        this.objectStack = new ArrayDeque<>();
        this.keyAccumulator = new StringBuilder();
        this.valueAccumulator = new StringBuilder();
    }

    public JSON parse(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(filePath)));

        String token;
        
        while (scanner.hasNext()) {
            token = scanner.next();

            if (START_AND_END_MARKERS.contains(token)) {
                handleStartAndEndMarker(token);
            }

            if (key == null) {
                appendTokenToKey(token);
            } else {
                appendTokenToValue(token);
            }

            if (key != null && value != null) {
                putKeyValuePair();
            }

        }
        return finalJsonObject;
    }

    private void handleStartAndEndMarker(String token) {
        switch (token) {
            case "[" -> putNewJsonArray();
            case "{" -> putNewJsonObject();
            case "]", "]," -> arrayStack.pop();
            case "}", "}," -> objectStack.pop();
        }
    }

    private void appendTokenToValue(String token) {
        valueAccumulator.append(" ").append(token);
        if (token.endsWith("\",")) {
            value = valueAccumulator.substring(1, valueAccumulator.length() - 1);
        } else if (token.endsWith(",")) {
            value = parseNonStringValue(token.substring(0, token.length() - 1));
        } else if (token.endsWith("\"") && valueAccumulator.length() > 2) {
            value = valueAccumulator.substring(1, valueAccumulator.length());
        }
    }

    private void appendTokenToKey(String token) {
        keyAccumulator.append(" ").append(token);
        if (token.endsWith("\":")) {
            key = keyAccumulator.substring(1, keyAccumulator.length() - 1);
        }
    }

    private void putNewJsonObject() {
        JSONObject jsonObject = new JSONObject();
        if (key != null) {
            value = jsonObject;
            putKeyValuePair();
        } else {
            arrayStack.peek().put(jsonObject);
        }
        objectStack.push(jsonObject);
    }

    private void putNewJsonArray() {
        JSONArray array = new JSONArray();
        if (key != null) {
            value = array;
            putKeyValuePair();
        }
        arrayStack.push(array);
        if (finalJsonObject == null) {
            finalJsonObject = array;
        }
    }

    private void putKeyValuePair() {
        objectStack.peek().put(key, value);
        clearKeyValueFields();
    }

    private void clearKeyValueFields() {
        key = null;
        value = null;
        keyAccumulator.setLength(0);
        valueAccumulator.setLength(0);
    }

    private Object parseNonStringValue(String token) {
        switch (token) {
            case "false": return false;
            case "true": return true;
            case "null": return null;
            default:
                return new BigInteger(token);
        }
    }

}
