package models.POJO.SearchResults;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Model Class for Search Results.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"items"})

public class SearchResults {

    @JsonProperty("items")
    public List<SearchResultItem> items = null;

    public SearchResults() {
    }

    @Override
    public String toString() {
        return "SearchResults{" +
                "items=" + items +
                '}';
    }

    public List<String> getVideoIds() {
        return items.parallelStream().map(video -> video.id.videoId).collect(Collectors.toList());
    }
}