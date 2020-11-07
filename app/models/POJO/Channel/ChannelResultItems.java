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
    public ChannelResultItems() {
    }

    @JsonProperty("items")
    public List<ChannelItem> items = null;

    @Override
    public String toString() {
        return "ChannelResultItems{" +
                "items=" + items +
                '}';
    }
}