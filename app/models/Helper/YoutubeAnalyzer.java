package models.Helper;

import models.POJO.Channel.ChannelResultItems;
import models.POJO.SearchResults.SearchResults;
import play.libs.ws.WSClient;

import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.*;

/**
 * Helper class to make requests to {@link YouTubeApiClient} and perform calculations on data.
 */
public class YoutubeAnalyzer {
    public WSClient wsClient;
    public YouTubeApiClient youTubeApiClient;

    public YoutubeAnalyzer() {
    }

    /**
     * @param wsClient WSClient Object to make External API calls
     */
    public void setWsClient(WSClient wsClient) {
        this.wsClient = wsClient;
        this.youTubeApiClient = new YouTubeApiClient(this.wsClient);
    }

    /**
     * Instantiates {@link YouTubeApiClient}
     *
     * @param youTubeApiClient YoutubeApiClient Object
     */
    public void setYouTubeApiClient(YouTubeApiClient youTubeApiClient) {
        this.youTubeApiClient = youTubeApiClient;
    }

    /**
     * This is a helper method that calls the {@link YouTubeApiClient}'s <code>getVideosJsonByChannelId</code> method.
     *
     * @param channelId id for which information is to be fetched
     * @param keyword   keyword for which top 10 videos is to be fetched for <code>id</code>
     * @return CompletionStage of {@link SearchResults}
     * @author Kishan Bhimani, Rajan Shah, Umang J Patel
     */
    public CompletionStage<SearchResults> getVideosJsonByChannelId(String channelId, String keyword) {
        return youTubeApiClient.getVideosJsonByChannelId(channelId, keyword);
    }

    /**
     * This is a helper method that calls the {@link YouTubeApiClient}'s <code>fetchVideos</code> method
     *
     * @param searchKeyword this is the key for which method is executed
     * @return CompletionStage of {@link SearchResults}.
     * @author Rajan Shah, Kishan Bhimani, Umang J Patel
     */
    public CompletionStage<SearchResults> fetchVideos(String searchKeyword) {
        return youTubeApiClient.fetchVideos(searchKeyword);
    }

    /**
     * This is a helper method that Calculates similarity-level statistic for videos from {@link SearchResults}, counting all unique words in the
     * video title in descending order.
     *
     * @param searchResultsLinkedHashMap {@link LinkedHashMap} of all {@link SearchResults}.
     * @param keyword                    key to get {@link SearchResults} from <code>searchResultsLinkedHashMap</code>.
     * @return {@link Map} that contains uniqueWord of type {@link String} as key and
     * count of type {@link Long} as a value.
     * @author Kishan Bhimani
     */
    public Map<String, Long> getSimilarityStats(LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap, String keyword) {
        SearchResults searchResults = searchResultsLinkedHashMap.get(keyword);
        if (searchResults == null || (searchResults.items == null || searchResults.items.size() == 0)) {
            return new HashMap<String, Long>() {{
                put(keyword, (long) 0);
            }};
        }
        List<String> tokens = searchResults
                .items
                .stream()
                .map(searchResultItem -> searchResultItem.snippet.title)
                .flatMap(title -> Arrays.stream(title.split("\\s+").clone()))    // split into words
                .map(s -> s.replaceAll("[^a-zA-Z0-9]", ""))          // discarding special characters
                .filter(s -> !s.matches("[0-9]+"))                              // discarding only number strings
                .filter(s -> !s.isEmpty() && s.length() > 1)                          // accept only non empty string with length more than 1
                .collect(toList());

        return tokens.stream()
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(identity(), counting()))    // creates map of (unique words, count)
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(reverseOrder()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a,
                        LinkedHashMap::new));
    }

    /**
     * This is a helper method that calls the {@link YouTubeApiClient}'s <code>getVideoJsonByVideoId</code> method
     *
     * @param videoId this is the video for which method is executed
     * @return CompletionStage of {@link SearchResults}.
     * @author Rajan Shah
     */
    public CompletionStage<String> getViewCountByVideoId(String videoId) {
        return youTubeApiClient.getViewCountByVideoId(videoId);
    }

    /**
     * This is a helper method that calls {@link YouTubeApiClient}'s <code>getChannelInformationByChannelId</code> method.
     *
     * @param channelId id for which channel information is to be fetched
     * @return CompletionStage of {@link ChannelResultItems}
     * @author Rajan Shah
     */
    public CompletionStage<ChannelResultItems> getChannelInformationByChannelId(String channelId) {
        return youTubeApiClient.getChannelInformationByChannelId(channelId);
    }

    /**
     * This is a helper method that calls {@link YouTubeApiClient}'s <code>getSentimentForVideos</code> method
     *
     * @param searchKey is the search keyword used to search for the videos
     * @return CompletionStage of {@link List<String>} i.e list of sentiments (emoji) for each video.
     * @author Umang J Patel
     */
    public CompletionStage<List<String>> getVideoSentiments(String searchKey) {
        return youTubeApiClient.getSentimentForVideos(searchKey);
    }

    /**
     * This is a helper method that calls {@link YouTubeApiClient}'s <code>getSentimentByVideo</code> method
     *
     * @param videoId is the ID of a particular video
     * @return CompletionStage of {@link String} i.e sentiment (emoji) for a particular video.
     * @author Umang J Patel
     */
    public CompletionStage<String> getSentimentPerVideo(String videoId) {
        return youTubeApiClient.getSentimentByVideoId(videoId);
    }
}
