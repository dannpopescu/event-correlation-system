import json.CharParser;
import json.JSONParser;

import java.io.IOException;

public class Main {

    static JSONParser jsonParser = new JSONParser();
    static CharParser charParser = new CharParser();

    static String path = "projects/eventcorrsystem/src/test/resources/traffic/traffic_5.json";

    public static void main(String[] args) throws IOException {
        long[] jsonParserScores = new long[100];
        long[] charParserScores = new long[100];

        for (int i = 0; i < 100; i++) {
            charParserScores[i] = timeChar();
            System.out.println(i);
        }

        for (int i = 0; i < 100; i++) {
            jsonParserScores[i] = timeJson();
            System.out.println(i);
        }



        System.out.println("JSONParser score: " + average(jsonParserScores));
        System.out.println("CharParser score: " + average(charParserScores));

    }

    public static double average(long[] array) {
        double average = 0;
        for (int i = 0; i < 100; i++) {
            average += array[i] / 100.0;
        }
        return average;
    }

    public static long timeJson() throws IOException {
        long start = System.nanoTime();
        jsonParser.parse(path);
        return System.nanoTime() - start;
    }

    public static long timeChar() throws IOException {
        long start = System.nanoTime();
        charParser.parse(path);
        return System.nanoTime() - start;
    }
}
