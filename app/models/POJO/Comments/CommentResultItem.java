package models.POJO.Comments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for {@link CommentResults} Item.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"snippet"})
public class CommentResultItem {
    @JsonProperty("snippet")
    private CommentSnippet snippet;

    public CommentSnippet getSnippet() {
        return snippet;
    }

    public void setCommentSnippet(CommentSnippet commentSnippet) {
        this.snippet = commentSnippet;
    }

    @Override
    public String toString() {
        return "CommentResultItem{" +
                ", CommentSnippet=" + snippet +
                '}';
    }
}
