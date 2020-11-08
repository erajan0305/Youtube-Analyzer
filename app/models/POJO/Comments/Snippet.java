package models.POJO.Comments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for {@link TopLevelComment} snippet.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "textDisplay",
        "textOriginal"
})
public class Snippet {

    @JsonProperty("textDisplay")
    private String textDisplay;

    @JsonProperty("textOriginal")
    private String textOriginal;

    public String getTextDisplay() {
        return textDisplay;
    }

    public String getTextOriginal() {
        return textOriginal;
    }
}
