package graph.tests;

import graph.topo.TopologicalSorter;
import metrics.BasicMetrics;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TopologicalSorterTest {

    @Test
    public void testSimpleDAGOrder() {

        Map<Integer, List<Integer>> dag = new HashMap<>();
        dag.put(0, List.of(1));
        dag.put(1, List.of(2));
        dag.put(2, List.of(3));
        dag.put(3, List.of());

        BasicMetrics m = new BasicMetrics();
        TopologicalSorter sorter = new TopologicalSorter(dag, m);

        List<Integer> order = sorter.topoOrder();


        assertEquals(List.of(0,1,2,3), order);
    }

    @Test
    public void testBranchingDAG() {

        Map<Integer, List<Integer>> dag = new HashMap<>();
        dag.put(0, List.of(1,2));
        dag.put(1, List.of(3));
        dag.put(2, List.of(3));
        dag.put(3, List.of());

        BasicMetrics m = new BasicMetrics();
        TopologicalSorter sorter = new TopologicalSorter(dag, m);

        List<Integer> order = sorter.topoOrder();

        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertTrue(order.indexOf(1) < order.indexOf(3) || order.indexOf(2) < order.indexOf(3));
    }
}
