import json.JSONArray;
import json.JSONObject;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        JSONArray json = new JSONArray();

        JSONObject person = new JSONObject();
        person.put("firstName", "John");
        person.put("lastName", "Smith");
        person.put("isAlive", true);
        person.put("age", 25);

        JSONObject address = new JSONObject();
        address.put("street", "21 2nd Street");
        address.put("city", "New York");
        address.put("state", "NY");
        address.put("postalCode", "10021-3100");

        person.put("address", address);

        JSONArray phoneNumbers = new JSONArray();

        JSONObject homePhone = new JSONObject();
        homePhone.put("type", "home");
        homePhone.put("number", "212 555-1234");

        JSONObject officePhone = new JSONObject();
        officePhone.put("type", "office");
        officePhone.put("number", "646 555-4567");

        phoneNumbers.put(homePhone);
        phoneNumbers.put(officePhone);
        person.put("phoneNumbers", phoneNumbers);

        person.put("children", new JSONArray());
        person.put("spouse", null);

        json.put(person);

        Deque<String> keys = new ArrayDeque<>(List.of("0", "phoneNumbers"));

        System.out.println(json);
    }
}
