package util;

import com.google.gson.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class GraphLoader {

    public static GraphModel loadFromJson(Path path) {
        try {
            String json = Files.readString(path);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            boolean directed = root.get("directed").getAsBoolean();
            JsonArray edges = root.getAsJsonArray("edges");

            Map<String, List<GraphModel.Edge>> adj = new HashMap<>();

            for (JsonElement e : edges) {
                JsonObject obj = e.getAsJsonObject();
                String u = String.valueOf(obj.get("u").getAsInt());
                String v = String.valueOf(obj.get("v").getAsInt());
                int w = obj.has("w") ? obj.get("w").getAsInt() : 1;

                adj.computeIfAbsent(u, k -> new ArrayList<>()).add(new GraphModel.Edge(u, v, w));


                adj.computeIfAbsent(v, k -> new ArrayList<>());
            }

            return new GraphModel(adj);

        } catch (IOException e) {
            throw new RuntimeException("Error reading graph JSON: " + e.getMessage(), e);
        }
    }
}
