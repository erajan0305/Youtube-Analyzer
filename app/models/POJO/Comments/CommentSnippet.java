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

    public TopLevelComment getTopLevelComment() {
        return topLevelComment;
    }
}
