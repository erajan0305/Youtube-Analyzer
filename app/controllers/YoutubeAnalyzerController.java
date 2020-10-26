package controllers;

import models.Search;
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
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

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
        return ok(index.render(searchForm, null, null, messagesApi.preferred(request)));
    }

    public CompletionStage<Result> fetchVideosByKeywords(Http.Request request) {
        Form<Search> searchForm = formFactory.form(Search.class);
        Map<String, String[]> requestBody = request.body().asFormUrlEncoded();
        YouTubeClient youTubeClient = new YouTubeClient(wsClient);
        String searchKeyword = requestBody.get("searchKeyword")[0];
        CompletionStage<SearchResults> searchResponsePromise = youTubeClient.fetchVideos(requestBody.get("searchKeyword")[0]);
        searchResponsePromise.thenApply(searchResults -> searchResults.items.parallelStream()
                .peek(item -> youTubeClient.getVideoJsonByVideoId(item.id.videoId)
                        .thenApply(viewCount -> {
                            item.viewCount = viewCount;
                            System.out.println(item.viewCount);
                            return item;
                        })
                ).collect(Collectors.toList()));
        // TODO: assign viewCount to item. Currently it is null even after assigning in `peek`.
        return searchResponsePromise.thenApply(searchResult -> ok(index.render(searchForm, searchResult, searchKeyword.split(" "), messagesApi.preferred(request))));
    }

    public Result fetchSimilarityStats(String term) {
        return ok(similarContent.render());
    }

    public Result fetchChannelInformation(String id) {
        return ok(channelInfo.render(id));
    }
}
