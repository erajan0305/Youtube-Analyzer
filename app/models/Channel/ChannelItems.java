package models.Channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "snippet",
        "statistics"
})

public class ChannelItems {
    @JsonProperty("id")
    public Id id;
    @JsonProperty("snippet")
    public Snippet snippet;
    @JsonProperty("statistics")
    public ChannelStatistics channelStatistics;
}