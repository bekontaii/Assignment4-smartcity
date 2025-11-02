package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class GraphGenerator {

    private static final Random rnd = new Random();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws IOException {
        Path dataDir = Path.of("data");
        if (!Files.exists(dataDir)) Files.createDirectories(dataDir);


        generateGraph(dataDir.resolve("small1.json"), 6, 0.3);
        generateGraph(dataDir.resolve("small2.json"), 8, 0.4);
        generateGraph(dataDir.resolve("small3.json"), 10, 0.5);


        generateGraph(dataDir.resolve("medium1.json"), 12, 0.3);
        generateGraph(dataDir.resolve("medium2.json"), 15, 0.5);
        generateGraph(dataDir.resolve("medium3.json"), 18, 0.6);


        generateGraph(dataDir.resolve("large1.json"), 25, 0.3);
        generateGraph(dataDir.resolve("large2.json"), 35, 0.4);
        generateGraph(dataDir.resolve("large3.json"), 50, 0.5);

        System.out.println("✅ Generated 9 DAGs in /data/ (no cycles)");
    }


    public static void generateGraph(Path path, int n, double density) throws IOException {
        List<Map<String, Object>> edges = new ArrayList<>();


        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                if (rnd.nextDouble() < density) {
                    int w = rnd.nextInt(9) + 1;
                    Map<String, Object> edge = new LinkedHashMap<>();
                    edge.put("u", u);
                    edge.put("v", v);
                    edge.put("w", w);
                    edges.add(edge);
                }
            }
        }

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("directed", true);
        json.put("n", n);
        json.put("edges", edges);
        json.put("source", 0);
        json.put("weight_model", "edge");

        try (FileWriter fw = new FileWriter(path.toFile())) {
            gson.toJson(json, fw);
        }
    }
}
