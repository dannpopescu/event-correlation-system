package json;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class CharParser {

    private final Deque<JSONArray> arrayStack;
    private final Deque<JSONObject> objectStack;

    private JSON root;

    private String key;
    private Object value;

    private final StringBuilder charAccumulator;
    private boolean insideString = false;

    public CharParser() {
        arrayStack = new ArrayDeque<>();
        objectStack = new ArrayDeque<>();
        charAccumulator = new StringBuilder();
    }

    public JSON parse(String file) throws IOException {
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            int charCode = reader.read();

            while (charCode != -1) {
                if (insideString && charCode != '"') {
                    charAccumulator.append((char) charCode);
                } else if (Character.isWhitespace(charCode)) {
                } else {
                    switch (charCode) {
                        case '[':
                            JSONArray array = new JSONArray();
                            if (key != null) {
                                value = array;
                                putKeyValuePair();
                            } else if (!arrayStack.isEmpty()) {
                                arrayStack.peek().put(array);
                            }
                            arrayStack.push(array);
                            if (root == null) {
                                root = array;
                            }
                            break;
                        case '{':
                            JSONObject object = new JSONObject();
                            if (key != null) {
                                value = object;
                                putKeyValuePair();
                            } else if (!arrayStack.isEmpty()) {
                                arrayStack.peek().put(object);
                            }
                            objectStack.push(object);
                            break;
                        case ']':
                            if (charAccumulator.length() > 0) {
                                value = charAccumulator.toString();
                                charAccumulator.setLength(0);
                            }

                            if (key != null) {
                                putKeyValuePair();
                            } else if (value != null) {
                                arrayStack.peek().put(value);
                            }
                            arrayStack.pop();
                            break;
                        case '}':
                            if (charAccumulator.length() > 0) {
                                value = charAccumulator.toString();
                                charAccumulator.setLength(0);
                            }
                            if (key != null) {
                                putKeyValuePair();
                            }

                            objectStack.pop();
                            break;
                        case '"':
                            insideString = !insideString;
                            charAccumulator.append('"');
                            break;
                        case ':':
                            key = charAccumulator.toString();
                            charAccumulator.setLength(0);
                            break;
                        case ',':
                            if (charAccumulator.length() > 0) {
                                value = charAccumulator.toString();
                                charAccumulator.setLength(0);
                            }
                            break;
                        default:
                            charAccumulator.append((char) charCode);
                            break;
                    }
                }

                if (key != null && value != null) {
                    putKeyValuePair();
                }

                charCode = reader.read();
            }

        }
        return root;
    }

    private void putKeyValuePair() {
        objectStack.peek().put(key, value);
        key = null;
        value = null;
    }
}
