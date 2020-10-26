package models.SearchResults;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import models.WebServiceClient;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"items"})

public class SearchResults {

    @Inject
    WSClient wsClient;

    WebServiceClient webServiceClient;

    public SearchResults() {
    }

    @JsonProperty("items")
    public List<SearchResultItem> items = null;

    public String searchResultsAsString() {
        return "Video Id: " + items.stream()
                .map(item -> item.id.videoId)
                .collect(Collectors.joining(", "));
    }

    public SearchResults appendViewsCountToItems(SearchResults searchResults) {
        webServiceClient = new WebServiceClient(wsClient);
        searchResults.items.forEach(item -> {
            System.out.println(item.snippet.channelId);
            System.out.println(item.snippet.channelTitle);
            System.out.println(item.snippet.description);
            System.out.println(item.snippet.publishedAt);
            System.out.println(item.snippet.title);
            System.out.println(item.snippet.publishTime);
            System.out.println(item.id.videoId);
            webServiceClient.getVideoJsonByVideoId(item.id.videoId, item);
            System.out.println(item.viewCount);
        });
        return searchResults;
    }
}