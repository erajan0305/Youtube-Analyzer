package models;

import dataset.DatasetHelper;
import models.Helper.YouTubeApiClient;
import models.POJO.SearchResults.SearchResults;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.libs.ws.WSClient;
import play.routing.RoutingDsl;
import play.server.Server;

import java.io.File;
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
                            String searchKey = request.getQueryString("q");
                            if (searchKey.toLowerCase().equals("java")) {
                                return ok(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/searchresults/Java.json")));
                            }
                            if (searchKey.toLowerCase().equals("python")) {
                                return ok(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/searchresults/Python.json")));
                            }
                            if (searchKey.toLowerCase().equals("golang")) {
                                return ok(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/searchresults/Golang.json")));
                            }
                            return ok();
                        })
                        .build());
        wsTestClient = play.test.WSTestClient.newClient(server.httpPort());
        youTubeApiClient = new YouTubeApiClient(wsTestClient);
        youTubeApiClient.BASE_URL = "/";
    }

    @Test
    public void fetchVideos() throws Exception {
        SearchResults actualJava = youTubeApiClient.fetchVideos("java").toCompletableFuture().get();
        SearchResults expectedJava = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Java.json"), SearchResults.class);
        Assert.assertEquals(expectedJava.toString(), actualJava.toString());

        SearchResults actualPython = youTubeApiClient.fetchVideos("python").toCompletableFuture().get();
        SearchResults expectedPython = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Python.json"), SearchResults.class);
        Assert.assertEquals(expectedPython.toString(), actualPython.toString());

        SearchResults actualGolang = youTubeApiClient.fetchVideos("golang").toCompletableFuture().get();
        SearchResults expectedGolang = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Golang.json"), SearchResults.class);
        Assert.assertEquals(expectedGolang.toString(), actualGolang.toString());
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
