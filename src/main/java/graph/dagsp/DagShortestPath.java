package graph.dagsp;

import util.GraphModel;
import metrics.BasicMetrics;
import java.util.*;


public class DagShortestPath {

    private final GraphModel graph;
    private final BasicMetrics metrics;

    public DagShortestPath(GraphModel graph, BasicMetrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }


    public Map<String, Integer> shortestFrom(String source) {
        metrics.startTimer();


        List<String> topo = topoSort();
        Map<String, Integer> dist = new HashMap<>();
        for (String v : graph.getAllNodes()) dist.put(v, Integer.MAX_VALUE);
        dist.put(source, 0);


        for (String u : topo) {
            if (dist.get(u) != Integer.MAX_VALUE) {
                for (GraphModel.Edge e : graph.getOutgoing(u)) {
                    metrics.incCounter("relaxEdges");
                    int newDist = dist.get(u) + e.weight;
                    if (newDist < dist.get(e.to)) {
                        dist.put(e.to, newDist);
                    }
                }
            }
        }

        metrics.stopTimer();
        return dist;
    }


    public List<String> criticalPath() {
        metrics.startTimer();

        List<String> topo = topoSort();
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> parent = new HashMap<>();

        for (String v : graph.getAllNodes()) dist.put(v, Integer.MIN_VALUE);
        String start = topo.get(0);
        dist.put(start, 0);


        for (String u : topo) {
            for (GraphModel.Edge e : graph.getOutgoing(u)) {
                metrics.incCounter("relaxEdges");
                int newDist = dist.get(u) + e.weight;
                if (newDist > dist.get(e.to)) {
                    dist.put(e.to, newDist);
                    parent.put(e.to, u);
                }
            }
        }


        String end = start;
        int maxDist = Integer.MIN_VALUE;
        for (String v : dist.keySet()) {
            if (dist.get(v) > maxDist) {
                maxDist = dist.get(v);
                end = v;
            }
        }


        List<String> path = new ArrayList<>();
        while (end != null) {
            path.add(end);
            end = parent.get(end);
        }
        Collections.reverse(path);

        metrics.stopTimer();
        System.out.println("Critical path length = " + maxDist);
        return path;
    }


    private List<String> topoSort() {
        Set<String> visited = new HashSet<>();
        List<String> order = new ArrayList<>();
        for (String node : graph.getAllNodes()) {
            if (!visited.contains(node))
                dfs(node, visited, order);
        }
        Collections.reverse(order);
        return order;
    }

    private void dfs(String u, Set<String> visited, List<String> order) {
        visited.add(u);
        for (GraphModel.Edge e : graph.getOutgoing(u)) {
            if (!visited.contains(e.to)) dfs(e.to, visited, order);
        }
        order.add(u);
    }
}
