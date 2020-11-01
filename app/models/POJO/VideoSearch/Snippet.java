package models.POJO.VideoSearch;

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

    @Override
    public String toString() {
        return "Snippet{" +
                "publishedAt='" + publishedAt + '\'' +
                ", channelId='" + channelId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", channelTitle='" + channelTitle + '\'' +
                ", publishTime='" + publishTime + '\'' +
                '}';
    }
}
