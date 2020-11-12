package models.POJO.Channel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * Model Class for Channel Result Items.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"items"})

public class ChannelResultItems {
    @JsonProperty("items")
    private List<ChannelItem> items = null;

    /**
     * Instantiates a new Channel result items.
     */
    public ChannelResultItems() {
    }

    /**
     * Gets items.
     *
     * @return the items
     */
    public List<ChannelItem> getItems() {
        return items;
    }

    /**
     * Sets items.
     *
     * @param items the items
     */
    public void setItems(List<ChannelItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "ChannelResultItems{" +
                "items=" + items +
                '}';
    }
}