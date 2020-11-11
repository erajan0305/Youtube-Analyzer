package models.POJO.Comments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for {@link CommentResultItem} Snippet.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"topLevelComment"})
public class CommentSnippet {

    @JsonProperty("topLevelComment")
    private TopLevelComment topLevelComment;

    /**
     * Gets top level comment.
     *
     * @return the top level comment
     */
    public TopLevelComment getTopLevelComment() {
        return topLevelComment;
    }

    /**
     * Sets top level comment.
     *
     * @param topLevelComment the top level comment
     */
    public void setTopLevelComment(TopLevelComment topLevelComment) {
        this.topLevelComment = topLevelComment;
    }

    @Override
    public String toString() {
        return "CommentSnippet{" +
                "toLevelComment='" + topLevelComment + "}";
    }
}
