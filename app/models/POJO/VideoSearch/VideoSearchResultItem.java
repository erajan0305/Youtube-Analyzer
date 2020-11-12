package models.POJO.VideoSearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for Video Search Result Item.
 *
 * @author Kishan Bhimani
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "snippet", "statistics"})

public class VideoSearchResultItem {
    @JsonProperty("id")
    private String id;
    @JsonProperty("snippet")
    private Snippet snippet;
    @JsonProperty("statistics")
    private VideoStatistics statistics;


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
     * Gets statistics.
     *
     * @return the statistics
     */
    public VideoStatistics getStatistics() {
        return statistics;
    }

    /**
     * Sets statistics.
     *
     * @param statistics the statistics
     */
    public void setStatistics(VideoStatistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public String toString() {
        return "VideoSearchResultItem{" +
                "id='" + id + '\'' +
                ", snippet=" + snippet +
                ", statistics=" + statistics +
                '}';
    }
}
