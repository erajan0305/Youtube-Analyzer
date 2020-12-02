package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import dataset.DatasetHelper;
import models.Actors.YoutubeApiClientActor;
import models.POJO.Channel.ChannelResultItems;
import models.POJO.SearchResults.SearchResults;
import models.POJO.VideoSearch.Videos;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import play.libs.ws.WSClient;
import play.routing.RoutingDsl;
import play.server.Server;
import scala.compat.java8.FutureConverters;

import java.io.File;
import java.util.Objects;

import static akka.pattern.Patterns.ask;
import static play.mvc.Results.ok;

public class YoutubeApiClientActorTest {
    ActorRef youtubeApiClientActor;

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Before
    public void setup() {
        Server server = Server.forRouter(
                (components) -> RoutingDsl.fromComponents(components)
                        .GET("/search")
                        .routingTo(request -> {
                            if (request.queryString("q").isPresent() && !request.queryString("channelId").isPresent()) {
                                String searchKey = request.queryString("q").get().toLowerCase();
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
                                String channelId = request.queryString("channelId").get();
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
                            String videoId = request.queryString("id").get();
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
                            String channelId = request.queryString("id").get();
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
                        .GET("/commentThreads")
                        .routingTo(request -> {
                            String videoId = request.queryString("videoId").get();
                            switch (videoId) {
                                case "X2lIovmNsUY":
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/comments/happy_video.json"))));
                                case "iupakooy3pU":
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/comments/sad_video.json"))));
                                case "Bi7f1JSSlh8":
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/comments/neutral_video.json"))));
                                default:
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/comments/zero_comments.json"))));
                            }
                        })
                        .build());

        WSClient wsTestClient = play.test.WSTestClient.newClient(server.httpPort());
        ActorSystem actorSystem = ActorSystem.create("TestActorSystem");
        youtubeApiClientActor = actorSystem.actorOf(YoutubeApiClientActor.props(wsTestClient));
        youtubeApiClientActor.tell(new YoutubeApiClientActor.SetBaseUrl("/"), ActorRef.noSender());
    }

    @Test
    public void fetchVideosTest() {
        SearchResults actualJava = FutureConverters.toJava(ask(youtubeApiClientActor, new YoutubeApiClientActor.FetchVideos("java"), 5000))
                .toCompletableFuture().thenApply(o -> (SearchResults) o).join();
        SearchResults expectedJava = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Java.json"), SearchResults.class);
        assert expectedJava != null;
        Assert.assertEquals(expectedJava.toString(), actualJava.toString());

        SearchResults actualPython = FutureConverters.toJava(ask(youtubeApiClientActor, new YoutubeApiClientActor.FetchVideos("python"), 5000))
                .toCompletableFuture().thenApply(o -> (SearchResults) o).join();
        SearchResults expectedPython = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Python.json"), SearchResults.class);
        assert expectedPython != null;
        Assert.assertEquals(expectedPython.toString(), actualPython.toString());

        SearchResults actualGolang = FutureConverters.toJava(ask(youtubeApiClientActor, new YoutubeApiClientActor.FetchVideos("golang"), 5000))
                .toCompletableFuture().thenApply(o -> (SearchResults) o).join();
        SearchResults expectedGolang = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Golang.json"), SearchResults.class);
        assert expectedGolang != null;
        Assert.assertEquals(expectedGolang.toString(), actualGolang.toString());

        SearchResults actualNoResult = FutureConverters.toJava(ask(youtubeApiClientActor, new YoutubeApiClientActor.FetchVideos("!029 ( 02 _2 (@ 92020** 7&6 ^^5"), 5000))
                .toCompletableFuture().thenApply(o -> (SearchResults) o).join();
        SearchResults expectedEmptyJson = DatasetHelper.jsonFileToObject(new File("test/dataset/empty.json"), SearchResults.class);
        assert Objects.requireNonNull(expectedEmptyJson).getItems() == null;
        assert Objects.requireNonNull(actualNoResult).getItems() == null;
    }

    @Test
    public void getVideosJsonByChannelId() {
        SearchResults actualJava = FutureConverters.toJava(ask(youtubeApiClientActor, new YoutubeApiClientActor.GetVideosJsonByChannelId("UC0RhatS1pyxInC00YKjjBqQ", "java"), 5000))
                .toCompletableFuture().thenApply(o -> (SearchResults) o).join();
        SearchResults expectedJava = DatasetHelper.jsonFileToObject(new File("test/dataset/channelvideos/Java_UC0RhatS1pyxInC00YKjjBqQ.json"), SearchResults.class);
        assert expectedJava != null;
        Assert.assertEquals(expectedJava.toString(), actualJava.toString());

        SearchResults actualPython = FutureConverters.toJava(ask(youtubeApiClientActor, new YoutubeApiClientActor.GetVideosJsonByChannelId("UCWr0mx597DnSGLFk1WfvSkQ", "python"), 5000))
                .toCompletableFuture().thenApply(o -> (SearchResults) o).join();
        SearchResults expectedPython = DatasetHelper.jsonFileToObject(new File("test/dataset/channelvideos/Python_UCWr0mx597DnSGLFk1WfvSkQ.json"), SearchResults.class);
        assert expectedPython != null;
        Assert.assertEquals(expectedPython.toString(), actualPython.toString());

        SearchResults actualGolang = FutureConverters.toJava(ask(youtubeApiClientActor, new YoutubeApiClientActor.GetVideosJsonByChannelId("UC-R1UuxHVDyNoJN0Tn4nkiQ", "golang"), 5000))
                .toCompletableFuture().thenApply(o -> (SearchResults) o).join();
        SearchResults expectedGolang = DatasetHelper.jsonFileToObject(new File("test/dataset/channelvideos/Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json"), SearchResults.class);
        assert expectedGolang != null;
        Assert.assertEquals(expectedGolang.toString(), actualGolang.toString());

        SearchResults actualNoResult = FutureConverters.toJava(ask(youtubeApiClientActor, new YoutubeApiClientActor.GetVideosJsonByChannelId("!029 ( 02 _2 (@ 92020** 7&6 ^^5", ""), 5000))
                .toCompletableFuture().thenApply(o -> (SearchResults) o).join();
        Videos expectedEmptyJson = DatasetHelper.jsonFileToObject(new File("test/dataset/empty.json"), Videos.class);
        assert Objects.requireNonNull(expectedEmptyJson).getItems() == null;
        assert Objects.requireNonNull(actualNoResult).getItems() == null;
    }

    @Test
    public void getChannelInformationByChannelId() {
        ChannelResultItems actualJava = FutureConverters.toJava(ask(youtubeApiClientActor, new YoutubeApiClientActor.GetChannelInformationByChannelId("UC0RhatS1pyxInC00YKjjBqQ"), 5000))
                .toCompletableFuture().thenApply(o -> (ChannelResultItems) o).join();
        ChannelResultItems expectedJava = DatasetHelper.jsonFileToObject(new File("test/dataset/channelinformation/Channel_Java_UC0RhatS1pyxInC00YKjjBqQ.json"), ChannelResultItems.class);
        assert expectedJava != null;
        Assert.assertEquals(expectedJava.toString(), actualJava.toString());

        ChannelResultItems actualPython = FutureConverters.toJava(ask(youtubeApiClientActor, new YoutubeApiClientActor.GetChannelInformationByChannelId("UCWr0mx597DnSGLFk1WfvSkQ"), 5000))
                .toCompletableFuture().thenApply(o -> (ChannelResultItems) o).join();
        ChannelResultItems expectedPython = DatasetHelper.jsonFileToObject(new File("test/dataset/channelinformation/Channel_Python_UCWr0mx597DnSGLFk1WfvSkQ.json"), ChannelResultItems.class);
        assert expectedPython != null;
        Assert.assertEquals(expectedPython.toString(), actualPython.toString());

        ChannelResultItems actualGolang = FutureConverters.toJava(ask(youtubeApiClientActor, new YoutubeApiClientActor.GetChannelInformationByChannelId("UC-R1UuxHVDyNoJN0Tn4nkiQ"), 5000))
                .toCompletableFuture().thenApply(o -> (ChannelResultItems) o).join();
        ChannelResultItems expectedGolang = DatasetHelper.jsonFileToObject(new File("test/dataset/channelinformation/Channel_Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json"), ChannelResultItems.class);
        assert expectedGolang != null;
        Assert.assertEquals(expectedGolang.toString(), actualGolang.toString());

        ChannelResultItems actualNoResult = FutureConverters.toJava(ask(youtubeApiClientActor, new YoutubeApiClientActor.GetChannelInformationByChannelId("!029 ( 02 _2 (@ 92020** 7&6 ^^5"), 5000))
                .toCompletableFuture().thenApply(o -> (ChannelResultItems) o).join();
        ChannelResultItems expectedEmptyJson = DatasetHelper.jsonFileToObject(new File("test/dataset/empty.json"), ChannelResultItems.class);
        assert Objects.requireNonNull(expectedEmptyJson).getItems() == null;
        assert Objects.requireNonNull(actualNoResult).getItems() == null;
    }
}
