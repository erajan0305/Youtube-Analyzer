package models.POJO.VIdeoSearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"viewCount", "likeCount", "dislikeCount", "favouriteCount", "commentCount"})

public class VideoStatistics {
    @JsonProperty("viewCount")
    public String viewCount;
    @JsonProperty("likeCount")
    public String likeCount;
    @JsonProperty("dislikeCount")
    public String dislikeCount;
    @JsonProperty("favouriteCount")
    public String favouriteCount;
    @JsonProperty("commentCount")
    public String commentCount;

    @Override
    public String toString() {
        return "VideoStatistics{" +
                "viewCount='" + viewCount + '\'' +
                ", likeCount='" + likeCount + '\'' +
                ", dislikeCount='" + dislikeCount + '\'' +
                ", favouriteCount='" + favouriteCount + '\'' +
                ", commentCount='" + commentCount + '\'' +
                '}';
    }
}
