package models.POJO.Channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for {@link ChannelItem} Snippet.
 *
 * @author Rajan Shah
 */
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
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("customUrl")
    private String customUrl;
    @JsonProperty("publishedAt")
    private String publishedAt;

    /**
     * Instantiates a new Snippet.
     */
    public Snippet() {
    }

    /**
     * Instantiates a new Snippet.
     *
     * @param title       the title
     * @param description the description
     * @param country     the country
     * @param customUrl   the custom url
     * @param publishedAt the published at
     */
    public Snippet(String title, String description, String country, String customUrl, String publishedAt) {
        this.title = title;
        this.description = description;
        this.country = country;
        this.customUrl = customUrl;
        this.publishedAt = publishedAt;
    }

    @JsonProperty("country")
    private String country;


    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets custom url.
     *
     * @return the custom url
     */
    public String getCustomUrl() {
        return customUrl;
    }

    /**
     * Sets custom url.
     *
     * @param customUrl the custom url
     */
    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    /**
     * Gets published at.
     *
     * @return the published at
     */
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     * Sets published at.
     *
     * @param publishedAt the published at
     */
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * Gets country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets country.
     *
     * @param country the country
     */
    public void setCountry(String country) {
        this.country = country;
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

