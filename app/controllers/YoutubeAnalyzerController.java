package controllers;

import actors.*;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import com.fasterxml.jackson.databind.JsonNode;
import models.Helper.SessionHelper;
import models.POJO.Channel.ChannelResultItems;
import models.POJO.SearchResults.SearchResults;
import models.Search;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.F;
import play.libs.streams.ActorFlow;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import scala.compat.java8.FutureConverters;
import views.html.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's pages.
 *
 * @author Rajan Shah, Kishan Bhimani, Umang J Patel
 */
@Singleton
public class YoutubeAnalyzerController extends Controller {

    @Inject
    FormFactory formFactory;
    @Inject
    MessagesApi messagesApi;
    @Inject
    WSClient wsClient;
    @Inject
    Materializer materializer;

    ActorSystem actorSystem = ActorSystem.create("ActorSystem");
    ActorRef sessionActor;
    ActorRef supervisorActor;
    ActorRef similarityContentActor;
    ActorRef channelInfoActor;
    ActorRef videosByChannelIdAndKeywordActor;
    ActorRef youtubeApiClientActor;

    /**
     * Controller Constructor
     */
    public YoutubeAnalyzerController() {
        this.sessionActor = actorSystem.actorOf(SessionActor.props(), "sessionActor");
        this.youtubeApiClientActor = actorSystem.actorOf(YoutubeApiClientActor.props(wsClient));
        this.supervisorActor = actorSystem.actorOf(SupervisorActor.props(youtubeApiClientActor), "supervisorActor");
        this.similarityContentActor = actorSystem.actorOf(SimilarityContentActor.props(this.sessionActor), "similarityContentActor");
        this.channelInfoActor = actorSystem.actorOf(ChannelInfoActor.props(supervisorActor), "channelInfoActor");
        this.videosByChannelIdAndKeywordActor = actorSystem.actorOf(VideosActor.props(supervisorActor), "videosByChannelIdActor");
    }

    /**
     * Instantiates the FormFactory object
     * @param formFactory is the form object in Play framework
     */
    public void setFormFactory(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    /**
     * Instantiates the Play Framework MessagesApi object
     * @param messagesApi is the {@link MessagesApi} object from the Play Framework
     */
    public void setMessagesApi(MessagesApi messagesApi) {
        this.messagesApi = messagesApi;
    }

    /**
     * Instantiates the Play Framework Materializer object
     * @param materializer is the {@link Materializer} object from the Play Framework.
     */
    public void setMaterializer(Materializer materializer) {
        this.materializer = materializer;
    }

    /**
     * An action that renders an HTML page with search form and updates the {@link SearchResults} for
     * all the {@link Search#searchKeyword} searched for, after every 30 seconds.
     *
     * @return {@link WebSocket} Json Flow of updated {@link SearchResults}
     * @author Rajan Shah
     */
    public WebSocket ws() {
        return WebSocket.Json.acceptOrResult(this::createFlow);
    }

    /**
     * Checks for existing user and for existing user creates Flow of {@link SearchResults}, and for non-existing user,
     * return error 403.
     *
     * @param requestHeader Request Header of current request.
     * @return CompletionStage of either Flow of {@link SearchResults} or 403 (Forbidden).
     * @author Umang Patel
     */
    protected CompletionStage<F.Either<Result, Flow<JsonNode, JsonNode, ?>>> createFlow(Http.RequestHeader requestHeader) {
        return CompletableFuture.completedFuture(
                requestHeader.session().get("sessionId")
                        .map(user -> F.Either.<Result, Flow<JsonNode, JsonNode, ?>>Right(
                                createFlowOfResults(user)))
                        .orElseGet(() -> F.Either.Left(forbidden())));
    }

    /**
     * Fetches the user information from Session and creates a WebSocket to provide a flow of {@link SearchResults}.
     *
     * @param userName for which to create the Flow of {@link SearchResults}
     * @return Flow of {@link JsonNode} of {@link SearchResults}
     * @author Kishan Bhimani
     */
    private Flow<JsonNode, JsonNode, ?> createFlowOfResults(String userName) {
        ActorRef userActor = FutureConverters.toJava(ask(sessionActor, new SessionActor.GetUser(userName), 5000))
                .toCompletableFuture().thenApply(o -> (ActorRef) o).join();
        return ActorFlow.actorRef(actorRef -> WebSocketActor.props(actorRef, userActor), actorSystem, materializer);
    }

    /**
     * An action that renders an HTML page with a search form.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     *
     * @param request Http Request mapped to the method
     * @return Result <code>OK</code>: 200.
     * @author Kishan Bhimani, Rajan Shah, Umang Patel
     */
    public CompletableFuture<Result> index(Http.Request request) {
        String url = routes.YoutubeAnalyzerController.ws().webSocketURL(request);
        String userAgentName = SessionHelper.getUserAgentNameFromRequest(request);
        Form<Search> searchForm = formFactory.form(Search.class);
        supervisorActor.tell(new YoutubeApiClientActor.SetWSClient(wsClient), ActorRef.noSender());
        if (!SessionHelper.isSessionExist(request)) {
            System.out.println("Creating session");
            sessionActor.tell(new SessionActor.CreateUser(userAgentName, supervisorActor), ActorRef.noSender());
            return CompletableFuture.completedFuture(ok(index.render(searchForm, null, url, messagesApi.preferred(request)))
                    .addingToSession(request, SessionHelper.getSessionKey(), userAgentName));
        }

        System.out.println("Session Exist");
        CompletableFuture<LinkedHashMap<String, SearchResults>> linkedHashMapCompletableFuture = FutureConverters.toJava(
                ask(sessionActor, new SessionActor.GetUserSearchResults(userAgentName), 2000))
                .thenApply(o -> (LinkedHashMap<String, SearchResults>) o)
                .toCompletableFuture();

        return linkedHashMapCompletableFuture.thenApply(existingSearchResults -> ok(index.render(searchForm, existingSearchResults, url, messagesApi.preferred(request))));
    }

    /**
     * <p>
     * An action that renders an HTML page with a search form.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>POST</code> request with a path of <code>/</code>.<br>
     * Searches for videos by <code>searchKeyword</code> sent in request body.
     * </p>
     *
     * @param request Http Request mapped to the method
     * @return Result <code>OK</code>: 200, {@link SearchResults}.
     * @author Rajan Shah, Kishan Bhimani, Umang Patel
     */
    public CompletionStage<Result> fetchVideosByKeywords(Http.Request request) {
        System.out.println("Fetch videos");
        String url = routes.YoutubeAnalyzerController.ws().webSocketURL(request);
        String userAgentName = SessionHelper.getUserAgentNameFromRequest(request);
        if (!SessionHelper.isSessionExist(request)) {
            return CompletableFuture.completedFuture(unauthorized("No Session Exist"));
        }
        Form<Search> searchForm = formFactory.form(Search.class);
        Map<String, String[]> requestBody = request.body().asFormUrlEncoded();
        String searchKeyword = requestBody.get("searchKeyword")[0];
        CompletionStage<SearchResults> searchResponsePromise = FutureConverters.toJava(ask(supervisorActor, new YoutubeApiClientActor.FetchVideos(searchKeyword), 5000))
                .thenApply(o -> (SearchResults) o);
        // try using then compose
        return searchResponsePromise.thenApply(searchResults -> {
            sessionActor.tell(new SessionActor.AddSearchResultsToUser(userAgentName, searchKeyword, searchResults), ActorRef.noSender());

            LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap = FutureConverters.toJava(
                    ask(sessionActor, new SessionActor.GetUserSearchResults(userAgentName), 1000))
                    .thenApply(o -> (LinkedHashMap<String, SearchResults>) o)
                    .toCompletableFuture()
                    .join();
            return ok(index.render(searchForm, searchResultsLinkedHashMap, url, messagesApi.preferred(request)));
        });
    }

    /**
     * <p>
     * An action that renders an HTML page with a similarity stats results.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/stats/:keyword</code>.<br>
     * Calculates similarity-level statistic for videos from {@link SearchResults}, counting all unique words in the
     * video title in descending order.
     * <p>
     *
     * @param request Http Request mapped to the method
     * @param keyword search keyword used to fetch videos.
     * @return Result <code>OK</code>: 200, {@link Map} that contains uniqueWord of type {@link String} as key and
     * count of type {@link Long} as a value.
     * @author Kishan Bhimani
     */
    public CompletionStage<Result> fetchSimilarityStats(Http.Request request, String keyword) {
        if (!SessionHelper.isSessionExist(request)) {
            return CompletableFuture.completedFuture(unauthorized("No Session Exist"));
        }
        return FutureConverters
                .toJava(ask(similarityContentActor, new SimilarityContentActor
                        .SimilarContentByKeyword(SessionHelper.getUserAgentNameFromRequest(request), keyword), 2000))
                .thenApply(o -> (LinkedHashMap<String, Long>) o)
                .thenApply(similarityStatsMap -> ok(similarContent.render(similarityStatsMap)));
    }

    /**
     * <p>
     * An action that renders an HTML page with a Channel Information and Top 10 Videos for that channel.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/:id/:keyword</code>.<br>
     * Searches for videos by <code>id,keyword</code> sent in request query.<br>
     * Fetches channel information and 10 latest videos sorted by <code>date</code> and <code>id</code>}.
     * <p>
     *
     * @param id      channel id for which information is requested
     * @param keyword search key for top 10 videos by <code>id</code>
     * @param request Http Request mapped to the method
     * @return Result <code>OK</code>: 200, {@link ChannelResultItems} and {@link SearchResults}
     * @author Rajan Shah
     */
    public CompletionStage<Result> fetchChannelInformationAndTop10Videos(Http.Request request, String id, String keyword) {
        if (!SessionHelper.isSessionExist(request)) {
            return CompletableFuture.completedFuture(unauthorized("No Session Exist"));
        }
        CompletionStage<ChannelResultItems> channelItemPromise = FutureConverters
                .toJava(ask(channelInfoActor, new ChannelInfoActor.ChannelInfo(id), 5000))
                .thenApply(o -> (ChannelResultItems) o);
        CompletionStage<SearchResults> videosJsonByChannelIdSearchPromise = FutureConverters
                .toJava(ask(videosByChannelIdAndKeywordActor, new VideosActor.VideosList(id, keyword), 5000))
                .thenApply(o -> (SearchResults) o);

        return channelItemPromise.thenCompose(channelResultItems -> videosJsonByChannelIdSearchPromise
                .thenApply(videoJsonByChannelId -> {
                    if (channelResultItems.getItems() == null) {
                        return notFound(channelInfo.render(null, null, messagesApi.preferred(request)));
                    }
                    return ok(channelInfo.render(videoJsonByChannelId, channelResultItems.getItems().get(0), messagesApi.preferred(request)));
                })
        );
    }
}
