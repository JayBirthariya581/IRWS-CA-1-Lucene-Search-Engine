package ie.tcd.jay;

import opennlp.tools.tokenize.TokenizerME;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Tokenizer {
    private static final Set<String> STOPWORDS = Set.of("a", "an", "the", "and", "or", "but", "is", "to", "in", "for", "on", "with", "as", "by");

    public static void addContentToDocument(Document doc, StringBuilder contentBuilder, String field, TokenizerME tokenizer) {
        if (!field.isEmpty() && contentBuilder.length() > 0) {
            String processedContent = preprocessText(contentBuilder.toString(), tokenizer);
            doc.add(new TextField(field, processedContent, TextField.Store.YES));
        }
    }

    private static String preprocessText(String text, TokenizerME tokenizer) {
        String[] tokens = tokenizer.tokenize(text);
        List<String> filteredTokens = new ArrayList<>();
        for (String token : tokens) {
            token = token.toLowerCase();
            if (!STOPWORDS.contains(token)) {
                filteredTokens.add(token);
            }
        }
        return String.join(" ", filteredTokens);
    }
}
