package models;

import models.Helper.YouTubeApiClient;
import models.POJO.SearchResults.Id;
import models.POJO.SearchResults.SearchResultItem;
import models.POJO.SearchResults.SearchResults;
import models.POJO.SearchResults.Snippet;
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
    private YouTubeApiClient youTubeApiClient;
    private WSClient wsTestClient;
    private Server server;

    @Before
    public void setup() {
        server = Server.forRouter(
                (components) -> RoutingDsl.fromComponents(components)
                        .GET("/search")
                        .routingTo(request -> {
                            SearchResultItem searchResultItem = new SearchResultItem();
                            System.out.println(searchResultItem);
                            searchResultItem.id = new Id("123");
                            searchResultItem.snippet = new Snippet("xyzabc", "Channel Title,",
                                    "Title", "Description", "publishedAT", "publishTime");
                            searchResultItem.viewCount = "1000";
                            SearchResults searchResults = new SearchResults();
                            searchResults.items.add(searchResultItem);
                            return ok(Json.toJson(searchResults));
                        })
                        .build());
        wsTestClient = play.test.WSTestClient.newClient(server.httpPort());
        youTubeApiClient = new YouTubeApiClient(wsTestClient);
        youTubeApiClient.BASE_URL = "/";
    }

    @Test
    public void fetchVideos() throws Exception {
        SearchResults searchResults = youTubeApiClient.fetchVideos("hello world").toCompletableFuture().get();
        System.out.println(searchResults);
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
