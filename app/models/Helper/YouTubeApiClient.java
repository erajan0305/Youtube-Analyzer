package models.Helper;

import models.POJO.Channel.ChannelResultItems;
import models.POJO.SearchResults.SearchResults;
import models.POJO.VideoSearch.Videos;
import play.libs.Json;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSBodyWritables;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * This class makes requests to YOUTUBE API V3 to fetch content based on parameters.
 */
public class YouTubeApiClient implements WSBodyReadables, WSBodyWritables {
    public WSClient wsClient;
    private final String API_KEY = "AIzaSyDSdXwds9Ok_eoNmxWiqNfXLQ5SjG0AuBQ";
    public String BASE_URL = "https://www.googleapis.com/youtube/v3/";

    public YouTubeApiClient(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    /**
     * <p>
     * This method makes request to the <code>search</code> API of Youtube and
     * fetches the top 10 videos for the <code>searchKey</code> sorted by date of upload.
     * </p>
     *
     * @param searchKey this is the key for which search request is executed
     * @return CompletionStage of {@link SearchResults}.
     * @author Rajan Shah
     */
    public CompletionStage<SearchResults> fetchVideos(String searchKey) {
        WSRequest request = this.wsClient
                .url(BASE_URL + "search")
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

    /**
     * This method makes request to the <code>videos</code> API of Youtube and fetches the video information
     * for <code>videoId</code>
     *
     * @param videoId id for which information is to be fetched
     * @return CompletionStage of {@link Videos} viewCount
     * @author Rajan Shah
     */
    public CompletionStage<String> getVideoJsonByVideoId(String videoId) {
        WSRequest request = this.wsClient
                .url(BASE_URL + "videos")
                .addQueryParameter("id", videoId)
                .addQueryParameter("part", "snippet,contentDetails,statistics")
                .addQueryParameter("fields", "items(id,snippet(publishedAt,channelId,title,description,channelTitle),contentDetails(duration),statistics(viewCount, likeCount, dislikeCount, favouriteCount, commentCount))")
                .addQueryParameter("key", API_KEY);
        return request.get().thenApply(wsResponse -> Json.parse(wsResponse.getBody()))
                .thenApply(video -> Json.fromJson(video, Videos.class))
                .thenApply(video -> video.items.get(0).statistics.viewCount)
                .toCompletableFuture();
    }

    /**
     * This method makes request to the <code>search</code> API of Youtube and fetches the video information
     * for <code>channelId,keyword</code>
     *
     * @param channelId id for which information is to be fetched
     * @param keyword   keyword for which top 10 videos is to be fetched for <code>id</code>
     * @return CompletionStage of {@link SearchResults}
     * @author Rajan Shah
     */
    public CompletableFuture<SearchResults> getVideosJsonByChannelId(String channelId, String keyword) {
        WSRequest request = this.wsClient
                .url(BASE_URL + "search")
                .addQueryParameter("channelId", channelId)
                .addQueryParameter("q", keyword)
                .addQueryParameter("maxResults", "10")
                .addQueryParameter("type", "videos")
                .addQueryParameter("order", "date")
                .addQueryParameter("part", "snippet")
                .addQueryParameter("fields", "items(id,snippet(publishedAt,channelId,channelTitle,title,description,publishTime))")
                .addQueryParameter("key", API_KEY);
        return request.get().thenApply(wsResponse -> {
            System.out.println(wsResponse);
            return Json.parse(wsResponse.getBody());
        }).thenApply(wsResponse -> Json.fromJson(wsResponse, SearchResults.class))
                .toCompletableFuture();
    }

    /**
     * This method makes request to the <code>channels</code> API of Youtube and fetches the channel information
     * for <code>channelId</code>
     *
     * @param channelId id for which channel information is to be fetched
     * @return CompletionStage of {@link ChannelResultItems}
     * @author Rajan Shah
     */
    public CompletionStage<ChannelResultItems> getChannelInformationByChannelId(String channelId) {
        WSRequest request = this.wsClient
                .url(BASE_URL + "channels")
                .addQueryParameter("id", channelId)
                .addQueryParameter("part", "snippet,statistics")
                .addQueryParameter("fields", "items(id, snippet(title, description, customUrl, publishedAt, country), statistics(viewCount, subscriberCount, videoCount))")
                .addQueryParameter("key", API_KEY);
        return request.get().thenApply(wsResponse -> Json.parse(wsResponse.getBody()))
                .thenApply(channelJsonNode -> Json.fromJson(channelJsonNode, ChannelResultItems.class))
                .toCompletableFuture();
    }
}