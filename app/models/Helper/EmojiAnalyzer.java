package models.Helper;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmojiAnalyzer {

    private static final List<Emoji> HAPPY_EMOJI_SET = Arrays.asList(
            EmojiManager.getForAlias("smiley"),
            EmojiManager.getForAlias("heart_eyes"),
            EmojiManager.getForAlias("smile"),
            EmojiManager.getForAlias("joy"),
            EmojiManager.getForAlias("blush"),
            EmojiManager.getForAlias("grinning"),
            EmojiManager.getForAlias("grin"),
            EmojiManager.getForAlias("stuck_out_tongue_closed_eyes"),
            EmojiManager.getForAlias("relaxed")
    );

    private static final List<Emoji> SAD_EMOJI_SET = Arrays.asList(
            EmojiManager.getForAlias("pensive"),
            EmojiManager.getForAlias("disappointed"),
            EmojiManager.getForAlias("persevere"),
            EmojiManager.getForAlias("cry"),
            EmojiManager.getForAlias("sob"),
            EmojiManager.getForAlias("worried")
    );

    private static final List<Emoji> EMOJIS_SET = Stream.concat(
            HAPPY_EMOJI_SET.parallelStream(), SAD_EMOJI_SET.parallelStream())
            .collect(Collectors.toList());

    private static final List<String> HAPPY_EMOJI_UNICODE_SET = HAPPY_EMOJI_SET
            .parallelStream()
            .map(Emoji::getUnicode).collect(Collectors.toList());

    public static String processCommentStream(Stream<String> stream) {
        return stream
                .filter(comment -> EmojiParser.extractEmojis(comment).size() != 0)
                .map(comment -> String.join("", EmojiParser.extractEmojis(comment)))
                .map(EmojiAnalyzer::filterFromEmojiSets)
                .filter(comment -> EmojiParser.extractEmojis(comment).size() != 0)
                .collect(Collectors.joining(""));
    }

    public static String filterFromEmojiSets(String string) {
        return EmojiParser.removeAllEmojisExcept(string, EMOJIS_SET);
    }

    public static String encodeEmojiSentiment(String emoji) {
        String parsedEmoji = EmojiParser.parseToUnicode(emoji);
        if (HAPPY_EMOJI_UNICODE_SET.contains(parsedEmoji))
            return "happy";
        else
            return "sad";
    }

    public static String generateReport(String comments) {
        Map<String, Long> emojiCounts = EmojiParser.extractEmojis(comments).parallelStream()
                .collect(Collectors.groupingBy(EmojiAnalyzer::encodeEmojiSentiment,
                        Collectors.counting()));
        // System.out.println(emojiCounts);
        Long totalCounts = emojiCounts.values().parallelStream().reduce(0L, Long::sum);
        Map<String, Float> result = emojiCounts.entrySet().parallelStream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue() * 100.0f) / totalCounts));
        // System.out.println(result);
        if (result.getOrDefault("happy", 0.0f) >= 70.0f)
            return EmojiManager.getForAlias("grin").getUnicode();
        else if (result.getOrDefault("sad", 0.0f) >= 70.0f)
            return EmojiManager.getForAlias("pensive").getUnicode();
        else
            return EmojiManager.getForAlias("neutral_face").getUnicode();
    }


}
