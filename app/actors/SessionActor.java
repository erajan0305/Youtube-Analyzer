package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.POJO.SearchResults.SearchResults;

import java.util.HashMap;

/**
 * Documentation for the {@link SessionActor} class.
 *
 * @author Kishan Bhimani, Rajan Shah and Umang Patel
 */
public class SessionActor extends AbstractActor {
    private final HashMap<String, ActorRef> activeUsers = new HashMap<>();

    /**
     * Protocol message for creating a user.
     *
     * @author Kishan Bhimani, Rajan Shah and Umang Patel
     */
    public static class CreateUser {
        private final String userId;
        private final ActorRef supervisorActor;

        /**
         * Constructor for the {@link CreateUser} protocol message.
         * @param userId is the user ID.
         * @param supervisorActor is the supervisor actor supervising this actor.
         *
         * @author Kishan Bhimani, Rajan Shah and Umang Patel
         */
        public CreateUser(String userId, ActorRef supervisorActor) {
            this.userId = userId;
            this.supervisorActor = supervisorActor;
        }
    }

    /**
     * Protocol message for getting a user.
     *
     * @author Kishan Bhimani, Rajan Shah and Umang Patel
     */
    public static class GetUser {
        public final String userId;

        /**
         * Constructor for the {@link GetUser} protocol message.
         * @param userId is the user ID.
         *
         * @author Kishan Bhimani, Rajan Shah and Umang Patel
         */
        public GetUser(String userId) {
            this.userId = userId;
        }
    }

    /**
     * Protocol message for retrieving the search results of a user.
     *
     * @author Kishan Bhimani, Rajan Shah and Umang Patel
     */
    public static class GetUserSearchResults {
        public final String userId;

        /**
         * Constructor for the {@link GetUserSearchResults} protocol message.
         * @param userId is the user ID.
         *
         * @author Kishan Bhimani, Rajan Shah and Umang Patel
         */
        public GetUserSearchResults(String userId) {
            this.userId = userId;
        }
    }

    /**
     * Protocol message for appending the search results of a user.
     *
     * @author Kishan Bhimani, Rajan Shah and Umang Patel
     */
    public static class AddSearchResultsToUser {
        public final String userId;
        public final String key;
        public final SearchResults searchResults;

        /**
         * Constructor for the {@link AddSearchResultsToUser} protocol message.
         * @param userId is the user ID.
         * @param key is the search keyword.
         * @param searchResults is the search results.
         *
         * @author Kishan Bhimani, Rajan Shah and Umang Patel
         */
        public AddSearchResultsToUser(String userId, String key, SearchResults searchResults) {
            this.userId = userId;
            this.key = key;
            this.searchResults = searchResults;
        }
    }

    /**
     * Factory method for instantiating the {@link SessionActor}.
     *
     * @author Kishan Bhimani, Rajan Shah and Umang Patel
     */
    public static Props props() {
        return Props.create(SessionActor.class);
    }

    /**
     * Message handling method for {@link SessionActor}
     * Overridden from the {@link AbstractActor} class.
     *
     * @author Kishan Bhimani, Rajan Shah and Umang Patel
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateUser.class, this::addUserToCurrentRecord)
                .match(AddSearchResultsToUser.class, this::addSearchResultsToUserRecord)
                .match(GetUser.class, this::getUser)
                .match(GetUserSearchResults.class, this::getUserSearchResults)
                .build();
    }

    /**
     * Helper method for handling user creation.
     * @param createUser is the message protocol for creating a user
     *
     * @author Kishan Bhimani, Rajan Shah and Umang Patel
     */
    private void addUserToCurrentRecord(CreateUser createUser) {
        ActorRef user = getContext().actorOf(UserActor.props(createUser.userId, createUser.supervisorActor));
        activeUsers.put(createUser.userId, user);
    }

    /**
     * Helper method for handling appending search results of a user.
     * @param addSearchResultsToUser is the message protocol for appending the search results of a user.
     *
     * @author Kishan Bhimani, Rajan Shah and Umang Patel
     */
    private void addSearchResultsToUserRecord(AddSearchResultsToUser addSearchResultsToUser) {
        ActorRef user = activeUsers.get(addSearchResultsToUser.userId);
        user.tell(new UserActor.AddSearchResult(addSearchResultsToUser.userId, addSearchResultsToUser.key, addSearchResultsToUser.searchResults), self());
    }

    /**
     * Helper method for handling the retrieval of a user.
     * @param getUser is the message protocol for retrieving the user.
     *
     * @author Kishan Bhimani, Rajan Shah and Umang Patel
     */
    private void getUser(GetUser getUser) {
        ActorRef user = activeUsers.get(getUser.userId);
        getSender().tell(user, getSelf());
    }

    /**
     * Helper method for handling the retrieval of search results of a user.
     * @param getUserSearchResults is the message protocol for retrieving the search results of a user.
     *
     * @author Kishan Bhimani, Rajan Shah and Umang Patel
     */
    private void getUserSearchResults(GetUserSearchResults getUserSearchResults) {
        ActorRef user = activeUsers.get(getUserSearchResults.userId);
        user.tell(new UserActor.GetSearchResults(getUserSearchResults.userId), getSender());
    }
}
