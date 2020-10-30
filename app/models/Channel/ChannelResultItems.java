package models.Channel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import models.Helper.YouTubeClient;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"items"})

public class ChannelResultItems {
    public ChannelResultItems() {
    }

    @Inject
    WSClient wsClient;

    @JsonProperty("items")
    public List<ChannelItem> items = null;

    public ChannelResultItems(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    public CompletionStage<ChannelResultItems> getChannelInformationByChannelId(String channelId) {
        YouTubeClient youTubeClient = new YouTubeClient(this.wsClient);
        return youTubeClient.getChannelInformationByChannelId(channelId);
    }
}