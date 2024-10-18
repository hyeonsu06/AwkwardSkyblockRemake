package io.hyonsu06.core;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class DataMapManager {
    public static Map<?, ?> loadObjectFromJson(String fileName) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(fileName)) {
            return gson.fromJson(reader, Map.class);  // Deserialize JSON back into the object
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void saveObjectToJson(Map<?, ?> obj, String fileName) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(obj, writer);  // Serialize the object to JSON and write to file
            System.out.println("Object saved to JSON file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
