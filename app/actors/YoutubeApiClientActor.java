package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.POJO.Channel.ChannelResultItems;
import models.POJO.Comments.CommentResults;
import models.POJO.SearchResults.SearchResultItem;
import models.POJO.SearchResults.SearchResults;
import models.POJO.VideoSearch.Videos;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import scala.compat.java8.FutureConverters;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static akka.pattern.Patterns.ask;

public class YoutubeApiClientActor extends AbstractActor {
    private WSClient wsClient;
    // private final String API_KEY = "AIzaSyC3b5LuRNndEHOlKdir8ReTMOec1A5t1n4";
    // private final String API_KEY = "AIzaSyAW3TfIG7ebUDcVQaYWHWPha3CXiATdzGE";
    // private final String API_KEY = "AIzaSyCvQ6FlySOyJn68Omj5Y6ItdwGPSFSP-ZQ";
    // private final String API_KEY = "AIzaSyCyAb62tFZSq2Hek-YgnlyaL7F4x2AlH0k";
    // private final String API_KEY = "AIzaSyA7X8mzniYR7inFmDlAZegOdUazCuDntCk";
    private final String API_KEY = "AIzaSyDCo0jpTa1TPM4afzuoG0-lZjm0OQPsL4s";

    public String BASE_URL = "https://www.googleapis.com/youtube/v3/";

    public static class SetWSClient {
        public WSClient getWsClient() {
            return wsClient;
        }

        private final WSClient wsClient;

        public SetWSClient(WSClient wsClient) {
            this.wsClient = wsClient;
        }
    }

    public static class SetBaseUrl {
        private final String baseUrl;

        public SetBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public static class FetchVideos {
        public String getSearchKey() {
            return searchKey;
        }

        private final String searchKey;

        public FetchVideos(String searchKey) {
            this.searchKey = searchKey;
        }
    }

    public static class GetViewCountByVideoId {
        private final String videoId;

        public GetViewCountByVideoId(String videoId) {
            this.videoId = videoId;
        }
    }

    public static class GetVideosJsonByChannelId {
        private final String channelId;
        private final String keyword;

        public String getChannelId() {
            return channelId;
        }

        public String getKeyword() {
            return keyword;
        }

        public GetVideosJsonByChannelId(String channelId, String keyword) {
            this.channelId = channelId;
            this.keyword = keyword;
        }
    }

    public static class GetChannelInformationByChannelId {

        public String getChannelId() {
            return channelId;
        }

        private final String channelId;

        public GetChannelInformationByChannelId(String channelId) {
            this.channelId = channelId;
        }
    }

    public static class GetSentimentByVideoId {
        public String getVideoId() {
            return videoId;
        }

        private final String videoId;

        public GetSentimentByVideoId(String videoId) {
            this.videoId = videoId;
        }
    }

    public static Props props(WSClient wsClient) {
        return Props.create(YoutubeApiClientActor.class, wsClient);
    }

    @Inject
    public YoutubeApiClientActor(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    @Override
    public Receive createReceive() {
        // changes made to wsClient condition. Might produce error.
        return receiveBuilder()
                .match(SetWSClient.class, t -> this.wsClient = t.wsClient)
                .match(SetBaseUrl.class, t -> BASE_URL = t.baseUrl)
                .match(FetchVideos.class, t -> {
                    ActorRef emojiAnalyzerActor = getContext().getSystem().actorOf(EmojiAnalyzerActor.props());
                    SearchResults searchResults = this.fetchVideos(t.searchKey);
                    // Apply getter/setter to search result items in search results
                    if (searchResults.getItems() == null) {
                        getSender().tell(searchResults, getSelf());
                        return;
                    }
                    List<SearchResultItem> answer =
                            searchResults.getItems().parallelStream()
                                    .map(searchResultItem -> this.getViewCountByVideoId(searchResultItem.getId().getVideoId()).toCompletableFuture()
                                            .thenCombineAsync(
                                                    FutureConverters.toJava(
                                                            ask(emojiAnalyzerActor, new EmojiAnalyzerActor.GetAnalysis(getSentimentByVideoId(searchResultItem.getId().getVideoId())), 2000))
                                                            .thenApplyAsync(item -> (CompletableFuture<String>) item)
                                                            .toCompletableFuture()
                                                            .thenApplyAsync(CompletableFuture::join)
                                                    , (String viewCount, String emoji) -> {
                                                        searchResultItem.setViewCount(viewCount);
                                                        searchResultItem.setCommentSentiment(emoji);
                                                        return searchResultItem;
                                                    }
                                            )).map(CompletableFuture::join)
                                    .collect(Collectors.toList());
                    searchResults.setItems(answer);
                    getSender().tell(searchResults, getSelf());
                })
                .match(GetViewCountByVideoId.class, t -> getSender().tell(this.getViewCountByVideoId(t.videoId), getSelf()))
                .match(GetVideosJsonByChannelId.class, t -> getSender().tell(this.getVideosJsonByChannelId(t.channelId, t.keyword), getSelf()))
                .match(GetChannelInformationByChannelId.class, t -> getSender().tell(this.getChannelInformationByChannelId(t.channelId), getSelf()))
                .match(GetSentimentByVideoId.class, t -> getSender().tell(this.getSentimentByVideoId(t.videoId), getSelf()))
                .build();
    }

    public SearchResults fetchVideos(String searchKey) {
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
                .toCompletableFuture().join();
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
    public SearchResults getVideosJsonByChannelId(String channelId, String keyword) {
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
                .toCompletableFuture()
                .join();
    }

    /**
     * This method makes request to the <code>channels</code> API of Youtube and fetches the channel information
     * for <code>channelId</code>
     *
     * @param channelId id for which channel information is to be fetched
     * @return {@link CompletionStage} of {@link ChannelResultItems}
     * @author Rajan Shah
     */
    public ChannelResultItems getChannelInformationByChannelId(String channelId) {
        WSRequest request = this.wsClient
                .url(BASE_URL + "channels")
                .addQueryParameter("id", channelId)
                .addQueryParameter("part", "snippet,statistics")
                .addQueryParameter("fields", "items(id, snippet(title, description, customUrl, publishedAt, country), statistics(viewCount, subscriberCount, videoCount))")
                .addQueryParameter("key", API_KEY);
        return request.get().thenApply(wsResponse -> Json.parse(wsResponse.getBody()))
                .thenApply(channelJsonNode -> Json.fromJson(channelJsonNode, ChannelResultItems.class))
                .toCompletableFuture()
                .join();
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
        return request.stream().thenApplyAsync(wsResponse -> Json.parse(wsResponse.getBody()))
                .thenApplyAsync(wsResponse -> Json.fromJson(wsResponse, CommentResults.class))
                .toCompletableFuture();
    }
}
