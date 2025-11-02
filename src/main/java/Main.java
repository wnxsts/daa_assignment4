package main;

import com.google.gson.JsonObject;
import core.DirectedGraph;
import graph.dagsp.DAGLongestPath;
import graph.dagsp.DAGShortestPath;
import graph.dagsp.PathResult;
import graph.scc.CondensationGraph;
import graph.scc.SCCResult;
import graph.scc.TarjanSCC;
import graph.topo.KahnTopologicalSort;
import graph.topo.TopologicalSortResult;
import utils.JsonReader;
import utils.JsonWriter;
import utils.MetricsImpl;
import utils.Timer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class Main {

    private static final DecimalFormat DF_TIME;
    private static final DecimalFormat DF_PATH;
    static {
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setDecimalSeparator('.');
        DF_TIME = new DecimalFormat("0.000", s);
        DF_PATH = new DecimalFormat("0.00",  s);
    }

    private static String densityByFile(String file){
        if (file == null) return "mixed";
        String f = file.toLowerCase();
        if (f.contains("dense"))  return "dense";
        if (f.contains("sparse")) return "sparse";
        return "mixed";
    }

    private static String variantByScc(SCCResult scc){
        int cyclic = 0;
        for (var comp : scc.components()) if (comp.size() > 1) cyclic++;
        if (cyclic == 0) return "pure_dag";
        if (cyclic == 1) return "one_cycle";
        if (cyclic == 2) return "two_cycles";
        return (scc.components().size() > cyclic) ? "mixed" : "many_sccs";
    }

    private static int pickSinkForSP(DirectedGraph dag, PathResult probe) {
        final double INF = 1e90;
        int n = dag.n(), sink = -1;
        double best = -1;
        for (int v = 0; v < n; v++) {
            boolean reachable = probe.dist()[v] < INF/2;
            if (reachable && dag.out(v).isEmpty() && probe.dist()[v] > best) {
                best = probe.dist()[v]; sink = v;
            }
        }
        if (sink < 0) {
            for (int v = 0; v < n; v++) {
                boolean reachable = probe.dist()[v] < INF/2;
                if (reachable && probe.dist()[v] > best) {
                    best = probe.dist()[v]; sink = v;
                }
            }
        }
        return sink >= 0 ? sink : 0;
    }

    private static int pickSinkForLP(DirectedGraph dag, PathResult probe) {
        final double NEG = -1e90;
        int n = dag.n(), sink = -1;
        double best = NEG;
        for (int v = 0; v < n; v++) {
            boolean reachable = probe.dist()[v] > NEG/2;
            if (reachable && dag.out(v).isEmpty() && probe.dist()[v] > best) {
                best = probe.dist()[v]; sink = v;
            }
        }
        if (sink < 0) {
            for (int v = 0; v < n; v++) {
                boolean reachable = probe.dist()[v] > NEG/2;
                if (reachable && probe.dist()[v] > best) {
                    best = probe.dist()[v]; sink = v;
                }
            }
        }
        return sink >= 0 ? sink : 0;
    }

    public static void main(String[] args) throws Exception {
        Path dataDir = Path.of("data");

        Path sccCsv  = dataDir.resolve("output_scc.csv");
        Path topoCsv = dataDir.resolve("output_topo.csv");
        Path spCsv   = dataDir.resolve("output_shortest.csv");
        Path lpCsv   = dataDir.resolve("output_longest.csv");

        // начинаем “с чистого листа”
        Files.deleteIfExists(sccCsv);
        Files.deleteIfExists(topoCsv);
        Files.deleteIfExists(spCsv);
        Files.deleteIfExists(lpCsv);

        // заголовки
        JsonWriter.appendCsv(sccCsv,
                "graph_id,vertices,edges,density,variant,scc_count,dfs_visits,time_ms", null);
        JsonWriter.appendCsv(topoCsv,
                "graph_id,vertices,edges,density,variant,topo_len,queue_ops,time_ms", null);
        JsonWriter.appendCsv(spCsv,
                "graph_id,vertices,edges,density,variant,src,sink,total_ops,relaxations,time_ms,path_length", null);
        JsonWriter.appendCsv(lpCsv,
                "graph_id,vertices,edges,density,variant,src,sink,total_ops,relaxations,time_ms,path_length", null);

        int fallbackId = 0;

        for (JsonObject item : JsonReader.readInputs(dataDir)) {
            int id = item.has("id") ? item.get("id").getAsInt() : ++fallbackId;
            String file = item.has("file") ? item.get("file").getAsString() : "unknown.json";
            String density = densityByFile(file);

            DirectedGraph g = JsonReader.graphFrom(item);

            Timer tScc = new Timer(); tScc.start();
            TarjanSCC tarjan = new TarjanSCC(g);
            SCCResult scc = tarjan.run();
            double msScc = tScc.stop();
            String variant = item.has("variant") ? item.get("variant").getAsString() : variantByScc(scc);

            JsonWriter.appendCsv(sccCsv, null, String.join(",",
                    String.valueOf(id),
                    String.valueOf(g.n()),
                    String.valueOf(g.m()),
                    density,
                    variant,
                    String.valueOf(scc.count()),
                    String.valueOf(tarjan.metrics().getDfsVisits()),
                    DF_TIME.format(msScc)
            ));

            // DAG конденсации
            DirectedGraph dag = CondensationGraph.build(g, scc.compId(), scc.count());

            // --- Topo ---
            Timer tTopo = new Timer(); tTopo.start();
            TopologicalSortResult topo = KahnTopologicalSort.run(dag);
            double msTopo = tTopo.stop();

            JsonWriter.appendCsv(topoCsv, null, String.join(",",
                    String.valueOf(id),
                    String.valueOf(g.n()),
                    String.valueOf(g.m()),
                    density,
                    variant,
                    String.valueOf(topo.order().size()),
                    String.valueOf(topo.metrics().getQueueOps()),
                    DF_TIME.format(msTopo)
            ));

            List<Integer> order = topo.order();

            int src = JsonReader.anySource(dag);
            if (src < 0) src = 0;

            PathResult spProbe = DAGShortestPath.run(dag, order, src, new MetricsImpl());
            PathResult lpProbe = DAGLongestPath.run(dag, order, src, new MetricsImpl());
            int sinkSp = pickSinkForSP(dag, spProbe);
            int sinkLp = pickSinkForLP(dag, lpProbe);

            MetricsImpl spMx = new MetricsImpl();
            Timer tSp = new Timer(); tSp.start();
            PathResult sp = DAGShortestPath.run(dag, order, src, spMx);
            double msSp = tSp.stop();
            int totalSpOps = tarjan.metrics().getDfsVisits() + topo.metrics().getQueueOps() + spMx.getRelaxations();

            JsonWriter.appendCsv(spCsv, null, String.join(",",
                    String.valueOf(id),
                    String.valueOf(g.n()),
                    String.valueOf(g.m()),
                    density,
                    variant,
                    String.valueOf(src),
                    String.valueOf(sinkSp),
                    String.valueOf(totalSpOps),
                    String.valueOf(spMx.getRelaxations()),
                    DF_TIME.format(msSp),
                    DF_PATH.format(sp.dist()[sinkSp])
            ));

            // --- Longest ---
            MetricsImpl lpMx = new MetricsImpl();
            Timer tLp = new Timer(); tLp.start();
            PathResult lp = DAGLongestPath.run(dag, order, src, lpMx);
            double msLp = tLp.stop();
            int totalLpOps = tarjan.metrics().getDfsVisits() + topo.metrics().getQueueOps() + lpMx.getRelaxations();

            JsonWriter.appendCsv(lpCsv, null, String.join(",",
                    String.valueOf(id),
                    String.valueOf(g.n()),
                    String.valueOf(g.m()),
                    density,
                    variant,
                    String.valueOf(src),
                    String.valueOf(sinkLp),
                    String.valueOf(totalLpOps),
                    String.valueOf(lpMx.getRelaxations()),
                    DF_TIME.format(msLp),
                    DF_PATH.format(lp.dist()[sinkLp])
            ));
        }

        System.out.println("output_scc.csv, output_topo.csv, output_shortest.csv, output_longest.csv");
    }
}