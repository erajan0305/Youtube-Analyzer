package controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import models.Actors.SessionActor;
import models.Actors.SimilarityContentActor;
import models.Actors.SupervisorActor;
import models.Actors.YoutubeApiClientActor;
import models.Helper.SessionHelper;
import models.Helper.YoutubeAnalyzer;
import models.POJO.Channel.ChannelResultItems;
import models.POJO.SearchResults.SearchResults;
import models.Search;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import views.html.channelInfo;
import views.html.index;
import views.html.similarContent;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static akka.pattern.Patterns.ask;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's pages.
 *
 * @author Rajan Shah, Kishan Bhimani, Umang J Patel
 */
public class YoutubeAnalyzerController extends Controller {

    @Inject
    FormFactory formFactory;
    @Inject
    MessagesApi messagesApi;
    @Inject
    WSClient wsClient;

    YoutubeAnalyzer youtubeAnalyzer;
    ActorSystem actorSystem = ActorSystem.create("ActorSystem");
    ActorRef sessionActor;
    ActorRef supervisorActor;
    ActorRef similarityContentActor;

    /**
     * Controller Constructor
     */
    public YoutubeAnalyzerController() {
        this.youtubeAnalyzer = new YoutubeAnalyzer();
        this.sessionActor = actorSystem.actorOf(SessionActor.props());
        this.supervisorActor = actorSystem.actorOf(SupervisorActor.props(wsClient));
        this.similarityContentActor = actorSystem.actorOf(SimilarityContentActor.props(this.sessionActor));
    }

    /**
     * Instantiates the YoutubeAnalyzer helper class object
     */
    public void setYoutubeAnalyzer(YoutubeAnalyzer youtubeAnalyzer) {
        this.youtubeAnalyzer = youtubeAnalyzer;
    }

    /**
     * Instantiates the FormFactory object
     */
    public void setFormFactory(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    /**
     * Instantiates the Play Framework MessagesApi object
     */
    public void setMessagesApi(MessagesApi messagesApi) {
        this.messagesApi = messagesApi;
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
    public Result index(Http.Request request) {
        Form<Search> searchForm = formFactory.form(Search.class);
        if (this.youtubeAnalyzer.getWsClient() == null) {
            this.youtubeAnalyzer.setWsClient(wsClient);
        }
        supervisorActor.tell(new YoutubeApiClientActor.SetWSClient(wsClient), ActorRef.noSender());
        if (!SessionHelper.isSessionExist(request)) {
            System.out.println("\n\nCreating session");
            sessionActor.tell(new SessionActor.CreateUser(SessionHelper.getUserAgentNameFromRequest(request)), ActorRef.noSender());
            return ok(index.render(searchForm, null, messagesApi.preferred(request)))
                    .addingToSession(request, SessionHelper.getSessionKey(), SessionHelper.getUserAgentNameFromRequest(request));
        }

        System.out.println("\n\nSession Exist");
        LinkedHashMap<String, SearchResults> existingSearchResults = FutureConverters.toJava(
                ask(sessionActor, new SessionActor.GetUserSearchResults(SessionHelper.getUserAgentNameFromRequest(request)), 2000))
                .thenApply(o -> (LinkedHashMap<String, SearchResults>) o)
                .toCompletableFuture()
                .join();

        return ok(index.render(searchForm, existingSearchResults, messagesApi.preferred(request)));
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
        if (!SessionHelper.isSessionExist(request)) {
            return CompletableFuture.completedFuture(unauthorized("No Session Exist"));
        }
        Form<Search> searchForm = formFactory.form(Search.class);
        Map<String, String[]> requestBody = request.body().asFormUrlEncoded();
        String searchKeyword = requestBody.get("searchKeyword")[0];
        CompletionStage<SearchResults> searchResponsePromise = this.youtubeAnalyzer.fetchVideos(searchKeyword);
//        CompletionStage<SearchResults> searchResponsePromise = FutureConverters.toJava(ask(supervisorActor, new YoutubeApiClientActor.FetchVideos(searchKeyword), 5000))
//                .thenApply(o -> (SearchResults) o);


        return searchResponsePromise.thenApply(searchResults -> {
            searchResults.getItems().parallelStream()
                    .map(searchResultItem -> CompletableFuture.allOf(
                            youtubeAnalyzer.getViewCountByVideoId(searchResultItem.getId().getVideoId())
                                    .thenApply(countString -> {
                                        searchResultItem.setViewCount(countString);
                                        return searchResultItem;
                                    }).toCompletableFuture(),
                            youtubeAnalyzer.getSentimentPerVideo(searchResultItem.getId().getVideoId())
                                    .thenApply(commentSentiment -> {
                                        searchResultItem.setCommentSentiment(commentSentiment);
                                        return searchResultItem;
                                    }).toCompletableFuture()
                    )).map(CompletableFuture::join).collect(Collectors.toList());

            sessionActor.tell(new SessionActor.AddSearchResultsToUser(SessionHelper.getUserAgentNameFromRequest(request)
                    , searchKeyword, searchResults), ActorRef.noSender());

            System.out.println("getting searchResults");
            LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap = FutureConverters.toJava(
                    ask(sessionActor, new SessionActor.GetUserSearchResults(SessionHelper.getUserAgentNameFromRequest(request)), 1000))
                    .thenApply(o -> (LinkedHashMap<String, SearchResults>) o)
                    .toCompletableFuture()
                    .join();

            //SessionHelper.setSessionSearchResultsHashMap(request, searchKeyword, searchResults);
            //return ok(index.render(searchForm, SessionHelper.getSearchResultsHashMapFromSession(request), messagesApi.preferred(request)));
            return ok(index.render(searchForm, searchResultsLinkedHashMap, messagesApi.preferred(request)));
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
//      if (SessionHelper.getSearchResultsHashMapFromSession(request) == null
//                || SessionHelper.getSearchResultsHashMapFromSession(request).get(keyword) == null) {
//            return notFound(similarContent.render(null));
//      }
//      Map<String, Long> similarityStatsMap = this.youtubeAnalyzer
//               .getSimilarityStats(SessionHelper.getSearchResultsHashMapFromSession(request), keyword);

        return FutureConverters.toJava(ask(similarityContentActor, new SimilarityContentActor.SimilarContentByKeyword(SessionHelper.getUserAgentNameFromRequest(request), keyword), 2000))
                .thenApply(o -> (LinkedHashMap<String, Long>) o)
                .thenApply(similarityStatsMap ->  ok(similarContent.render(similarityStatsMap)));
    }

    /**
     * <p>
     * An action that renders an HTML page with a Channel Information and Top 10 Videos for that channel.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/:id/:keyword</code>.<br>
     * Searches for videos by <code>id,keyword</code> sent in request query.<br>
     * Fetches channel information and 10 latest videos sorted by <code>date</code> and <code>id</code}.
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
        CompletionStage<ChannelResultItems> channelItemPromise = this.youtubeAnalyzer.getChannelInformationByChannelId(id);
        CompletionStage<SearchResults> videosJsonByChannelIdSearchPromise = this.youtubeAnalyzer.getVideosJsonByChannelId(id, keyword);

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
