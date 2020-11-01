package models.POJO.VIdeoSearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "snippet", "statistics"})

public class VideoSearchResultItem {
    @JsonProperty("id")
    public String id;
    @JsonProperty("snippet")
    public Snippet snippet;
    @JsonProperty("statistics")
    public VideoStatistics statistics;

    @Override
    public String toString() {
        return "VideoSearchResultItem{" +
                "id='" + id + '\'' +
                ", snippet=" + snippet +
                ", statistics=" + statistics +
                '}';
    }
}
