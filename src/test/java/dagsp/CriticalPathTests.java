package graph.dagsp;
import core.DirectedGraph;
import graph.dagsp.DAGLongestPath;
import graph.dagsp.PathResult;
import graph.topo.KahnTopologicalSort;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CriticalPathTests {

    private static List<Integer> restorePath(int src, int sink, PathResult r){
        List<Integer> path = new ArrayList<>();
        for (int v = sink; v != -1; v = r.prev()[v]) {
            path.add(0, v);
            if (v == src) break;
        }
        return path;
    }

    @Test
    void critical_path_equals_longest_path_in_DAG() {
        DirectedGraph g = new DirectedGraph(5);
        g.addEdge(0,1,1); g.addEdge(1,3,2);
        g.addEdge(0,2,2); g.addEdge(2,4,2);

        var topo = KahnTopologicalSort.run(g).order();
        int src = 0;

        PathResult lp = DAGLongestPath.run(g, topo, src, new utils.MetricsImpl());
        int sink = (lp.dist()[3] >= lp.dist()[4]) ? 3 : 4;

        List<Integer> critical = restorePath(src, sink, lp);
        assertEquals(List.of(0,2,4), critical);
        assertEquals(4.0, lp.dist()[sink], 1e-9);
    }
}