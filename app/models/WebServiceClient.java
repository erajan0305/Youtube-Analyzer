package models;

import com.fasterxml.jackson.databind.JsonNode;
import models.VIdeoSearch.Videos;
import play.libs.Json;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSBodyWritables;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class WebServiceClient implements WSBodyReadables, WSBodyWritables {
    private final WSClient wsClient;
    private final String API_KEY = "AIzaSyAW3TfIG7ebUDcVQaYWHWPha3CXiATdzGE";

    @Inject
    public WebServiceClient(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    public WSClient getWsClient() {
        return wsClient;
    }

    public JsonNode fetchVideos(String searchKey) throws ExecutionException, InterruptedException {
        WSRequest request = wsClient
                .url("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&order=date&q=" + searchKey + "&fields=items(id,snippet(publishedAt,channelId,channelTitle,title,description,publishTime))&key=" + API_KEY);
        CompletionStage<Object> responsePromise = request.get().thenApply(wsResponse -> wsResponse.getBody(json()));
        JsonNode jsonData = (JsonNode) responsePromise.toCompletableFuture().get();
        return jsonData;
    }


    public String getVideoJsonByVideoId(String videoId) throws ExecutionException, InterruptedException {
        WSRequest request = wsClient.url("https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics&id=" + videoId + "&fields=items(id,snippet(publishedAt,channelId,title,description,channelTitle),contentDetails(duration),statistics(viewCount,likeCount,dislikeCount,commentCount))&key=" + API_KEY);
        CompletionStage<Object> responsePromise = request.get().thenApply(wsResponse -> wsResponse.getBody(json()));
        JsonNode jsonData = (JsonNode) responsePromise.toCompletableFuture().get();
        Videos videos = Json.fromJson(jsonData, Videos.class);
        if (videos.items != null && !videos.items.isEmpty()) {
            return videos.items.get(0).statistics.viewCount;
        }
        return "No Data Available";
    }
}
