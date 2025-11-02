package org.example;

import metrics.BasicMetrics;
import util.GraphLoader;
import util.GraphModel;
import util.MetricsExporter;
import graph.scc.SCCFinder;
import graph.topo.TopologicalSorter;
import graph.dagsp.DagShortestPath;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;


public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("=== Smart City / Smart Campus Scheduling ===");
        System.out.println("Scanning /data/ folder...\n");

        Path dataDir = Path.of("data");
        if (!Files.exists(dataDir)) {
            System.err.println("❌ Folder 'data/' not found. Please generate graphs first.");
            return;
        }

        List<Path> jsonFiles = Files.list(dataDir)
                .filter(f -> f.toString().endsWith(".json"))
                .sorted()
                .toList();

        if (jsonFiles.isEmpty()) {
            System.err.println("❌ No .json datasets found in /data/");
            return;
        }


        System.out.printf("%-12s | %-5s | %-5s | %-5s | %-6s | %-6s | %-6s | %-6s | %-6s | %-6s | %-8s | %-20s%n",
                "Dataset", "V", "E", "SCC", "dfsV", "edges", "push", "pop", "relax", "nsTime", "CritLen", "CritPath");
        System.out.println("-".repeat(120));


        List<String[]> csvRows = new ArrayList<>();

        for (Path file : jsonFiles) {
            String[] row = runDataset(file);
            if (row != null) csvRows.add(row);
        }

        MetricsExporter.exportCSV(csvRows, "data/metrics.csv");

        System.out.println("\n✅ Metrics exported to data/metrics.csv");
        System.out.println("=== END OF EXECUTION ===");
    }

    private static String[] runDataset(Path file) {
        try {
            GraphModel g = GraphLoader.loadFromJson(file);


            BasicMetrics m1 = new BasicMetrics();
            SCCFinder sccFinder = new SCCFinder(g, m1);
            List<List<String>> sccs = sccFinder.findSCCs();
            Map<Integer, List<Integer>> dag = sccFinder.buildCondensation(sccs);


            BasicMetrics m2 = new BasicMetrics();
            TopologicalSorter topo = new TopologicalSorter(dag, m2);
            List<Integer> order = topo.topoOrder();

            int vCount = g.getAllNodes().size();
            int eCount = g.getAdjacency().values().stream().mapToInt(List::size).sum();
            int sccCount = sccs.size();


            String critPathDisplay;
            long critLen = 0;
            long relaxCount = 0;
            long nsTime = 0;

            if (sccCount == vCount) {
                BasicMetrics m3 = new BasicMetrics();
                DagShortestPath dsp = new DagShortestPath(g, m3);
                Map<String, Integer> dist = dsp.shortestFrom("0");
                List<String> crit = dsp.criticalPath();

                relaxCount = m3.getCounter("relaxEdges");
                nsTime = m3.getElapsedNs();
                critLen = getPathLength(dist, crit);
                critPathDisplay = crit.toString();
            } else {
                relaxCount = 0;
                nsTime = 0;
                critLen = 0;
                critPathDisplay = "Cyclic graph — skipped";
            }


            System.out.printf("%-12s | %-5d | %-5d | %-5d | %-6d | %-6d | %-6d | %-6d | %-6d | %-6d | %-8d | %-20s%n",
                    file.getFileName().toString(),
                    vCount, eCount, sccCount,
                    m1.getCounter("dfsVisits"), m1.getCounter("edgesSeen"),
                    m2.getCounter("pushOps"), m2.getCounter("popOps"),
                    relaxCount, nsTime, critLen, critPathDisplay);


            return new String[]{
                    file.getFileName().toString(),
                    String.valueOf(vCount),
                    String.valueOf(eCount),
                    String.valueOf(sccCount),
                    String.valueOf(m1.getCounter("dfsVisits")),
                    String.valueOf(m1.getCounter("edgesSeen")),
                    String.valueOf(m2.getCounter("pushOps")),
                    String.valueOf(m2.getCounter("popOps")),
                    String.valueOf(relaxCount),
                    String.valueOf(nsTime),
                    String.valueOf(critLen),
                    critPathDisplay.replace(",", ";")
            };

        } catch (Exception e) {
            System.err.println("⚠ Error processing " + file.getFileName() + ": " + e.getMessage());
            return null;
        }
    }


    private static int getPathLength(Map<String, Integer> dist, List<String> path) {
        if (path.isEmpty()) return 0;
        int len = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            if (dist.get(path.get(i + 1)) < Integer.MAX_VALUE)
                len = dist.get(path.get(i + 1));
        }
        return len;
    }
}
