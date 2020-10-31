package models.POJO.SearchResults;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"videoId"})

public class Id {
    @JsonProperty("videoId")
    public String videoId;

    public Id() {
    }

    public Id(String videoId) {
        this.videoId = videoId;
    }
}