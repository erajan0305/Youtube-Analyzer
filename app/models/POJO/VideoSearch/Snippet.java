package models.POJO.VideoSearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for {@link VideoSearchResultItem} Snippet.
 */
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
    private String publishedAt;
    @JsonProperty("channelId")
    private String channelId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("channelTitle")
    private String channelTitle;
    @JsonProperty("publishTime")
    private String publishTime;

    /**
     * Gets published at.
     *
     * @return the published at
     */
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     * Sets published at.
     *
     * @param publishedAt the published at
     */
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * Gets channel id.
     *
     * @return the channel id
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Sets channel id.
     *
     * @param channelId the channel id
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets channel title.
     *
     * @return the channel title
     */
    public String getChannelTitle() {
        return channelTitle;
    }

    /**
     * Sets channel title.
     *
     * @param channelTitle the channel title
     */
    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    /**
     * Gets publish time.
     *
     * @return the publish time
     */
    public String getPublishTime() {
        return publishTime;
    }

    /**
     * Sets publish time.
     *
     * @param publishTime the publish time
     */
    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

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
