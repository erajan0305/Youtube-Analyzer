package models.SearchResults;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "publishedAt",
        "channelId",
        "title",
        "description",
        "thumbnails",
        "channelTitle",
        "liveBroadcastContent",
        "publishTime"
})
public class Snippet {
    @JsonProperty("publishedAt")
    public String publishedAt;
    @JsonProperty("channelId")
    public String channelId;
    @JsonProperty("title")
    public String title;
    @JsonProperty("description")
    public String description;
    @JsonProperty("thumbnails")
    public Thumbnails thumbnails;
    @JsonProperty("channelTitle")
    public String channelTitle;
    @JsonProperty("liveBroadcastContent")
    public String liveBroadcastContent;
    @JsonProperty("publishTime")
    public String publishTime;
}
