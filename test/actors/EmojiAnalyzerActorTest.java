package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.vdurmont.emoji.EmojiManager;
import dataset.DatasetHelper;
import models.POJO.Comments.CommentResults;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import scala.compat.java8.FutureConverters;

import static akka.pattern.Patterns.ask;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

/**
 * Unit tests for the {@link EmojiAnalyzerActor}.
 *
 * @author Umang Patel
 */
public class EmojiAnalyzerActorTest {

    static ActorSystem actorSystem;
    static ActorRef emojiActor;

    /**
     * Initialization code for the unit tests
     *
     * @author Umang Patel
     */
    @BeforeClass
    public static void init() {
        actorSystem = ActorSystem.create();
        emojiActor = actorSystem.actorOf(EmojiAnalyzerActor.props());
    }

    /**
     * Cleanup code for the unit tests
     *
     * @author Umang Patel
     */
    @AfterClass
    public static void destroy() {
        actorSystem.terminate();
        emojiActor = null;
    }

    /**
     * Checks whether happy comments are analyzed for an overall happy emoji result.
     *
     * @author Umang Patel
     */
    @Test
    public void testHappyEmojiFromComments() {
        final CompletableFuture<CommentResults> happyCommentsPromise = fetchDummyComments("happy_video");
        CompletableFuture<String> emojiResponse = getEmojiResponse(happyCommentsPromise);
        assertEquals(emojiResponse.join(), EmojiManager.getForAlias("grin").getUnicode());
    }

    /**
     * Checks whether sad comments are analyzed for an overall sad emoji result.
     *
     * @author Umang Patel
     */
    @Test
    public void testSadEmojiFromComments() {
        final CompletableFuture<CommentResults> sadCommentsPromise = fetchDummyComments("sad_video");
        CompletableFuture<String> emojiResponse = getEmojiResponse(sadCommentsPromise);
        assertEquals(emojiResponse.join(), EmojiManager.getForAlias("pensive").getUnicode());
    }

    /**
     * Checks whether neutral comments are analyzed for an overall neutral emoji result.
     *
     * @author Umang Patel
     */
    @Test
    public void testNeutralEmojiFromComments() {
        final CompletableFuture<CommentResults> neutralCommentsPromise = fetchDummyComments("neutral_video");
        CompletableFuture<String> emojiResponse = getEmojiResponse(neutralCommentsPromise);
        assertEquals(emojiResponse.join(), EmojiManager.getForAlias("neutral_face").getUnicode());
    }

    /**
     * Checks whether empty/zero comments are analyzed for an overall neutral emoji result.
     *
     * @author Umang Patel
     */
    @Test
    public void testNeutralEmojiFromNoComments() {
        final CompletableFuture<CommentResults> noCommentsPromise = fetchDummyComments("null");
        CompletableFuture<String> emojiResponse = getEmojiResponse(noCommentsPromise);
        assertEquals(emojiResponse.join(), EmojiManager.getForAlias("neutral_face").getUnicode());
    }

    /**
     * This method tests the {@link EmojiAnalyzerActor.EmojiAnalyzer#processCommentStream(Stream commentStream)} method and matches the result with the expected result.
     * @throws ExecutionException for safety
     * @throws InterruptedException for safety
     *
     * @author Umang Patel
     *
     */
    @Test
    public void testHappyAnalysisResultWithExpectedOutput() throws ExecutionException, InterruptedException {
        final CommentResults commentResults = fetchDummyComments("happy_video").get();
        Stream<String> commentStream = commentResults.getItems().parallelStream()
                .map(commentResultItem -> commentResultItem.getCommentSnippet().getTopLevelComment().getSnippet().getTextOriginal().trim());
        String happyEmojiUnicode = EmojiManager.getForAlias("heart_eyes").getUnicode();
        String expectedFilteredCommentString = String.join("", Collections.nCopies(5, happyEmojiUnicode));
        assertEquals(expectedFilteredCommentString, EmojiAnalyzerActor.EmojiAnalyzer.processCommentStream(commentStream));
    }

    /**
     * This method tests the {@link EmojiAnalyzerActor.EmojiAnalyzer#processCommentStream(Stream commentStream)} method for empty comments or comments with no emojis
     * and matches the result with the expected result.
     *
     * @author Umang Patel
     */

    @Test
    public void testEmptyCommentProcessingAndProcessingWithNoEmojis() {
        // Stream of string without emojis or an empty string return empty string.
        assertEquals("", EmojiAnalyzerActor.EmojiAnalyzer.processCommentStream(Stream.of("comments")));
        assertEquals("", EmojiAnalyzerActor.EmojiAnalyzer.processCommentStream(Stream.of("")));
    }

    /**
     * This method tests the {@link EmojiAnalyzerActor.EmojiAnalyzer#encodeEmojiSentiment(String emoji)} method for <code>grin</code> emoji
     * and expects to be encoded to <code>happy</code>.
     *
     * @author Umang Patel
     */
    @Test
    public void testHappyEmojiEncoding() {
        //encodes "grin" emoji as "happy"
        String grinEmoji = EmojiManager.getForAlias("grin").getUnicode();
        assertEquals("happy", EmojiAnalyzerActor.EmojiAnalyzer.encodeEmojiSentiment(grinEmoji));
    }

    /**
     * This method tests the {@link EmojiAnalyzerActor.EmojiAnalyzer#encodeEmojiSentiment(String emoji)} method for <code>pensive</code> emoji
     * and expects to be encoded to <code>sad</code>.
     *
     * @author Umang Patel
     */
    @Test
    public void testSadEmojiEncoding() {
        String pensiveEmoji = EmojiManager.getForAlias("pensive").getUnicode();
        assertEquals("sad", EmojiAnalyzerActor.EmojiAnalyzer.encodeEmojiSentiment(pensiveEmoji));
    }

    /**
     * This method tests the {@link actors.EmojiAnalyzerActor.EmojiAnalyzer#getCommentsString(CommentResults commentResults)} method
     * for processing comments with no emojis and expects to be processed to empty string.
     *
     * @author Umang Patel
     */
    @Test
    public void testEmptyProcessedCommentsWhenEmptyCommentResults() {
        CommentResults commentResults = new CommentResults();
        commentResults.setItems(null);
        assertEquals("", EmojiAnalyzerActor.EmojiAnalyzer.getCommentsString(commentResults));
    }

    /**
     * Helper method to fetch downloaded JSON files for emoji analysis tests.
     * @param commentResultsPromise is the Youtube comments promise
     * @return emoji response in the form of {@link CompletableFuture}
     *
     * @author Umang Patel
     */
    @Ignore
    private CompletableFuture<String> getEmojiResponse(CompletableFuture<CommentResults> commentResultsPromise) {
        return FutureConverters.toJava(ask(emojiActor, new EmojiAnalyzerActor.GetAnalysis(commentResultsPromise), 2000))
                .thenApplyAsync(item -> (CompletableFuture<String>) item)
                .toCompletableFuture()
                .thenApplyAsync(CompletableFuture::join);
    }

    /**
     * Helper method to fetch comments from downloaded JSON files.
     * @param fileName is the name of the file (excluding extension) in {@link String}
     * @return comment results in the form of {@link CompletableFuture}
     *
     * @author Umang Patel
     */
    @Ignore
    private static CompletableFuture<CommentResults> fetchDummyComments(String fileName) {
        return fileName.equals("null") ? null : CompletableFuture.supplyAsync(() ->
                DatasetHelper.jsonFileToObject(new File("test/dataset/comments/" + fileName + ".json"),
                        CommentResults.class)
        );
    }


}