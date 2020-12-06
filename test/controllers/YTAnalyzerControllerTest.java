package controllers;

import actors.SessionActor;
import actors.YoutubeApiClientActor;
import akka.actor.ActorRef;
import dataset.DatasetHelper;
import models.Helper.SessionHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import play.Application;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.ws.WSClient;
import play.mvc.Http;
import play.mvc.Result;
import play.routing.RoutingDsl;
import play.server.Server;
import play.test.Helpers;
import play.test.WithApplication;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static play.mvc.Http.Status.OK;
import static play.mvc.Results.ok;

@RunWith(MockitoJUnitRunner.class)
public class YTAnalyzerControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    YoutubeAnalyzerController youtubeAnalyzerController;
    FormFactory _mockFormFactory;
    MessagesApi _messagesApi;
    WSClient _wsClient;
    Server server;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        youtubeAnalyzerController = new YoutubeAnalyzerController();
        _mockFormFactory = new GuiceApplicationBuilder().injector().instanceOf(FormFactory.class);
        _messagesApi = new GuiceApplicationBuilder().injector().instanceOf(MessagesApi.class);
        youtubeAnalyzerController.setFormFactory(_mockFormFactory);
        youtubeAnalyzerController.setMessagesApi(_messagesApi);
        server = Server.forRouter(
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
        _wsClient = play.test.WSTestClient.newClient(server.httpPort());
        youtubeAnalyzerController.youtubeApiClientActor.tell(new YoutubeApiClientActor.SetBaseUrl("/"), ActorRef.noSender());
        youtubeAnalyzerController.youtubeApiClientActor.tell(new YoutubeApiClientActor.SetWSClient(_wsClient), ActorRef.noSender());
    }

    @After
    public void destroy() {
        server = null;
        youtubeAnalyzerController = null;
        _messagesApi = null;
        _mockFormFactory = null;
        _wsClient = null;
    }

    @Test
    public void indexWithoutSessionTest() {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.index());
        requestBuilder.header("User-Agent", "chrome");
        Result result = youtubeAnalyzerController.index(requestBuilder.build()).join();
        Assert.assertEquals(OK, result.status());
    }

    @Test
    public void indexWithSessionTest() {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.index());
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.getSessionKey(), "chrome");
        youtubeAnalyzerController.sessionActor
                .tell(new SessionActor.CreateUser("chrome", youtubeAnalyzerController.supervisorActor), ActorRef.noSender());
        Result result = youtubeAnalyzerController.index(requestBuilder.build()).join();
        Assert.assertEquals(OK, result.status());
    }

    @Test
    public void fetchVideosByKeywordTest() {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.index());
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.getSessionKey(), "chrome");
        Map<String, String[]> requestBody = new HashMap<>();
        String[] searchKeyWord = new String[]{"java"};
        requestBody.put("searchKeyword", searchKeyWord);
        requestBuilder.bodyFormArrayValues(requestBody);
        youtubeAnalyzerController.sessionActor
                .tell(new SessionActor.CreateUser("chrome", youtubeAnalyzerController.supervisorActor), ActorRef.noSender());
        Result result = youtubeAnalyzerController.fetchVideosByKeywords(requestBuilder.build()).toCompletableFuture().join();
        Assert.assertEquals(OK, result.status());
    }
}
