package models.POJO.SearchResults;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for Search Results Item.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "snippet",
        "viewCount",
        "commentSentiment"
})

public class SearchResultItem {
    @JsonProperty("id")
    private Id id;
    @JsonProperty("snippet")
    private Snippet snippet;
    @JsonProperty("viewCount")
    private String viewCount;
    @JsonProperty("commentSentiment")
    private String commentSentiment;

    /**
     * Instantiates a new Search result item.
     */
    public SearchResultItem() {
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public Id getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Id id) {
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
     * Gets comment sentiment.
     *
     * @return the comment sentiment
     */
    public String getCommentSentiment() {
        return commentSentiment;
    }

    /**
     * Sets comment sentiment.
     *
     * @param commentSentiment the comment sentiment
     */
    public void setCommentSentiment(String commentSentiment) {
        this.commentSentiment = commentSentiment;
    }

    @Override
    public String toString() {
        return "SearchResultItem{" +
                "id=" + id +
                ", snippet=" + snippet +
                ", viewCount='" + viewCount + '\'' +
                ", commentSentiment='" + commentSentiment + '\'' +
                '}';
    }
}