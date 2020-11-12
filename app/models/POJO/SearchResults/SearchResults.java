package models.POJO.SearchResults;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * Model Class for Search Results.
 *
 * @author Kishan Bhimani
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"items"})

public class SearchResults {

    @JsonProperty("items")
    private List<SearchResultItem> items = null;

    /**
     * Instantiates a new Search results.
     */
    public SearchResults() {
    }

    /**
     * Gets items.
     *
     * @return the items
     */
    public List<SearchResultItem> getItems() {
        return items;
    }

    /**
     * Sets items.
     *
     * @param items the items
     */
    public void setItems(List<SearchResultItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "SearchResults{" +
                "items=" + items +
                '}';
    }
}