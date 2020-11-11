package models.POJO.Comments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for {@link TopLevelComment} snippet.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "textOriginal"
})
public class Snippet {

    @JsonProperty("textOriginal")
    private String textOriginal;

    public String getTextOriginal() {
        return textOriginal;
    }

    public void setTextOriginal(String textOriginal) {
        this.textOriginal = textOriginal;
    }

    @Override
    public String toString() {
        return "Snippet{" +
                "textOriginal='" + textOriginal + '}';
    }
}
