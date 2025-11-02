package graph.scc;

import util.GraphModel;
import metrics.BasicMetrics;

import java.util.*;


public class SCCFinder {

    private final GraphModel graph;
    private final BasicMetrics metrics;

    private Map<String, Integer> disc;
    private Map<String, Integer> low;
    private Deque<String> stack;
    private Set<String> onStack;
    private int time;

    public SCCFinder(GraphModel graph, BasicMetrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public List<List<String>> findSCCs() {
        List<List<String>> sccs = new ArrayList<>();
        disc = new HashMap<>();
        low = new HashMap<>();
        stack = new ArrayDeque<>();
        onStack = new HashSet<>();
        time = 0;

        for (String node : graph.getAllNodes()) {
            if (!disc.containsKey(node)) {
                dfs(node, sccs);
            }
        }

        return sccs;
    }

    private void dfs(String u, List<List<String>> sccs) {
        metrics.incCounter("dfsVisits");

        disc.put(u, time);
        low.put(u, time);
        time++;
        stack.push(u);
        onStack.add(u);

        for (GraphModel.Edge e : graph.getOutgoing(u)) {
            metrics.incCounter("edgesSeen");
            String v = e.to;

            if (!disc.containsKey(v)) {
                dfs(v, sccs);
                low.put(u, Math.min(low.get(u), low.get(v)));
            } else if (onStack.contains(v)) {
                low.put(u, Math.min(low.get(u), disc.get(v)));
            }
        }


        if (low.get(u).equals(disc.get(u))) {
            List<String> component = new ArrayList<>();
            String node;
            do {
                node = stack.pop();
                onStack.remove(node);
                component.add(node);
            } while (!node.equals(u));
            sccs.add(component);
        }
    }


    public Map<Integer, List<Integer>> buildCondensation(List<List<String>> sccs) {
        Map<String, Integer> compIndex = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (String node : sccs.get(i)) {
                compIndex.put(node, i);
            }
        }

        Map<Integer, Set<Integer>> temp = new HashMap<>();
        for (String u : graph.getAllNodes()) {
            int cu = compIndex.get(u);
            for (GraphModel.Edge e : graph.getOutgoing(u)) {
                int cv = compIndex.get(e.to);
                if (cu != cv) {
                    temp.computeIfAbsent(cu, k -> new HashSet<>()).add(cv);
                }
            }
        }


        Map<Integer, List<Integer>> dag = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            dag.put(i, new ArrayList<>(temp.getOrDefault(i, Set.of())));
        }

        return dag;
    }
}
