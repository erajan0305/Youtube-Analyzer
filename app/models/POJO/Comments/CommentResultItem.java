package models.POJO.Comments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"snippet"})
public class CommentResultItem {
    @JsonProperty("snippet")
    private CommentSnippet snippet;

    public CommentSnippet getSnippet() {
        return snippet;
    }
}
