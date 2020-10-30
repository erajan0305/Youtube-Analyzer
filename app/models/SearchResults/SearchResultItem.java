package models.SearchResults;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import models.Helper.YouTubeClient;
import play.libs.ws.WSClient;

import javax.inject.Inject;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "snippet",
        "viewCount"
})
public class SearchResultItem {
    @JsonProperty("id")
    public Id id;
    @JsonProperty("snippet")
    public Snippet snippet;
    @JsonProperty("viewCount")
    public String viewCount;

    @Inject
    WSClient wsClient;

    public SearchResultItem appendViewCountToItem() {
        YouTubeClient youTubeClient = new YouTubeClient(wsClient);
        return (SearchResultItem) youTubeClient.getVideoJsonByVideoId(this.id.videoId).thenApply(viewCount -> {
            this.viewCount = viewCount;
            return this;
        });
    }
}