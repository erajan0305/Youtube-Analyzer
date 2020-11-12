package models.Helper;

import models.POJO.Channel.ChannelResultItems;
import models.POJO.Comments.CommentResults;
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
    private final WSClient wsClient;
    private final String API_KEY = "AIzaSyCnECnkJrVZIjtA_1_zvbiBqkHTwfaBDlk";
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
     * @return {@link CompletionStage} of {@link SearchResults}.
     * @author Kishan Bhimani, Rajan Shah, Umang J Patel
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
     * @return {@link CompletionStage} of {@link Videos} viewCount
     * @author Rajan Shah, Kishan Bhimani, Umang J Patel
     */
    public CompletionStage<String> getViewCountByVideoId(String videoId) {
        WSRequest request = this.wsClient
                .url(BASE_URL + "videos")
                .addQueryParameter("id", videoId)
                .addQueryParameter("part", "snippet,contentDetails,statistics")
                .addQueryParameter("fields", "items(id,snippet(publishedAt,channelId,title,description,channelTitle),contentDetails(duration),statistics(viewCount, likeCount, dislikeCount, commentCount))")
                .addQueryParameter("key", API_KEY);
        return request.get().thenApply(wsResponse -> Json.parse(wsResponse.getBody()))
                .thenApply(video -> Json.fromJson(video, Videos.class))
                .thenApply(video -> (video.getItems() != null && !video.getItems().isEmpty()) ?
                        video.getItems().get(0).getStatistics().getViewCount() : "No Data")
                .toCompletableFuture();
    }

    /**
     * This method makes request to the <code>search</code> API of Youtube and fetches the video information
     * for <code>channelId,keyword</code>
     *
     * @param channelId id for which information is to be fetched
     * @param keyword   keyword for which top 10 videos is to be fetched for <code>id</code>
     * @return {@link CompletionStage} of {@link SearchResults}
     * @author Rajan Shah
     */
    public CompletionStage<SearchResults> getVideosJsonByChannelId(String channelId, String keyword) {
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
        return request.get().thenApply(wsResponse -> Json.parse(wsResponse.getBody()))
                .thenApply(wsResponse -> Json.fromJson(wsResponse, SearchResults.class))
                .toCompletableFuture();
    }

    /**
     * This method makes request to the <code>channels</code> API of Youtube and fetches the channel information
     * for <code>channelId</code>
     *
     * @param channelId id for which channel information is to be fetched
     * @return {@link CompletionStage} of {@link ChannelResultItems}
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

    /**
     * This method makes request to the <code>commentThreads</code> API of Youtube and fetches the comments
     * for <code>videoId</code>
     *
     * @param videoId id for which sentiment is to be calculated.
     * @return {@link CompletableFuture} of {@link CommentResults}
     * @author Umang J Patel
     */
    public CompletableFuture<CommentResults> getSentimentByVideoId(String videoId) {
        WSRequest request = this.wsClient
                .url(BASE_URL + "commentThreads")
                .addQueryParameter("part", "snippet")
                .addQueryParameter("maxResults", "100")
                .addQueryParameter("order", "relevance")
                .addQueryParameter("videoId", videoId)
                .addQueryParameter("fields", "items(snippet(topLevelComment(snippet(textDisplay,textOriginal))))")
                .addQueryParameter("key", API_KEY);
        return request.get().thenApply(wsResponse -> Json.parse(wsResponse.getBody()))
                .thenApplyAsync(wsResponse -> Json.fromJson(wsResponse, CommentResults.class))
                .toCompletableFuture();
    }
}