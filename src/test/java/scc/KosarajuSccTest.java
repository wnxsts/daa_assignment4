package graph.scc;
import core.DirectedGraph;
import graph.scc.SCCResult;
import graph.scc.TarjanSCC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KosarajuSccTest {

    @Test
    void pureDag_hasN_SCCs_of_size1() {
        DirectedGraph g = new DirectedGraph(4);
        g.addEdge(0,1,1); g.addEdge(1,2,1); g.addEdge(2,3,1);

        TarjanSCC t = new TarjanSCC(g);
        SCCResult r = t.run();

        assertEquals(4, r.count(), "");
        r.components().forEach(c -> assertEquals(1, c.size()));
        assertEquals(4, t.metrics().getDfsVisits());
    }

    @Test
    void oneCycle_detects_one_nontrivial_SCC() {
        DirectedGraph g = new DirectedGraph(5);

        g.addEdge(0,1,1); g.addEdge(1,2,1); g.addEdge(2,0,1);
        g.addEdge(2,3,1); g.addEdge(3,4,1);

        TarjanSCC t = new TarjanSCC(g);
        SCCResult r = t.run();

        assertEquals(3, r.count(), "");
        assertTrue(r.components().stream().anyMatch(c -> c.size() == 3),
                "");
    }
}