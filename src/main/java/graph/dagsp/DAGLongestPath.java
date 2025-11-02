package graph.dagsp;

import core.DirectedGraph;
import utils.MetricsImpl;

import java.util.Arrays;
import java.util.List;

public class DAGLongestPath {
    private static final double NEG = -1e100;

    public static PathResult run(DirectedGraph dag, List<Integer> topo, int src, MetricsImpl metrics) {
        int n = dag.n();
        double[] dist = new double[n];
        int[] prev = new int[n];
        Arrays.fill(dist, NEG);
        Arrays.fill(prev, -1);
        dist[src] = 0;

        PathResult r = new PathResult(dist, prev, metrics);

        for (int u : topo) {
            if (dist[u] <= NEG / 2) continue;
            for (var e : dag.out(u)) {
                double nd = dist[u] + e.w;
                if (nd > dist[e.v]) {
                    dist[e.v] = nd;
                    prev[e.v] = u;
                    r.metrics().addRelaxation();
                }
            }
        }
        return r;
    }
}