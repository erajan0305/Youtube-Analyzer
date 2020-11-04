package models;

import dataset.DatasetHelper;
import models.Helper.YoutubeAnalyzer;
import models.POJO.SearchResults.SearchResults;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class YoutubeAnalyzerTest {
    SearchResults searchResults;
    LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap;
    YoutubeAnalyzer youtubeAnalyzer;

    @Before
    public void init() {
        searchResults = DatasetHelper
                .jsonFileToObject(new File("test/dataset/searchresults/Golang.json"), SearchResults.class);
        searchResultsLinkedHashMap = new LinkedHashMap<String, SearchResults>() {
            {
                put("golang", searchResults);
            }
        };
        youtubeAnalyzer = new YoutubeAnalyzer();
    }

    @Test
    public void getSimilarityStatsTest() {
        Map<String, Long> golangSimilarityStats = youtubeAnalyzer.getSimilarityStats(searchResultsLinkedHashMap, "golang");
        Assert.assertNotNull(golangSimilarityStats);
        Assert.assertTrue(golangSimilarityStats.size() > 0);
    }

    @Test
    public void getSimilarityStatsForSearchWordNotPresentTest() {
        String keyword = "hello world";
        Map<String, Long> golangSimilarityStats = youtubeAnalyzer.getSimilarityStats(searchResultsLinkedHashMap, keyword);
        Map<String, Long> similarityStatsNotFoundMap = new HashMap<String, Long>() {{
            put(keyword, (long) 0);
        }};
        Assert.assertEquals(similarityStatsNotFoundMap.get(keyword), golangSimilarityStats.get(keyword));
    }
}
