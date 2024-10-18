package ie.tcd.jay;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    public static List<String> parseQueries(String filePath) throws IOException {
        List<String> queries = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        StringBuilder queryBuilder = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith(".I")) {
                if (queryBuilder.length() > 0) {
                    queries.add(queryBuilder.toString().trim());
                }
                queryBuilder.setLength(0);
            } else if (!line.startsWith(".W")) {
                queryBuilder.append(line).append(" ");
            }
        }
        if (queryBuilder.length() > 0) {
            queries.add(queryBuilder.toString().trim());
        }
        return queries;
    }

    public static void writeResults(List<String> results, String filePath) throws IOException {
        Files.write(Paths.get(filePath), results);
    }
}
