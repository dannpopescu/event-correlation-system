package com.danpopescu.eventcorrelation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JSONTest {

    private static final String RESOURCES_PATH = "src/test/resources/";

    private String trafficFile;
    private String queryFile;
    private String refFile;
    private String outputFile;

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void testOutput(int fileNumber) {
        updateResourceFileNames(fileNumber);
        JSON json = generateJSON();
        generateOutput(json);
        validateOutput();
    }

    private JSON generateJSON() {
        JSONParser parser = new JSONParser();
        try {
            return parser.parse(trafficFile);
        } catch (IOException e) {
            fail("IOException when loading the json file for parsing.");
        }
        return null;
    }

    private void validateOutput() {
        try {
            assertEquals(
                    new String(Files.readAllBytes(Path.of(refFile))),
                    new String(Files.readAllBytes(Path.of(outputFile))));
        } catch (IOException e) {
            fail("IOException when validating the output.");
        }
    }

    private void generateOutput(JSON json) {
        try (Scanner queries = new Scanner(new BufferedReader(new FileReader(queryFile)));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            while (queries.hasNextLine()) {
                String query = queries.nextLine();
                Deque<String> tokens = parseQuery(query);
                Optional<String> outputMessage = executeQuery(json, tokens);

                if (outputMessage.isPresent()) {
                    writer.write(outputMessage.get() + "\n");
                }
            }
        } catch (IOException e) {
            fail("IOException when loading the input and output files.");
        }
    }

    private Optional<String> executeQuery(JSON json, Deque<String> queryTokens) {
        String outputMessage = null;
        switch (queryTokens.poll()) {
            case "GET":
                try {
                    Object obj = json.get(queryTokens);
                    if (obj instanceof JSONObject) {
                        outputMessage = "JSON_OBJECT";
                    } else if (obj instanceof JSONArray) {
                        outputMessage = "JSON_ARRAY";
                    } else {
                        outputMessage = obj.toString();
                    }
                } catch (JSONException e) {
                    outputMessage = "GET_" + e.getMessage();
                }
                break;

            case "PUT":
                try {
                    String value = queryTokens.pollLast();
                    json.put(queryTokens, value);
                } catch (JSONException e) {
                    outputMessage = "PUT_" + e.getMessage();
                }
                break;

            case "DEL":
                try {
                    json.delete(queryTokens);
                } catch (JSONException e) {
                    outputMessage = "DEL_" + e.getMessage();
                }
        }
        return Optional.ofNullable(outputMessage);
    }

    private Deque<String> parseQuery(String query) {
        Deque<String> tokens = new ArrayDeque<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(query);
        while (m.find()) {
            tokens.add(m.group().strip());
        }
        return tokens;
    }

    private void updateResourceFileNames(int fileNumber) {
        trafficFile = RESOURCES_PATH + "traffic/traffic_" + fileNumber + ".json";
        queryFile = RESOURCES_PATH + "input/query_" + fileNumber + ".txt";
        refFile = RESOURCES_PATH + "ref/ref_" + fileNumber + ".txt";
        outputFile = RESOURCES_PATH + "output/out_"+ fileNumber + ".txt";
    }
}