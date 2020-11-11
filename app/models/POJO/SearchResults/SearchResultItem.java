package models.POJO.SearchResults;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for Search Results Item.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "snippet",
        "viewCount",
        "commentSentiment"
})

public class SearchResultItem {
    @JsonProperty("id")
    public Id id;
    @JsonProperty("snippet")
    public Snippet snippet;
    @JsonProperty("viewCount")
    public String viewCount;
    @JsonProperty("commentSentiment")
    public String commentSentiment;

    public SearchResultItem() {
    }

    @Override
    public String toString() {
        return "SearchResultItem{" +
                "id=" + id +
                ", snippet=" + snippet +
                ", viewCount='" + viewCount + '\'' +
                ", commentSentiment='" + commentSentiment + '\'' +
                '}';
    }
}