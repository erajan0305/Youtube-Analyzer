package models.Helper;

import models.POJO.Channel.ChannelResultItems;
import models.POJO.SearchResults.SearchResults;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.*;

public class YoutubeAnalyzer {
    @Inject
    WSClient wsClient;
    YouTubeApiClient youTubeApiClient;

    public YoutubeAnalyzer() {
    }

    public YoutubeAnalyzer(WSClient wsClient) {
        this.wsClient = wsClient;
        youTubeApiClient = new YouTubeApiClient(this.wsClient);
    }

    public CompletionStage<SearchResults> getVideosJsonByChannelId(String channelId) {
        return youTubeApiClient.getVideosJsonByChannelId(channelId);
    }

    public CompletionStage<SearchResults> fetchVideos(String searchKeyword) {
        return youTubeApiClient.fetchVideos(searchKeyword);
    }

    public Map<String, Long> getSimilarityStats(LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap, String keyword) {
        List<String> tokens = searchResultsLinkedHashMap
                .get(keyword)
                .items
                .stream()
                .map(searchResultItem -> searchResultItem.snippet.title)
                .flatMap(title -> Arrays.stream(title.split("\\s+").clone()))   // split into words
                .map(s -> s.replaceAll("[^\\p{Alpha}]", ""))    // removing special characters
                .filter(s -> !s.isEmpty())
                .collect(toList());

        return tokens.stream()
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(identity(), counting()))    // creates map of (unique words, count)
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(reverseOrder()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a,
                        LinkedHashMap::new));
    }

    public CompletionStage<String> getVideosJsonByVideoId(String videoId) {
        return youTubeApiClient.getVideoJsonByVideoId(videoId);
    }

    public CompletionStage<ChannelResultItems> getChannelInformationByChannelId(String channelId) {
        return youTubeApiClient.getChannelInformationByChannelId(channelId);
    }
}
