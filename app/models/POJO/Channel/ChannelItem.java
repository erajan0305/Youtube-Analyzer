package models.POJO.Channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "snippet",
        "statistics"
})

public class ChannelItem {
    @JsonProperty("id")
    public String id;
    @JsonProperty("snippet")
    public Snippet snippet;
    @JsonProperty("statistics")
    public ChannelStatistics channelStatistics;

    @Override
    public String toString() {
        return "ChannelItem{" +
                "id='" + id + '\'' +
                ", snippet=" + snippet +
                ", channelStatistics=" + channelStatistics +
                '}';
    }
}