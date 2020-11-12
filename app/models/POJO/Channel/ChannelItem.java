package models.POJO.Channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for Channel Item.
 *
 * @author Rajan Shah
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "snippet",
        "statistics"
})

public class ChannelItem {
    @JsonProperty("id")
    private String id;
    @JsonProperty("snippet")
    private Snippet snippet;
    @JsonProperty("statistics")
    private ChannelStatistics channelStatistics;

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets snippet.
     *
     * @return the snippet
     */
    public Snippet getSnippet() {
        return snippet;
    }

    /**
     * Sets snippet.
     *
     * @param snippet the snippet
     */
    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }

    /**
     * Gets channel statistics.
     *
     * @return the channel statistics
     */
    public ChannelStatistics getChannelStatistics() {
        return channelStatistics;
    }

    /**
     * Sets channel statistics.
     *
     * @param channelStatistics the channel statistics
     */
    public void setChannelStatistics(ChannelStatistics channelStatistics) {
        this.channelStatistics = channelStatistics;
    }

    @Override
    public String toString() {
        return "ChannelItem{" +
                "id='" + id + '\'' +
                ", snippet=" + snippet +
                ", channelStatistics=" + channelStatistics +
                '}';
    }
}