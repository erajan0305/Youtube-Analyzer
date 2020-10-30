package models.VIdeoSearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import models.Helper.YouTubeClient;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"items"})

public class Videos {
    public Videos() {
    }

    @Inject
    WSClient wsClient;

    public Videos(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    @JsonProperty("items")
    public List<VideoSearchResultItem> items = null;

    public CompletionStage<String> getVideosJsonByVideoId(String videoId) {
        YouTubeClient youTubeClient = new YouTubeClient(this.wsClient);
        return youTubeClient.getVideoJsonByVideoId(videoId);
    }
}
