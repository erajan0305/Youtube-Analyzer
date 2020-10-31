package models.POJO.Channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "viewCount",
        "commentCount",
        "subscriberCount",
        "videoCount"
})

public class ChannelStatistics {
    @JsonProperty("viewCount")
    public String viewCount;
    @JsonProperty("subscriberCount")
    public String subscriberCount;
    @JsonProperty("videoCount")
    public String videoCount;
}
