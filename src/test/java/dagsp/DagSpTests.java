package graph.dagsp;
import core.DirectedGraph;
import graph.dagsp.DAGLongestPath;
import graph.dagsp.DAGShortestPath;
import graph.dagsp.PathResult;
import graph.topo.KahnTopologicalSort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DagSpTests {

    @Test
    void shortest_and_longest_on_small_dag() {
        DirectedGraph g = new DirectedGraph(6);
        // 0→1(1), 0→2(2), 1→3(2), 2→3(1), 3→4(3), 3→5(1)
        g.addEdge(0,1,1); g.addEdge(0,2,2);
        g.addEdge(1,3,2); g.addEdge(2,3,1);
        g.addEdge(3,4,3); g.addEdge(3,5,1);

        var topo = KahnTopologicalSort.run(g).order();
        int src = 0;

        PathResult sp = DAGShortestPath.run(g, topo, src, new utils.MetricsImpl());
        PathResult lp = DAGLongestPath.run(g, topo, src, new utils.MetricsImpl());

        assertEquals(4.0, sp.dist()[5], 1e-9);
        assertEquals(6.0, lp.dist()[4], 1e-9);

        assertTrue(sp.metrics().getRelaxations() > 0);
        assertTrue(lp.metrics().getRelaxations() > 0);
    }
}