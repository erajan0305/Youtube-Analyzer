package controllers;

import models.WebServiceClient;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class YoutubeAnalyzerController extends Controller {

    @Inject
    WSClient wsClient;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public CompletionStage<Result> index() {
        WebServiceClient webServiceClient = new WebServiceClient(wsClient);
        WSClient wsClient = webServiceClient.getWsClient();
        return wsClient.url("https://www.googleapis.com/youtube/v3/search?part=snippet&q=pikachu&key=")
                .get()
                .thenApply(response -> ok("Feed title: " + response.asJson()
                        .findPath("title")
                        .asText()));
    }
}
