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
import java.util.concurrent.CompletionStage;

import static play.mvc.Http.Status.*;
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
                                if ("java".equals(searchKey)) {
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/searchresults/Java.json"))));
                                }
                                return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/empty.json"))));
                            } else {
                                String channelId = request.queryString("channelId").get();
                                if ("UC0RhatS1pyxInC00YKjjBqQ".equals(channelId)) {
                                    return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelvideos/Java_UC0RhatS1pyxInC00YKjjBqQ.json"))));
                                }
                                return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/empty.json"))));
                            }
                        })
                        .GET("/videos")
                        .routingTo(request -> ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/empty.json"))))
                        )
                        .GET("/channels")
                        .routingTo(request -> {
                            String channelId = request.queryString("id").get();
                            if ("UC0RhatS1pyxInC00YKjjBqQ".equals(channelId)) {
                                return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/channelinformation/Channel_Java_UC0RhatS1pyxInC00YKjjBqQ.json"))));
                            }
                            return ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/empty.json"))));
                        })
                        .GET("/commentThreads")
                        .routingTo(request ->
                                ok(Objects.requireNonNull(DatasetHelper.jsonNodeFromJsonFile(new File("test/dataset/comments/zero_comments.json"))))
                        )
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
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchVideosByKeywords());
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

    @Test
    public void getSimilarityStatsTestWithoutSession() {
        youtubeAnalyzerController.sessionActor
                .tell(new SessionActor.CreateUser("chrome", youtubeAnalyzerController.supervisorActor), ActorRef.noSender());
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchSimilarityStats("hello world"));
        requestBuilder.header("User-Agent", "chrome");
        Result result = youtubeAnalyzerController.fetchSimilarityStats(requestBuilder.build(), "hello world").toCompletableFuture().join();
        Assert.assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void getSimilarityStatsTestWithSession() {
        youtubeAnalyzerController.sessionActor
                .tell(new SessionActor.CreateUser("chrome", youtubeAnalyzerController.supervisorActor), ActorRef.noSender());
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchSimilarityStats("hello world"));
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.getSessionKey(), "chrome");
        Result result = youtubeAnalyzerController.fetchSimilarityStats(requestBuilder.build(), "hello world").toCompletableFuture().join();
        Assert.assertEquals(OK, result.status());
    }

    @Test
    public void fetchChannelInformationAndTop10VideosTest0() {
        youtubeAnalyzerController.sessionActor
                .tell(new SessionActor.CreateUser("chrome", youtubeAnalyzerController.supervisorActor), ActorRef.noSender());
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchChannelInformationAndTop10Videos("UC0RhatS1pyxInC00YKjjBq", "java"));
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.getSessionKey(), "chrome");
        CompletionStage<Result> resultCompletionStage = youtubeAnalyzerController.fetchChannelInformationAndTop10Videos(requestBuilder.build(), "UC0RhatS1pyxInC00YKjjBq", "java");
        Assert.assertEquals(NOT_FOUND, resultCompletionStage.toCompletableFuture().join().status());
    }

    @Test
    public void fetchChannelInformationAndTop10VideosTest1() {
        youtubeAnalyzerController.sessionActor
                .tell(new SessionActor.CreateUser("chrome", youtubeAnalyzerController.supervisorActor), ActorRef.noSender());
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchChannelInformationAndTop10Videos("UC0RhatS1pyxInC00YKjjBqQ", "java"));
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.getSessionKey(), "chrome");
        CompletionStage<Result> resultCompletionStage = youtubeAnalyzerController.fetchChannelInformationAndTop10Videos(requestBuilder.build(), "UC0RhatS1pyxInC00YKjjBqQ", "java");
        Assert.assertEquals(OK, resultCompletionStage.toCompletableFuture().join().status());
    }
}
