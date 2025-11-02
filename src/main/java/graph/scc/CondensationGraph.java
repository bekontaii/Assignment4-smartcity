package graph.scc;

import java.util.List;
import java.util.Map;


public class CondensationGraph {
    public final Map<Integer, List<Integer>> condensationDag;
    public final Map<String, Integer> compOfNode;

    public CondensationGraph(Map<Integer, List<Integer>> condensationDag,
                             Map<String, Integer> compOfNode) {
        this.condensationDag = condensationDag;
        this.compOfNode = compOfNode;
    }
}
