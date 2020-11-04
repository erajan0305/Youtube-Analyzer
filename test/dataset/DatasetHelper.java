package dataset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;

import java.io.File;
import java.io.IOException;

public class DatasetHelper {
    static ObjectMapper mapper = new ObjectMapper();

    public static <T> T jsonFileToObject(File input, Class<T> clazz) {
        try {
            JsonNode masterJSON = mapper.readTree(input);
            return Json.fromJson(masterJSON, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonNode jsonNodeFromJsonFile(File input) {
        try {
            JsonNode masterJSON = mapper.readTree(input);
            return masterJSON;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
