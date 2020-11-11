package models.POJO.Comments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * Model Class for Comment Results.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"items"})

public class CommentResults {

    @JsonProperty("items")
    private List<CommentResultItem> items = null;

    /**
     * Gets items.
     *
     * @return the items
     */
    public List<CommentResultItem> getItems() {
        return items;
    }

    /**
     * Sets items.
     *
     * @param items the items
     */
    public void setItems(List<CommentResultItem> items) {
        this.items = items;
    }

    /**
     * Instantiates a new Comment results.
     */
    public CommentResults() {
    }

    @Override
    public String toString() {
        return "CommentResults{" +
                "items=" + items +
                '}';
    }

}
