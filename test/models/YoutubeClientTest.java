package models;

import models.Helper.YouTubeClient;
import models.SearchResults.SearchResultItem;
import models.SearchResults.SearchResults;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.routing.RoutingDsl;
import play.server.Server;

import java.io.IOException;

import static play.mvc.Results.ok;

public class YoutubeClientTest {
    private YouTubeClient youTubeClient;
    private WSClient wsTestClient;
    private Server server;

    @Before
    public void setup() {
        server = Server.forRouter(
                (components) -> RoutingDsl.fromComponents(components)
                        .GET("search")
                        .routingTo(request -> {
                            SearchResultItem searchResultItem = new SearchResultItem();
                            searchResultItem.id.videoId = "abcxyz";
                            searchResultItem.viewCount = "1000";
                            searchResultItem.snippet.title = "Test Output";
                            searchResultItem.snippet.description = "Test Description";
                            searchResultItem.snippet.publishTime = "Test Time";
                            searchResultItem.snippet.channelId = "xyzabc";
                            searchResultItem.snippet.channelTitle = "Test Channel";
                            SearchResults searchResults = new SearchResults();
                            searchResults.items.add(searchResultItem);
                            return ok(Json.toJson(searchResults));
                        })
                        .build());
        wsTestClient = play.test.WSTestClient.newClient(server.httpPort());
        youTubeClient = new YouTubeClient(wsTestClient);
        youTubeClient.BASE_URL = "/";
    }

    @Test
    public void fetchVideos() throws Exception {
        SearchResults searchResults = youTubeClient.fetchVideos("hello world").toCompletableFuture().get();
        System.out.println(searchResults);
//        assertThat(searchResults, hasItem(Json.parse()));
    }

    @After
    public void destroy() throws IOException {
        try {
            wsTestClient.close();
        } finally {
            server.stop();
        }
    }
}
