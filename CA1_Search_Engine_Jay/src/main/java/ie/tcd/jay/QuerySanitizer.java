package ie.tcd.jay;

public class QuerySanitizer {
    public static String sanitizeQuery(String queryText) {
        String[] terms = queryText.split("\\s+");
        StringBuilder sanitizedQuery = new StringBuilder();
        for (String term : terms) {
            term = term.replaceAll("[*?]", "\\\\$0"); 
            sanitizedQuery.append(term).append(" ");
        }
        return sanitizedQuery.toString().trim();
    }
}
