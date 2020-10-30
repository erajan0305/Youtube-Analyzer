package controllers;

import models.Channel.ChannelItem;
import models.Channel.ChannelResultItems;
import models.Helper.SessionHelper;
import models.Helper.YouTubeClient;
import models.Search;
import models.SearchResults.SearchResultItem;
import models.SearchResults.SearchResults;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.channelInfo;
import views.html.index;
import views.html.similarContent;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class YoutubeAnalyzerController extends Controller {

    @Inject
    WSClient wsClient;
    @Inject
    FormFactory formFactory;
    @Inject
    MessagesApi messagesApi;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index(Http.Request request) {
        Form<Search> searchForm = formFactory.form(Search.class);
        if (!SessionHelper.isSessionExist(request)) {
            SessionHelper.setSession(request, new LinkedHashMap<>());
            return ok(index.render(searchForm, null, messagesApi.preferred(request)))
                    .addingToSession(request, SessionHelper.SESSION_KEY, SessionHelper.getUserAgentNameFromRequest(request));
        }
        return ok(index.render(searchForm, SessionHelper.getSearchResultsHashMapFromSession(request), messagesApi.preferred(request)));
    }

    public CompletionStage<Result> fetchVideosByKeywords(Http.Request request) {
        if (!SessionHelper.isSessionExist(request)) {
            return CompletableFuture.completedFuture(unauthorized("No Session Exist"));
        }
        Form<Search> searchForm = formFactory.form(Search.class);
        Map<String, String[]> requestBody = request.body().asFormUrlEncoded();
        String searchKeyword = requestBody.get("searchKeyword")[0];
        YouTubeClient youTubeClient = new YouTubeClient(wsClient);
        CompletionStage<SearchResults> searchResponsePromise = youTubeClient.fetchVideos(searchKeyword);
        searchResponsePromise.thenApply(searchResults -> searchResults.items.parallelStream()
                .map(SearchResultItem::appendViewCountToItem)
                .peek(item -> System.out.println(item.viewCount))
                .collect(Collectors.toList()));
        // TODO: assign viewCount to item. Currently it is null even after assigning in `peek`.
        return searchResponsePromise.thenApply(searchResult -> {
            LinkedHashMap<String, SearchResults> searchResultsHashMap = SessionHelper.getSearchResultsHashMapFromSession(request);
            if (searchResultsHashMap == null || searchResultsHashMap.isEmpty()) {
                searchResultsHashMap = new LinkedHashMap<>();
            }
            searchResultsHashMap.put(searchKeyword, searchResult);
            SessionHelper.setSession(request, searchResultsHashMap);
            return ok(index.render(searchForm, SessionHelper.getSearchResultsHashMapFromSession(request), messagesApi.preferred(request)));
        });
    }

    /**
     * @author Kishan Bhimani
     * <p>
     * Calculates similarity-level statistic for videos from search results, counting all unique words in the
     * video title in descending order.
     * <p>
     * {@return ok {@link similarContent}}
     */
    public Result fetchSimilarityStats(Http.Request request, String keyword) {
        if (!SessionHelper.isSessionExist(request)) {
            return unauthorized("No Session Exist");
        }
        if (SessionHelper.getSearchResultsHashMapFromSession(request) == null
                || SessionHelper.getSearchResultsHashMapFromSession(request).get(keyword) == null) {
            return notFound(similarContent.render(null));
        }

        List<String> tokens = SessionHelper.getSearchResultsHashMapFromSession(request)
                .get(keyword)
                .items
                .stream()
                .map(searchResultItem -> searchResultItem.snippet.title)
                .flatMap(title -> Arrays.stream(title.split("\\s+").clone()))
                .map(s -> s.replaceAll("[^\\p{Alpha}]", ""))// Split title into words// Split title into words
                .filter(s -> !s.isEmpty())
                .collect(toList());

        Map<String, Long> similarityStatsMap =
                tokens.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.groupingBy(identity(), counting()))// creates map of (unique words, count)
                        .entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue(reverseOrder()))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a,
                                LinkedHashMap::new)); // hashmap is unordered, overrode toMap constructor to make it ordered.
        return ok(similarContent.render(similarityStatsMap));
    }

    /**
     * @author Rajan Shah
     * <p>
     * Fetches channel information and 10 latest videos sorted by date, by {@param id}.
     * <p>
     * {@param id: channel id for which information is requested}
     * {@return ok {@link ChannelItem} and {@link SearchResults}}
     */
    public CompletionStage<Result> fetchChannelInformation(Http.Request request, String id) {
        if (!SessionHelper.isSessionExist(request)) {
            return CompletableFuture.completedFuture(unauthorized("No Session Exist"));
        }
        YouTubeClient youTubeClient = new YouTubeClient(wsClient);
        CompletionStage<ChannelResultItems> channelItemPromise = youTubeClient.getChannelInformationByChannelId(id);
        CompletionStage<SearchResults> videosJsonByChannelIdSearchPromise = youTubeClient.getVideosJsonByChannelId(id);
        return channelItemPromise.thenCompose(channelItem -> videosJsonByChannelIdSearchPromise
                .thenApply(videoJsonByChannelId -> ok(channelInfo.render(videoJsonByChannelId, channelItem.items.get(0), messagesApi.preferred(request)))));
    }
}
