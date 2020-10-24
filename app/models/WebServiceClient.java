package models;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSBodyWritables;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class WebServiceClient implements WSBodyReadables, WSBodyWritables {
    private final WSClient wsClient;

    @Inject
    public WebServiceClient(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    public CompletionStage<JsonNode> fetchVideos(String key) {
        WSRequest request = wsClient.url("https://www.googleapis.com/youtube/v3/search?part=snippet&q=pikachu&key=AIzaSyBt1HUXNJTAtfKyENT-yx6rrBHgHTWnHj4");
        CompletionStage<JsonNode> jsonResponsePromise = request.get().thenApply(wsResponse -> wsResponse.getBody(json()));
        System.out.println(jsonResponsePromise);
        return jsonResponsePromise;
    }
}
