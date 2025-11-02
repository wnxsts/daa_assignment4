package graph.topo;
import core.DirectedGraph;
import graph.topo.KahnTopologicalSort;
import graph.topo.TopologicalSortResult;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TopoTests {

    @Test
    void kahn_returns_valid_topological_order() {
        DirectedGraph g = new DirectedGraph(5);
        // 0→1, 0→2, 1→3, 2→3, 3→4
        g.addEdge(0,1,1); g.addEdge(0,2,1);
        g.addEdge(1,3,1); g.addEdge(2,3,1);
        g.addEdge(3,4,1);

        TopologicalSortResult res = KahnTopologicalSort.run(g);
        assertEquals(5, res.order().size());

        var pos = new int[5];
        for (int i=0;i<res.order().size();i++) pos[res.order().get(i)] = i;
        for (int u=0; u<5; u++){
            for (var e: g.out(u)){
                assertTrue(pos[u] < pos[e.v], "u должно идти раньше v");
            }
        }

        assertTrue(res.metrics().getQueueOps() > 0);
    }

    @Test
    void kahn_handles_multiple_sources() {
        DirectedGraph g = new DirectedGraph(4);
        g.addEdge(0,2,1); g.addEdge(1,2,1); g.addEdge(2,3,1);

        TopologicalSortResult r = KahnTopologicalSort.run(g);
        Set<Integer> firstTwo = new HashSet<>(r.order().subList(0,2));
        assertTrue(firstTwo.contains(0));
        assertTrue(firstTwo.contains(1));
    }
}