package models.POJO.VideoSearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * Model Class for Videos.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"items"})

public class Videos {
    /**
     * Instantiates a new Videos.
     */
    public Videos() {
    }

    @JsonProperty("items")
    private List<VideoSearchResultItem> items = null;

    /**
     * Gets items.
     *
     * @return the items
     */
    public List<VideoSearchResultItem> getItems() {
        return items;
    }

    /**
     * Sets items.
     *
     * @param items the items
     */
    public void setItems(List<VideoSearchResultItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Videos{" +
                "items=" + items +
                '}';
    }
}
