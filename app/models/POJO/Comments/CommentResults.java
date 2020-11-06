package models.POJO.Comments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import models.Helper.EmojiAnalyzer;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"items"})
public class CommentResults {

    @JsonProperty("items")
    private List<CommentResultItem> items;

    private String getComments() {
        var commentStream = items.parallelStream()
                .map(commentResultItem -> commentResultItem.getSnippet().getTopLevelComment().getSnippet().getTextOriginal().trim().strip());
        var comments = EmojiAnalyzer.processCommentStream(commentStream);
        return comments;
    }

    public String getAnalysisResult() {
        return EmojiAnalyzer.generateReport(getComments());
    }


}
