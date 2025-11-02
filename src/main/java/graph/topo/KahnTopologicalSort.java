package graph.topo;

import core.DirectedGraph;

import java.util.ArrayDeque;
import java.util.Deque;

public class KahnTopologicalSort {
    public static TopologicalSortResult run(DirectedGraph dag) {
        int n = dag.n();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) for (var e : dag.out(u)) indeg[e.v]++;

        Deque<Integer> q = new ArrayDeque<>();
        TopologicalSortResult res = new TopologicalSortResult();

        for (int i = 0; i < n; i++) {
            if (indeg[i] == 0) {
                q.add(i);
                res.metrics().addQueueOp();
            }
        }

        while (!q.isEmpty()) {
            int u = q.remove();
            res.order().add(u);
            for (var e : dag.out(u)) {
                if (--indeg[e.v] == 0) {
                    q.add(e.v);
                    res.metrics().addQueueOp();
                }
            }
        }
        return res;
    }
}