package graph.topo;

import java.util.*;
import metrics.BasicMetrics;


public class TopologicalSorter {

    private final Map<Integer, List<Integer>> dag;
    private final BasicMetrics metrics;

    public TopologicalSorter(Map<Integer, List<Integer>> dag, BasicMetrics metrics) {
        this.dag = dag;
        this.metrics = metrics;
    }

    public List<Integer> topoOrder() {
        List<Integer> order = new ArrayList<>();
        Map<Integer, Integer> indegree = new HashMap<>();


        for (int u : dag.keySet()) {
            indegree.putIfAbsent(u, 0);
            for (int v : dag.get(u)) {
                indegree.put(v, indegree.getOrDefault(v, 0) + 1);
            }
        }


        Deque<Integer> queue = new ArrayDeque<>();
        for (int node : indegree.keySet()) {
            if (indegree.get(node) == 0) {
                queue.add(node);
            }
        }


        while (!queue.isEmpty()) {
            int u = queue.remove();
            metrics.incCounter("popOps");
            order.add(u);

            for (int v : dag.getOrDefault(u, List.of())) {
                metrics.incCounter("edgesSeen");
                indegree.put(v, indegree.get(v) - 1);
                if (indegree.get(v) == 0) {
                    queue.add(v);
                    metrics.incCounter("pushOps");
                }
            }
        }


        if (order.size() != dag.size()) {
            System.out.println("⚠ DAG may have missing nodes or hidden cycle!");
        }

        return order;
    }
}
