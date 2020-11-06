package models.POJO.Channel;

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

    public Snippet() {
    }

    public Snippet(String title, String description, String country, String customUrl, String publishedAt) {
        this.title = title;
        this.description = description;
        this.country = country;
        this.customUrl = customUrl;
        this.publishedAt = publishedAt;
    }

    @Override
    public String toString() {
        return "Snippet{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", customUrl='" + customUrl + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}

