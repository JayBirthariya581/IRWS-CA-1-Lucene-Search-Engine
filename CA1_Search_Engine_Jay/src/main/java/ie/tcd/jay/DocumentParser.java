package ie.tcd.jay;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DocumentParser {
    public static List<Document> parseFiles(String filePath) throws IOException {
        List<Document> documents = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        Document currentDoc = null;
        StringBuilder contentBuilder = new StringBuilder();
        String currentField = "";

        try (var modelIn = Files.newInputStream(Paths.get("../models/en-token.bin"))) {
            TokenizerModel model = new TokenizerModel(modelIn);
            TokenizerME tokenizer = new TokenizerME(model);

            for (String line : lines) {
                line = line.trim();
                if (line.startsWith(".I")) {
                    if (currentDoc != null) {
                        Tokenizer.addContentToDocument(currentDoc, contentBuilder, currentField, tokenizer);
                        documents.add(currentDoc);
                    }

                    currentDoc = new Document();
                    contentBuilder.setLength(0);
                    String docId = line.substring(3).trim();
                    currentDoc.add(new StringField("docID", docId, StringField.Store.YES));
                    currentField = "";
                } else if (line.startsWith(".T")) {
                    currentField = "title";
                    contentBuilder.setLength(0);
                } else if (line.startsWith(".A")) {
                    currentField = "author";
                    contentBuilder.setLength(0);
                } else if (line.startsWith(".W")) {
                    currentField = "content";
                    contentBuilder.setLength(0);
                } else {
                    if (contentBuilder.length() > 0) {
                        contentBuilder.append(" ");
                    }
                    contentBuilder.append(line);
                }
            }

            if (currentDoc != null) {
                Tokenizer.addContentToDocument(currentDoc, contentBuilder, currentField, tokenizer);
                documents.add(currentDoc);
            }
        }
        return documents;
    }
}
