package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Search;
import models.WebServiceClient;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.index;

import javax.inject.Inject;
import java.util.Map;
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
        return ok(index.render(searchForm, "", messagesApi.preferred(request)));
    }

    public Result fetchVideosByKeywords(Http.Request request) throws ExecutionException, InterruptedException {
        Form<Search> searchForm = formFactory.form(Search.class);
//        Form<Search> postedSearchForm = formFactory.form(Search.class).bindFromRequest(request);
//        Search search = postedSearchForm.get();
        Map<String, String[]> requestBody = request.body().asFormUrlEncoded();
        WebServiceClient webServiceClient = new WebServiceClient(wsClient);
        JsonNode jsonData = webServiceClient.fetchVideos(requestBody.get("searchKeyword")[0]);
//        System.out.println(jsonData);
        return ok(index.render(searchForm, jsonData.asText(), messagesApi.preferred(request)));
    }
}
