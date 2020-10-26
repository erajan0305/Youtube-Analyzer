package models.Channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "publishedAt",
        "title",
        "description",
        "publishTime",
        "country"
})
public class Snippet {
    @JsonProperty("title")
    public String title;
    @JsonProperty("description")
    public String description;
    @JsonProperty("customUrl")
    public String customUrl;
    @JsonProperty("publishedAt")
    public String publishedAt;
    @JsonProperty("country")
    public String country;
}

