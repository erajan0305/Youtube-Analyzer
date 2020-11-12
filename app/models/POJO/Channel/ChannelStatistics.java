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
    private String viewCount;
    @JsonProperty("subscriberCount")
    private String subscriberCount;
    @JsonProperty("videoCount")
    private String videoCount;

    /**
     * Instantiates a new Channel statistics.
     */
    public ChannelStatistics() {
    }

    /**
     * Instantiates a new Channel statistics.
     *
     * @param videoCount      the video count
     * @param viewCount       the view count
     * @param subscriberCount the subscriber count
     */
    public ChannelStatistics(String videoCount, String viewCount, String subscriberCount) {
        this.viewCount = viewCount;
        this.videoCount = videoCount;
        this.subscriberCount = subscriberCount;
    }

    /**
     * Gets view count.
     *
     * @return the view count
     */
    public String getViewCount() {
        return viewCount;
    }

    /**
     * Sets view count.
     *
     * @param viewCount the view count
     */
    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    /**
     * Gets subscriber count.
     *
     * @return the subscriber count
     */
    public String getSubscriberCount() {
        return subscriberCount;
    }

    /**
     * Sets subscriber count.
     *
     * @param subscriberCount the subscriber count
     */
    public void setSubscriberCount(String subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    /**
     * Gets video count.
     *
     * @return the video count
     */
    public String getVideoCount() {
        return videoCount;
    }

    /**
     * Sets video count.
     *
     * @param videoCount the video count
     */
    public void setVideoCount(String videoCount) {
        this.videoCount = videoCount;
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
