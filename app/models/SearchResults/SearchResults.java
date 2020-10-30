package models.SearchResults;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import models.Helper.YouTubeClient;
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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"items"})

public class SearchResults {

    public SearchResults() {
    }

    @Inject
    WSClient wsClient;

    public SearchResults(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    @JsonProperty("items")
    public List<SearchResultItem> items = null;

    public String searchResultsAsString() {
        return "Video Id: " + items.stream()
                .map(item -> item.id.videoId)
                .collect(Collectors.joining(", "));
    }

    public CompletionStage<SearchResults> getVideosJsonByChannelId(String channelId) {
        YouTubeClient youTubeClient = new YouTubeClient(this.wsClient);
        return youTubeClient.getVideosJsonByChannelId(channelId);
    }

    public CompletionStage<SearchResults> fetchVideos(String searchKeyword) {
        YouTubeClient youTubeClient = new YouTubeClient(this.wsClient);
        return youTubeClient.fetchVideos(searchKeyword);
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
}