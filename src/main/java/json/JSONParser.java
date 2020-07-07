package json;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Note: probably won't work if an array contains only values and not an json.JSONObject
 */
public class JSONParser {

    private final Deque<JSONArray> arrayStack;
    private final Deque<JSONObject> objectStack;
    private final Deque<Boolean> isCursorInsideArray;

    private JSON root;

    private String currentKey;
    private Object currentValue;

    private static final int QUOTED_STRING = 34;
    private static final int OPEN_ARRAY = '[';
    private static final int CLOSE_ARRAY = ']';
    private static final int OPEN_OBJECT = '{';
    private static final int CLOSE_OBJECT = '}';

    public JSONParser() {
        this.objectStack = new ArrayDeque<>();
        this.arrayStack = new ArrayDeque<>();
        this.isCursorInsideArray = new ArrayDeque<>();
    }

    public JSON parse(String filePath) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(new BufferedReader(new FileReader(filePath)));
        setTokenizerSyntax(tokenizer);

        int currentToken = tokenizer.nextToken();

        while (currentToken != StreamTokenizer.TT_EOF) {
            switch (currentToken) {
                case JSONParser.OPEN_ARRAY -> processNewArray();
                case JSONParser.OPEN_OBJECT -> processNewObject();
                case JSONParser.CLOSE_ARRAY -> {
                    arrayStack.pop();
                    isCursorInsideArray.pop();
                }
                case JSONParser.CLOSE_OBJECT -> {
                    objectStack.pop();
                    isCursorInsideArray.pop();
                }
                case JSONParser.QUOTED_STRING -> processQuotedString(tokenizer.sval);
                case StreamTokenizer.TT_WORD -> processUnquotedString(tokenizer.sval);
            }

            if (currentKey != null && currentValue != null) {
                putKeyValuePair();
            }

            currentToken = tokenizer.nextToken();
        }

        return root;
    }

    /**
     * Creates a new array
     */
    private void processNewArray() {
        JSONArray array = new JSONArray();

        handleIfCorrespondsToAKey(array);

        if (isCursorInsideArray.peek() != null && isCursorInsideArray.peek()) {
            arrayStack.peek().put(array);
        }

        arrayStack.push(array);
        isCursorInsideArray.push(Boolean.TRUE);

        if (root == null) {
            root = array;
        }
    }

    private boolean handleIfCorrespondsToAKey(JSON json) {
        if (currentKey != null) {
            currentValue = json;
            putKeyValuePair();
            return true;
        }
        return false;
    }

    private void processNewObject() {
        JSONObject object = new JSONObject();
        if (!handleIfCorrespondsToAKey(object)) {
            arrayStack.peek().put(object);
        }
        objectStack.push(object);
        isCursorInsideArray.push(Boolean.FALSE);
    }

    private void processQuotedString(String string) {
        string = "\"" + string + "\"";
        if (isCursorInsideArray.peek()) {
            arrayStack.peek().put(string);
            return;
        }
        if (currentKey == null) {
            currentKey = string;
        } else {
            currentValue = string;
        }
    }

    private void processUnquotedString(String string) {
        currentValue = switch (string) {
            case "false" -> Boolean.FALSE;
            case "true" -> Boolean.TRUE;
            case "null" -> "null";
            default -> new BigInteger(string);
        };
        if (isCursorInsideArray.peek()) {
            arrayStack.peek().put(currentValue);
            currentValue = null;
        }
    }

    private void putKeyValuePair() {
        objectStack.peek().put(currentKey, currentValue);
        currentKey = null;
        currentValue = null;
    }

    private void setTokenizerSyntax(StreamTokenizer tokenizer) {
        tokenizer.resetSyntax();

        tokenizer.whitespaceChars(0, 32);

        tokenizer.wordChars('0', '9');
        tokenizer.wordChars('-', '.');
        tokenizer.wordChars('+', '+');
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars(0xa0, 0xff);

        tokenizer.quoteChar('"');
    }
}
