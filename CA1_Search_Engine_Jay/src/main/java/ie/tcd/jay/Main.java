package ie.tcd.jay;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        IndexBuilder.buildIndex(); // Step 1: Index the Cranfield Collection
//        QueryProcessor.searchWithBM25(); //Query the index using BM25
        QueryProcessor.searchWithVectorSpaceModel(); //Query the index using BM25
    }
}
