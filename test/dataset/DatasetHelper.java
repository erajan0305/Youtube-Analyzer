package dataset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;

import java.io.File;
import java.io.IOException;

/**
 * This class helps test server of {@link actors.YoutubeApiClientActor} to serialize and
 * deserialize JSON to POJO and vice versa.
 *
 * @author Kishan Bhimani
 */
public class DatasetHelper {
    static ObjectMapper mapper = new ObjectMapper();

    /**
     * This method maps JSON file to POJO object
     *
     * @param input JSON file
     * @param clazz POJO class to be generated from <code>input</code>
     * @param <T>   This describes my parameter type
     * @return object of type {@link T}
     * @author Kishan Bhimani
     */
    public static <T> T jsonFileToObject(File input, Class<T> clazz) {
        try {
            JsonNode masterJSON = mapper.readTree(input);
            return Json.fromJson(masterJSON, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This methods maps JSON file to {@link JsonNode} object.
     *
     * @param input JSON file
     * @return object of type {@link JsonNode}
     * @author Kishan Bhimani
     */
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
