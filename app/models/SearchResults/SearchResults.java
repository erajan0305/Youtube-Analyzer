package models.SearchResults;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.stream.Collectors;


@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "kind",
        "etag",
        "nextPageToken",
        "regionCode",
        "pageInfo",
        "items"
})
public class SearchResults {

    public SearchResults(){}
    @JsonProperty("kind")
    public String kind;
    @JsonProperty("etag")
    public String etag;
    @JsonProperty("nextPageToken")
    public String nextPageToken;
    @JsonProperty("regionCode")
    public String regionCode;
    @JsonProperty("pageInfo")
    public PageInfo pageInfo;
    @JsonProperty("items")
    public List<SearchResultItem> items = null;

    public String searchResultsAsString(){
        return "Kind: " + kind +
                "\nEtag : " + etag+
                "\nRegion Code: " + nextPageToken +
                "\nTotal Results: " + pageInfo.totalResults +
                "\nVideo Id: " + items.stream()
                .map(item -> item.id.videoId)
                .collect(Collectors.joining(" , "));
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "totalResults",
        "resultsPerPage"
})
class PageInfo {
    @JsonProperty("totalResults")
    public int totalResults;
    @JsonProperty("resultsPerPage")
    public int resultsPerPage;
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "url",
        "width",
        "height"
})
class Default {
    @JsonProperty("url")
    public String url;
    @JsonProperty("width")
    public int width;
    @JsonProperty("height")
    public int height;
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "url",
        "width",
        "height"
})
class Medium {
    @JsonProperty("url")
    public String url;
    @JsonProperty("width")
    public int width;
    @JsonProperty("height")
    public int height;
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "url",
        "width",
        "height"
})
class High {
    @JsonProperty("url")
    public String url;
    @JsonProperty("width")
    public int width;
    @JsonProperty("height")
    public int height;
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "default",
        "medium",
        "high"
})
class Thumbnails {
    @JsonProperty("default")
    public Default _default;
    @JsonProperty("medium")
    public Medium medium;
    @JsonProperty("high")
    public High high;
}