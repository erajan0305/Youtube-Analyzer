package models;

import models.Channel.ChannelItems;
import models.SearchResults.SearchResults;
import models.VIdeoSearch.Videos;
import play.libs.Json;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSBodyWritables;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class YouTubeClient implements WSBodyReadables, WSBodyWritables {
    private final WSClient wsClient;
    private final String API_KEY = "AIzaSyAW3TfIG7ebUDcVQaYWHWPha3CXiATdzGE";

    @Inject
    public YouTubeClient(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    public CompletionStage<SearchResults> fetchVideos(String searchKey) {
        WSRequest request = this.wsClient
                .url("https://www.googleapis.com/youtube/v3/search")
                .addQueryParameter("part", "snippet")
                .addQueryParameter("maxResults", "10")
                .addQueryParameter("type", "videos")
                .addQueryParameter("order", "date")
                .addQueryParameter("q", searchKey)
                .addQueryParameter("fields", "items(id,snippet(publishedAt,channelId,channelTitle,title,description,publishTime))")
                .addQueryParameter("key", API_KEY);
        return request.get().thenApply(wsResponse -> Json.parse(wsResponse.getBody()))
                .thenApply(wsResponse -> Json.fromJson(wsResponse, SearchResults.class))
                .toCompletableFuture();
    }


    public CompletionStage<String> getVideoJsonByVideoId(String videoId) {
        WSRequest request = this.wsClient
                .url("https://www.googleapis.com/youtube/v3/videos")
                .addQueryParameter("id", videoId)
                .addQueryParameter("part", "snippet,contentDetails,statistics")
                .addQueryParameter("fields", "items(id,snippet(publishedAt,channelId,title,description,channelTitle),contentDetails(duration),statistics(viewCount, likeCount, dislikeCount, favouriteCount, commentCount))")
                .addQueryParameter("key", API_KEY);
        return request.get().thenApply(wsResponse -> Json.parse(wsResponse.getBody()))
                .thenApply(video -> Json.fromJson(video, Videos.class))
                .thenApply(video -> video.items.get(0).statistics.viewCount)
                .toCompletableFuture();
    }

    public CompletionStage<String> getVideosJsonByChannelId(String channelId) {
        WSRequest request = this.wsClient
                .url("https://www.googleapis.com/youtube/v3/videos")
                .addQueryParameter("channelId", channelId)
                .addQueryParameter("maxResults", "10")
                .addQueryParameter("order", "date")
                .addQueryParameter("part", "snippet,contentDetails,statistics")
                .addQueryParameter("fields", "items(id,snippet(publishedAt,channelId,title,description,channelTitle),contentDetails(duration),statistics(viewCount, likeCount, dislikeCount, favouriteCount, commentCount))")
                .addQueryParameter("key", API_KEY);
        return request.get().thenApply(wsResponse -> Json.parse(wsResponse.getBody()))
                .thenApply(video -> Json.fromJson(video, Videos.class))
                .thenApply(video -> video.items.get(0).statistics.viewCount)
                .toCompletableFuture();
    }

    public CompletionStage<ChannelItems> getChannelInformationByChannelId(String channelId) {
        //TODO: Implement in a similar way as getVideoJsonByVideoId
        return null;
    }
}
