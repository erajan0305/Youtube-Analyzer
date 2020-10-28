package controllers;

import models.Search;
import models.SearchResults.SearchResultItem;
import models.SearchResults.SearchResults;
import models.YouTubeClient;
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
import java.util.*;
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

    static HashMap<String, SearchResults> searchResultHashMap = new HashMap<>();

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index(Http.Request request) {
        Form<Search> searchForm = formFactory.form(Search.class);
        return ok(index.render(searchForm, null, messagesApi.preferred(request)));
    }

    public CompletionStage<Result> fetchVideosByKeywords(Http.Request request) {
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
            searchResultHashMap.put(searchKeyword, searchResult);
            return ok(index.render(searchForm, searchResultHashMap, messagesApi.preferred(request)));
        });
    }

    /**
     * This Method uses static hashmap {@link YoutubeAnalyzerController#searchResultHashMap} and processes
     * all the {@link SearchResults} objects to creates a hashmap of words used in the title against it's count
     * and passes it to {@link similarContent} view for rendering.
     *
     * @author Kishan Bhimani
     *
     */
    public Result fetchSimilarityStats() {
        List<String> tokens = searchResultHashMap
                .values()
                .stream()
                .flatMap(searchResults -> searchResults.items.stream())
                .map(searchResultItem -> searchResultItem.snippet.title)
                .flatMap(title -> Arrays.stream(title.split(" ").clone())) // Split title into words
                .collect(toList());

        Map<String, Long> similarityStatsMap =
                tokens.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.groupingBy(identity(), counting()))// creates map of (unique words, count)
                        .entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue(reverseOrder()))
                        .collect(toMap(entry -> entry.getKey(), entry -> entry.getValue(), (a, b) -> a,
                                LinkedHashMap::new)); // hashmap is unordered, overrode toMap constructor to make it ordered.
        return ok(similarContent.render(similarityStatsMap));
    }

    public CompletionStage<Result> fetchChannelInformation(Http.Request request, String id) {
        YouTubeClient youTubeClient = new YouTubeClient(wsClient);
        CompletionStage<SearchResults> videosJsonByChannelIdSearchPromise = youTubeClient.getVideosJsonByChannelId(id);
        return videosJsonByChannelIdSearchPromise.thenApply(searchResults -> ok(channelInfo.render(searchResults, "", messagesApi.preferred(request))));
    }
}
