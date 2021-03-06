package models.POJO.SearchResults;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for {@link SearchResultItem} Id.
 *
 * @author Kishan Bhimani
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"videoId"})

public class Id {
    @JsonProperty("videoId")
    private String videoId;

    /**
     * Instantiates a new Id.
     */
    public Id() {
    }

    /**
     * Instantiates a new Id.
     *
     * @param videoId the video id
     */
    public Id(String videoId) {
        this.videoId = videoId;
    }

    /**
     * Gets video id.
     *
     * @return the video id
     */
    public String getVideoId() {
        return videoId;
    }

    /**
     * Sets video id.
     *
     * @param videoId the video id
     */
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    @Override
    public String toString() {
        return "Id{" +
                "videoId='" + videoId + '\'' +
                '}';
    }
}