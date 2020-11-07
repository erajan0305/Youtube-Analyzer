package models.POJO.Channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for {@link ChannelItem} Statistics.
 */

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

    public ChannelStatistics() {
    }

    public ChannelStatistics(String videoCount, String viewCount, String subscriberCount) {
        this.viewCount = viewCount;
        this.videoCount = videoCount;
        this.subscriberCount = subscriberCount;
    }

    @Override
    public String toString() {
        return "ChannelStatistics{" +
                "viewCount='" + viewCount + '\'' +
                ", subscriberCount='" + subscriberCount + '\'' +
                ", videoCount='" + videoCount + '\'' +
                '}';
    }
}
