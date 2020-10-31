package models.POJO.SearchResults;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "publishedAt",
        "channelId",
        "title",
        "description",
        "channelTitle",
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
    @JsonProperty("channelTitle")
    public String channelTitle;
    @JsonProperty("publishTime")
    public String publishTime;

    public Snippet() {
    }

    public Snippet(String channelId, String channelTitle, String title, String description, String publishedAt, String publishTime) {
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.description = description;
        this.title = title;
        this.publishedAt = publishedAt;
        this.publishTime = publishTime;
    }
}
