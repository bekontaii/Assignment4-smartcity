package graph.tests;

import graph.scc.SCCFinder;
import metrics.BasicMetrics;
import util.GraphModel;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class SCCFinderTest {

    @Test
    public void testSingleCycleSCC() {

        Map<String, List<GraphModel.Edge>> adj = new HashMap<>();
        adj.put("0", List.of(new GraphModel.Edge("0", "1", 1)));
        adj.put("1", List.of(new GraphModel.Edge("1", "2", 1)));
        adj.put("2", List.of(new GraphModel.Edge("2", "0", 1)));

        GraphModel graph = new GraphModel(adj);
        BasicMetrics metrics = new BasicMetrics();

        SCCFinder sccFinder = new SCCFinder(graph, metrics);
        List<List<String>> sccs = sccFinder.findSCCs();

        // Проверяем: ровно одна компонента и в ней три вершины
        assertEquals(1, sccs.size(), "Should detect one SCC");
        Set<String> nodes = new HashSet<>(sccs.get(0));
        assertEquals(Set.of("0", "1", "2"), nodes, "SCC should contain 0,1,2");
    }

    @Test
    public void testTwoSeparateComponents() {

        Map<String, List<GraphModel.Edge>> adj = new HashMap<>();
        adj.put("0", List.of(new GraphModel.Edge("0", "1", 1)));
        adj.put("1", List.of());
        adj.put("2", List.of(new GraphModel.Edge("2", "3", 1)));
        adj.put("3", List.of(new GraphModel.Edge("3", "2", 1)));

        GraphModel graph = new GraphModel(adj);
        BasicMetrics metrics = new BasicMetrics();

        SCCFinder sccFinder = new SCCFinder(graph, metrics);
        List<List<String>> sccs = sccFinder.findSCCs();


        assertEquals(3, sccs.size(), "Should detect 3 SCCs (0)(1)(2,3)");

        boolean foundCycle = sccs.stream().anyMatch(comp ->
                comp.contains("2") && comp.contains("3")
        );
        assertTrue(foundCycle, "There should be a cycle SCC with (2,3)");
    }
}
