package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MetricsExporter {

    public static void exportCSV(List<String[]> rows, String fileName) {
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write("Dataset,V,E,SCC,dfsV,edges,push,pop,relax,nsTime,CritLen,CritPath\n");
            for (String[] r : rows) {
                fw.write(String.join(",", r));
                fw.write("\n");
            }
            System.out.println("✅ Metrics exported to " + fileName);
        } catch (IOException e) {
            throw new RuntimeException("Error writing metrics CSV: " + e.getMessage());
        }
    }
}
