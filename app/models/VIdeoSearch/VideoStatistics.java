package models.VIdeoSearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "viewCount",
        "likeCount",
        "dislikeCount",
        "favoriteCount",
        "commentCount"
})
public class VideoStatistics {
    @JsonProperty("viewCount")
    public String viewCount;
    @JsonProperty("likeCount")
    public String likeCount;
    @JsonProperty("dislikeCount")
    public String dislikeCount;
    @JsonProperty("favoriteCount")
    public String favoriteCount;
    @JsonProperty("commentCount")
    public String commentCount;
}
