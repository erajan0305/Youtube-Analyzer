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
                            } else {
                                String channelId = request.getQueryString("channelId");
                                if (channelId.equals("UC0RhatS1pyxInC00YKjjBqQ")) {
                                    return ok(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelvideos/Java_UC0RhatS1pyxInC00YKjjBqQ.json")));
                                }
                                if (channelId.equals("UCWr0mx597DnSGLFk1WfvSkQ")) {
                                    return ok(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelvideos/Python_UCWr0mx597DnSGLFk1WfvSkQ.json")));
                                }
                                if (channelId.equals("UC-R1UuxHVDyNoJN0Tn4nkiQ")) {
                                    return ok(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelvideos/Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json")));
                                }
                            }
                            return ok();
                        })
                        .GET("/videos")
                        .routingTo(request -> {
                            String videoId = request.getQueryString("id");
                            if (videoId.equals("uhp3GbQiSRs")) {
                                return ok(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/viewcount/Java_uhp3GbQiSRs.json")));
                            }
                            if (videoId.equals("OsKQw3qTMMk")) {
                                return ok(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/viewcount/Python_OsKQw3qTMMk.json")));
                            }
                            if (videoId.equals("FxxkOfvY39c")) {
                                return ok(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/viewcount/Golang_FxxkOfvY39c.json")));
                            }
                            return ok();
                        })
                        .GET("/channels")
                        .routingTo(request -> {
                            String channelId = request.getQueryString("id");
                            if (channelId.equals("UC0RhatS1pyxInC00YKjjBqQ")) {
                                return ok(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelinformation/Channel_Java_UC0RhatS1pyxInC00YKjjBqQ.json")));
                            }
                            if (channelId.equals("UCWr0mx597DnSGLFk1WfvSkQ")) {
                                return ok(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelinformation/Channel_Python_UCWr0mx597DnSGLFk1WfvSkQ.json")));
                            }
                            if (channelId.equals("UC-R1UuxHVDyNoJN0Tn4nkiQ")) {
                                return ok(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelinformation/Channel_Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json")));
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

    @Test
    public void getVideoJsonByVideoId() throws Exception {
        String actualJava = youTubeApiClient.getVideoJsonByVideoId("uhp3GbQiSRs").toCompletableFuture().get();
        Videos expectedJavaVideoItems = DatasetHelper.jsonFileToObject(new File("test/dataset/viewcount/Java_uhp3GbQiSRs.json"), Videos.class);
        Assert.assertEquals(expectedJavaVideoItems.items.get(0).statistics.viewCount, actualJava);

        String actualPython = youTubeApiClient.getVideoJsonByVideoId("OsKQw3qTMMk").toCompletableFuture().get();
        Videos expectedPythonVideoItems = DatasetHelper.jsonFileToObject(new File("test/dataset/viewcount/Python_OsKQw3qTMMk.json"), Videos.class);
        Assert.assertEquals(expectedPythonVideoItems.items.get(0).statistics.viewCount, actualPython);

        String actualGolang = youTubeApiClient.getVideoJsonByVideoId("FxxkOfvY39c").toCompletableFuture().get();
        Videos expectedGolangVideoItems = DatasetHelper.jsonFileToObject(new File("test/dataset/viewcount/Golang_FxxkOfvY39c.json"), Videos.class);
        Assert.assertEquals(expectedGolangVideoItems.items.get(0).statistics.viewCount, actualGolang);
    }

    @Test
    public void getVideosJsonByChannelId() throws Exception {
        SearchResults actualJava = youTubeApiClient.getVideosJsonByChannelId("UC0RhatS1pyxInC00YKjjBqQ").toCompletableFuture().get();
        SearchResults expectedJava = DatasetHelper.jsonFileToObject(new File("test/dataset/channelvideos/Java_UC0RhatS1pyxInC00YKjjBqQ.json"), SearchResults.class);
        Assert.assertEquals(expectedJava.toString(), actualJava.toString());

        SearchResults actualPython = youTubeApiClient.getVideosJsonByChannelId("UCWr0mx597DnSGLFk1WfvSkQ").toCompletableFuture().get();
        SearchResults expectedPython = DatasetHelper.jsonFileToObject(new File("test/dataset/channelvideos/Python_UCWr0mx597DnSGLFk1WfvSkQ.json"), SearchResults.class);
        Assert.assertEquals(expectedPython.toString(), actualPython.toString());

        SearchResults actualGolang = youTubeApiClient.getVideosJsonByChannelId("UC-R1UuxHVDyNoJN0Tn4nkiQ").toCompletableFuture().get();
        SearchResults expectedGolang = DatasetHelper.jsonFileToObject(new File("test/dataset/channelvideos/Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json"), SearchResults.class);
        Assert.assertEquals(expectedGolang.toString(), actualGolang.toString());
    }

    @Test
    public void getChannelInformationByChannelId() throws Exception {
        ChannelResultItems actualJava = youTubeApiClient.getChannelInformationByChannelId("UC0RhatS1pyxInC00YKjjBqQ").toCompletableFuture().get();
        ChannelResultItems expectedJava = DatasetHelper.jsonFileToObject(new File("test/dataset/channelinformation/Channel_Java_UC0RhatS1pyxInC00YKjjBqQ.json"), ChannelResultItems.class);
        Assert.assertEquals(expectedJava.toString(), actualJava.toString());

        ChannelResultItems actualPython = youTubeApiClient.getChannelInformationByChannelId("UCWr0mx597DnSGLFk1WfvSkQ").toCompletableFuture().get();
        ChannelResultItems expectedPython = DatasetHelper.jsonFileToObject(new File("test/dataset/channelinformation/Channel_Python_UCWr0mx597DnSGLFk1WfvSkQ.json"), ChannelResultItems.class);
        Assert.assertEquals(expectedPython.toString(), actualPython.toString());

        ChannelResultItems actualGolang = youTubeApiClient.getChannelInformationByChannelId("UC-R1UuxHVDyNoJN0Tn4nkiQ").toCompletableFuture().get();
        ChannelResultItems expectedGolang = DatasetHelper.jsonFileToObject(new File("test/dataset/channelinformation/Channel_Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json"), ChannelResultItems.class);
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
