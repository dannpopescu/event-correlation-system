package json;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;

public class JSONParser {

    private final Deque<JSONArray> arrayStack = new ArrayDeque<>();
    private final Deque<JSONObject> objectStack = new ArrayDeque<>();

    private JSON root;

    private String key;
    private Object value;

    private static final int QUOTED_STRING = 34;
    private static final int OPEN_ARRAY = '[';
    private static final int CLOSE_ARRAY = ']';
    private static final int OPEN_OBJECT = '{';
    private static final int CLOSE_OBJECT = '}';

    public JSON parse(String filePath) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(new BufferedReader(new FileReader(filePath)));
        setTokenizerSyntax(tokenizer);

        int currentToken = tokenizer.nextToken();

        while (currentToken != StreamTokenizer.TT_EOF) {
            switch (currentToken) {
                case OPEN_ARRAY -> processNewArray();
                case OPEN_OBJECT -> processNewObject();
                case CLOSE_ARRAY -> arrayStack.pop();
                case CLOSE_OBJECT -> objectStack.pop();
                case QUOTED_STRING -> processQuotedString(tokenizer.sval);
                case StreamTokenizer.TT_WORD -> processUnquotedString(tokenizer.sval);
            }

            if (key != null && value != null) {
                putKeyValuePair();
            }

            currentToken = tokenizer.nextToken();
        }

        return root;
    }

    private void processNewArray() {
        JSONArray array = new JSONArray();
        if (key != null) {
            value = array;
            putKeyValuePair();
        }
        arrayStack.push(array);
        if (root == null) {
            root = array;
        }
    }

    private void processNewObject() {
        JSONObject object = new JSONObject();
        if (key != null) {
            value = object;
            putKeyValuePair();
        } else {
            arrayStack.peek().put(object);
        }
        objectStack.push(object);
        if (root == null) {
            root = object;
        }
    }

    private void processQuotedString(String string) {
        string = "\"" + string + "\"";
        if (key == null) {
            key = string;
        } else {
            value = string;
        }
    }

    private void processUnquotedString(String string) {
        value = switch (string) {
            case "false" -> Boolean.FALSE;
            case "true" -> Boolean.TRUE;
            case "null" -> "null";
            default -> new BigInteger(string);
        };
    }

    private void putKeyValuePair() {
        objectStack.peek().put(key, value);
        key = null;
        value = null;
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
