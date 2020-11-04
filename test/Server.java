import models.Helper.YouTubeApiClient;
import play.Mode;
import play.libs.ws.WSClient;
import play.routing.RoutingDsl;

import static play.mvc.Results.ok;

public class Server {
    private YouTubeApiClient youTubeApiClient;
    private WSClient wsTestClient;
    private play.server.Server server;

    public void setup() {
        server = play.server.Server.forRouter(Mode.TEST,
                (components) -> RoutingDsl.fromComponents(components)
                        .GET("/search")
                        .routingTo(request -> ok()
                                .sendResource(""))
                        .build());
        wsTestClient = play.test.WSTestClient.newClient(server.httpPort());
        youTubeApiClient = new YouTubeApiClient(wsTestClient);
        youTubeApiClient.BASE_URL = "/";
    }

    public void fetchVideos() {

    }
}
