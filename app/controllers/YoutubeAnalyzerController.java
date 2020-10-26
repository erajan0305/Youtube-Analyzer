package controllers;

import models.Search;
import models.SearchResults.SearchResults;
import models.WebServiceClient;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.index;
import views.html.similarContent;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

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

    public CompletionStage<Result> fetchVideosByKeywords(Http.Request request) throws ExecutionException, InterruptedException {
        Form<Search> searchForm = formFactory.form(Search.class);
        Map<String, String[]> requestBody = request.body().asFormUrlEncoded();
        WebServiceClient webServiceClient = new WebServiceClient(wsClient);
        String searchKeyword = requestBody.get("searchKeyword")[0];
//        return webServiceClient.fetchVideos(searchKeyword).thenApply(searchResults -> ok(index.render(searchForm, searchResults, searchKeyword.split(" "), messagesApi.preferred(request))));
        CompletionStage<SearchResults> searchResponse = webServiceClient.fetchVideos(requestBody.get("searchKeyword")[0]);
        return searchResponse.thenApply(searchResults -> searchResults.appendViewsCountToItems(searchResults)).thenApply(searchResults -> ok(index.render(searchForm, searchResults, searchKeyword.split(" "), messagesApi.preferred(request))));
//        SearchResults searchResults = Json.fromJson(jsonData, SearchResults.class);
//        searchResults.items.forEach(item -> {
//            try {
//                item.viewCount = webServiceClient.getVideoJsonByVideoId(item.id.videoId);
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
        /*List<String> searchResponse = searchResults.items.stream()
                .map(item -> "<a href='https://www.youtube.com/watch?v=" + item.id.videoId + "' target='_blank'>" + item.snippet.title + "</a>&nbsp;&nbsp;" +
                        "<a href='#' target='_blank'>" + item.snippet.channelTitle + "</a>&nbsp;&nbsp;" +
                        item.viewCount + " " + item.snippet.publishTime)
                .collect(Collectors.toList());*/
//        return ok(index.render(searchForm, searchResponse, requestBody.get("searchKeyword")[0].split(" "), messagesApi.preferred(request)));
    }

    public Result fetchSimilarityStats(String term) {
        return ok(similarContent.render());
    }
}
