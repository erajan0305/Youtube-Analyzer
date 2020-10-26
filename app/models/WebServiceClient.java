package models;

import models.SearchResults.SearchResultItem;
import models.SearchResults.SearchResults;
import models.VIdeoSearch.Videos;
import play.libs.Json;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSBodyWritables;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

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

    public CompletionStage<SearchResults> fetchVideos(String searchKey) {
        WSRequest request = this.wsClient
                .url("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&order=date&q=" + searchKey + "&fields=items(id,snippet(publishedAt,channelId,channelTitle,title,description,publishTime))&key=" + API_KEY);
        return request.get().thenApply(wsResponse -> Json.parse(wsResponse.getBody()))
                .thenApply(wsResponse -> Json.fromJson(wsResponse, SearchResults.class))
                .thenApply(searchResults -> searchResults.appendViewsCountToItems(searchResults));
    }


    public void getVideoJsonByVideoId(String videoId, SearchResultItem item) {
        System.out.println(videoId);
        WSRequest request = this.wsClient.url("https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics&id=" + videoId + "&fields=items(id,snippet(publishedAt,channelId,title,description,channelTitle),contentDetails(duration),statistics(viewCount))&key=" + API_KEY);
        request.get().thenApply(wsResponse -> Json.parse(wsResponse.getBody()))
                .thenApply(video -> Json.fromJson(video, Videos.class))
                .thenApply(video -> video.items.get(0).statistics.viewCount)
                .thenAccept(viewsCount -> {
                    item.viewCount = viewsCount;
                });
    }
}
