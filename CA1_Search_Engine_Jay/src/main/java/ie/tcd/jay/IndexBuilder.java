package ie.tcd.jay;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexBuilder {
    private static final String INDEX_DIRECTORY = "../Index";
    private static final String CRANFIELD_DOCS_PATH = "../CranfieldCollection/cran.all.1400";

    public static void buildIndex() throws IOException {
        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        analyzerPerField.put("docID", new KeywordAnalyzer());
        analyzerPerField.put("title", new EnglishAnalyzer());
        analyzerPerField.put("author", new StandardAnalyzer());
        analyzerPerField.put("content", new EnglishAnalyzer());

        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);

        try (Directory indexDirectory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
             IndexWriter indexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig(analyzer).setOpenMode(IndexWriterConfig.OpenMode.CREATE))) {

            List<Document> documents = DocumentParser.parseFiles(CRANFIELD_DOCS_PATH);
            for (Document doc : documents) {
                indexWriter.addDocument(doc);
            }
        }
    }
}
