package util;

import java.util.List;
import java.util.Map;


public class GraphModel {
    public static class Edge {
        public final String from;
        public final String to;
        public final int weight;

        public Edge(String from, String to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }


    private final Map<String, List<Edge>> adj;

    public GraphModel(Map<String, List<Edge>> adj) {
        this.adj = adj;
    }

    public Map<String, List<Edge>> getAdjacency() {
        return adj;
    }

    public List<Edge> getOutgoing(String node) {
        return adj.getOrDefault(node, List.of());
    }

    public List<String> getAllNodes() {
        return List.copyOf(adj.keySet());
    }
}
