package models.POJO.Comments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for {@link CommentSnippet} Top Level Comment.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"snippet"})
public class TopLevelComment {

    @JsonProperty("snippet")
    private Snippet snippet;

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

    @Override
    public String toString() {
        return "Snippet{" +
                "publishedAt='" + snippet + "}";
    }
}
