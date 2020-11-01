package models;

import dataset.DatasetHelper;
import models.Helper.YouTubeApiClient;
import models.POJO.Channel.ChannelResultItems;
import models.POJO.SearchResults.SearchResults;
import models.POJO.VIdeoSearch.Videos;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.libs.ws.WSClient;
import play.routing.RoutingDsl;
import play.server.Server;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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
                            if (request.getQueryString("q") != null && !request.getQueryString("q").isEmpty()) {
                                String searchKey = request.getQueryString("q").toLowerCase();
                                switch (searchKey) {
                                    case "java":
                                        return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/searchresults/Java.json"))));
                                    case "python":
                                        return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/searchresults/Python.json"))));
                                    case "golang":
                                        return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/searchresults/Golang.json"))));
                                    default:
                                        return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/empty.json"))));
                                }
                            } else {
                                String channelId = request.getQueryString("channelId");
                                switch (channelId) {
                                    case "UC0RhatS1pyxInC00YKjjBqQ":
                                        return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelvideos/Java_UC0RhatS1pyxInC00YKjjBqQ.json"))));
                                    case "UCWr0mx597DnSGLFk1WfvSkQ":
                                        return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelvideos/Python_UCWr0mx597DnSGLFk1WfvSkQ.json"))));
                                    case "UC-R1UuxHVDyNoJN0Tn4nkiQ":
                                        return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelvideos/Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json"))));
                                    default:
                                        return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/empty.json"))));
                                }
                            }
                        })
                        .GET("/videos")
                        .routingTo(request -> {
                            String videoId = request.getQueryString("id");
                            switch (videoId) {
                                case "uhp3GbQiSRs":
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/viewcount/Java_uhp3GbQiSRs.json"))));
                                case "OsKQw3qTMMk":
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/viewcount/Python_OsKQw3qTMMk.json"))));
                                case "FxxkOfvY39c":
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/viewcount/Golang_FxxkOfvY39c.json"))));
                                default:
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/empty.json"))));
                            }
                        })
                        .GET("/channels")
                        .routingTo(request -> {
                            String channelId = request.getQueryString("id");
                            switch (channelId) {
                                case "UC0RhatS1pyxInC00YKjjBqQ":
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelinformation/Channel_Java_UC0RhatS1pyxInC00YKjjBqQ.json"))));
                                case "UCWr0mx597DnSGLFk1WfvSkQ":
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelinformation/Channel_Python_UCWr0mx597DnSGLFk1WfvSkQ.json"))));
                                case "UC-R1UuxHVDyNoJN0Tn4nkiQ":
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelinformation/Channel_Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json"))));
                                default:
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/empty.json"))));
                            }
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
        assert expectedJava != null;
        Assert.assertEquals(expectedJava.toString(), actualJava.toString());

        SearchResults actualPython = youTubeApiClient.fetchVideos("python").toCompletableFuture().get();
        SearchResults expectedPython = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Python.json"), SearchResults.class);
        assert expectedPython != null;
        Assert.assertEquals(expectedPython.toString(), actualPython.toString());

        SearchResults actualGolang = youTubeApiClient.fetchVideos("golang").toCompletableFuture().get();
        SearchResults expectedGolang = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Golang.json"), SearchResults.class);
        assert expectedGolang != null;
        Assert.assertEquals(expectedGolang.toString(), actualGolang.toString());

        SearchResults actualNoResult = youTubeApiClient.fetchVideos("!029 ( 02 _2 (@ 92020** 7&6 ^^5").toCompletableFuture().get();
        Videos expectedEmptyJson = DatasetHelper.jsonFileToObject(new File("test/dataset/empty.json"), Videos.class);
        assert Objects.requireNonNull(expectedEmptyJson).items == null;
        assert Objects.requireNonNull(actualNoResult).items == null;
    }

    @Test
    public void getVideoJsonByVideoId() throws Exception {
        String actualJava = youTubeApiClient.getVideoJsonByVideoId("uhp3GbQiSRs").toCompletableFuture().get();
        Videos expectedJavaVideoItems = DatasetHelper.jsonFileToObject(new File("test/dataset/viewcount/Java_uhp3GbQiSRs.json"), Videos.class);
        assert expectedJavaVideoItems != null;
        Assert.assertEquals(expectedJavaVideoItems.items.get(0).statistics.viewCount, actualJava);

        String actualPython = youTubeApiClient.getVideoJsonByVideoId("OsKQw3qTMMk").toCompletableFuture().get();
        Videos expectedPythonVideoItems = DatasetHelper.jsonFileToObject(new File("test/dataset/viewcount/Python_OsKQw3qTMMk.json"), Videos.class);
        assert expectedPythonVideoItems != null;
        Assert.assertEquals(expectedPythonVideoItems.items.get(0).statistics.viewCount, actualPython);

        String actualGolang = youTubeApiClient.getVideoJsonByVideoId("FxxkOfvY39c").toCompletableFuture().get();
        Videos expectedGolangVideoItems = DatasetHelper.jsonFileToObject(new File("test/dataset/viewcount/Golang_FxxkOfvY39c.json"), Videos.class);
        assert expectedGolangVideoItems != null;
        Assert.assertEquals(expectedGolangVideoItems.items.get(0).statistics.viewCount, actualGolang);

        // TODO: write after getVideoJsonByVideoId result viewCount/videos can be appended to search results.
        /*String actualNoResult = youTubeApiClient.getVideoJsonByVideoId("!029 ( 02 _2 (@ 92020** 7&6 ^^5").toCompletableFuture().get();
        Videos expectedEmptyJson = DatasetHelper.jsonFileToObject(new File("test/dataset/empty.json"), Videos.class);
        assert expectedEmptyJson == null;
        assert actualNoResult == null;*/
    }

    @Test
    public void getVideosJsonByChannelId() throws Exception {
        SearchResults actualJava = youTubeApiClient.getVideosJsonByChannelId("UC0RhatS1pyxInC00YKjjBqQ").toCompletableFuture().get();
        SearchResults expectedJava = DatasetHelper.jsonFileToObject(new File("test/dataset/channelvideos/Java_UC0RhatS1pyxInC00YKjjBqQ.json"), SearchResults.class);
        assert expectedJava != null;
        Assert.assertEquals(expectedJava.toString(), actualJava.toString());

        SearchResults actualPython = youTubeApiClient.getVideosJsonByChannelId("UCWr0mx597DnSGLFk1WfvSkQ").toCompletableFuture().get();
        SearchResults expectedPython = DatasetHelper.jsonFileToObject(new File("test/dataset/channelvideos/Python_UCWr0mx597DnSGLFk1WfvSkQ.json"), SearchResults.class);
        assert expectedPython != null;
        Assert.assertEquals(expectedPython.toString(), actualPython.toString());

        SearchResults actualGolang = youTubeApiClient.getVideosJsonByChannelId("UC-R1UuxHVDyNoJN0Tn4nkiQ").toCompletableFuture().get();
        SearchResults expectedGolang = DatasetHelper.jsonFileToObject(new File("test/dataset/channelvideos/Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json"), SearchResults.class);
        assert expectedGolang != null;
        Assert.assertEquals(expectedGolang.toString(), actualGolang.toString());

        SearchResults actualNoResult = youTubeApiClient.getVideosJsonByChannelId("!029 ( 02 _2 (@ 92020** 7&6 ^^5").toCompletableFuture().get();
        Videos expectedEmptyJson = DatasetHelper.jsonFileToObject(new File("test/dataset/empty.json"), Videos.class);
        assert Objects.requireNonNull(expectedEmptyJson).items == null;
        assert Objects.requireNonNull(actualNoResult).items == null;
    }

    @Test
    public void getChannelInformationByChannelId() throws Exception {
        ChannelResultItems actualJava = youTubeApiClient.getChannelInformationByChannelId("UC0RhatS1pyxInC00YKjjBqQ").toCompletableFuture().get();
        ChannelResultItems expectedJava = DatasetHelper.jsonFileToObject(new File("test/dataset/channelinformation/Channel_Java_UC0RhatS1pyxInC00YKjjBqQ.json"), ChannelResultItems.class);
        assert expectedJava != null;
        Assert.assertEquals(expectedJava.toString(), actualJava.toString());

        ChannelResultItems actualPython = youTubeApiClient.getChannelInformationByChannelId("UCWr0mx597DnSGLFk1WfvSkQ").toCompletableFuture().get();
        ChannelResultItems expectedPython = DatasetHelper.jsonFileToObject(new File("test/dataset/channelinformation/Channel_Python_UCWr0mx597DnSGLFk1WfvSkQ.json"), ChannelResultItems.class);
        assert expectedPython != null;
        Assert.assertEquals(expectedPython.toString(), actualPython.toString());

        ChannelResultItems actualGolang = youTubeApiClient.getChannelInformationByChannelId("UC-R1UuxHVDyNoJN0Tn4nkiQ").toCompletableFuture().get();
        ChannelResultItems expectedGolang = DatasetHelper.jsonFileToObject(new File("test/dataset/channelinformation/Channel_Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json"), ChannelResultItems.class);
        assert expectedGolang != null;
        Assert.assertEquals(expectedGolang.toString(), actualGolang.toString());

        ChannelResultItems actualNoResult = youTubeApiClient.getChannelInformationByChannelId("!029 ( 02 _2 (@ 92020** 7&6 ^^5").toCompletableFuture().get();
        ChannelResultItems expectedEmptyJson = DatasetHelper.jsonFileToObject(new File("test/dataset/empty.json"), ChannelResultItems.class);
        assert Objects.requireNonNull(expectedEmptyJson).items == null;
        assert Objects.requireNonNull(actualNoResult).items == null;
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
