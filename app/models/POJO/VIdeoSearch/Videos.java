package models.POJO.VIdeoSearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"items"})

public class Videos {
    public Videos() {
    }

    @JsonProperty("items")
    public List<VideoSearchResultItem> items = null;

    @Override
    public String toString() {
        return "Videos{" +
                "items=" + items +
                '}';
    }
}
