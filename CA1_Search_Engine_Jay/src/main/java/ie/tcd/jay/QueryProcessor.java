package ie.tcd.jay;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import java.util.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryProcessor {
    private static final String INDEX_DIRECTORY = "../Index";
    private static final String QUERY_FILE_PATH = "../CranfieldCollection/cran.qry";
    private static final String RESULTS_FILE_PATH = "../results.txt";

    public static void searchWithBM25() throws IOException, ParseException {
        try (Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
             DirectoryReader ireader = DirectoryReader.open(directory)) {

            IndexSearcher searcher = new IndexSearcher(ireader);
            searcher.setSimilarity(new BM25Similarity());
            queryIndex(searcher, "BM25");
        }
    }


     public static void searchWithVectorSpaceModel() throws IOException, ParseException {
        try (Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
             DirectoryReader ireader = DirectoryReader.open(directory)) {

            IndexSearcher searcher = new IndexSearcher(ireader);
            searcher.setSimilarity(new ClassicSimilarity());
            queryIndex(searcher, "VSM");
        }
    }

    private static void queryIndex(IndexSearcher searcher, String similarityType) throws IOException, ParseException {
        List<String> queries = FileHelper.parseQueries(QUERY_FILE_PATH);

        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        analyzerPerField.put("docID", new KeywordAnalyzer());
        analyzerPerField.put("title", new EnglishAnalyzer());
        analyzerPerField.put("content", new EnglishAnalyzer());

        PerFieldAnalyzerWrapper queryAnalyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
        QueryParser parser = new QueryParser("content", queryAnalyzer);

        List<String> results = new ArrayList<>();
        for (int i = 0; i < queries.size(); i++) {
            String queryText = QuerySanitizer.sanitizeQuery(queries.get(i));
            if (queryText.isEmpty()) {
                continue;
            }
            Query query = parser.parse(queryText);
            TopDocs topDocs = searcher.search(query, 50);

            for (int rank = 0; rank < topDocs.scoreDocs.length; rank++) {
                ScoreDoc scoreDoc = topDocs.scoreDocs[rank];
                Document doc = searcher.doc(scoreDoc.doc);
                String docId = doc.get("docID");
                if (docId != null) {
                    String resultLine = String.format("%d Q0 %s %d %.4f %s", (i + 1), docId, (rank + 1), scoreDoc.score, similarityType);
                    results.add(resultLine);
                }
            }
        }
        FileHelper.writeResults(results, RESULTS_FILE_PATH);
    }
}
