package models.VIdeoSearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "statistics"})

public class VideoSearchResultItem {
    @JsonProperty("id")
    public String id;
    @JsonProperty("statistics")
    public VideoStatistics statistics;
}