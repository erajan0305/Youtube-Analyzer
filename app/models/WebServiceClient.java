package models;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSBodyWritables;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class WebServiceClient implements WSBodyReadables, WSBodyWritables {
    private final WSClient wsClient;

    @Inject
    public WebServiceClient(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    public WSClient getWsClient() {
        return wsClient;
    }

    public JsonNode fetchVideos(String searchKey) throws ExecutionException, InterruptedException {
        System.out.println("--" + searchKey);
        WSRequest request = wsClient.url("https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + searchKey + "&key=AIzaSyBt1HUXNJTAtfKyENT-yx6rrBHgHTWnHj4");
        CompletionStage<Object> responsePromise = request.get().thenApply(wsResponse -> wsResponse.getBody(json()));
        JsonNode jsonData = (JsonNode) responsePromise.toCompletableFuture().get();
        return jsonData;
    }
}
