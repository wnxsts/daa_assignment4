package graph.scc;
import core.DirectedGraph;
import graph.scc.CondensationGraph;
import graph.scc.SCCResult;
import graph.scc.TarjanSCC;
import graph.topo.KahnTopologicalSort;
import graph.topo.TopologicalSortResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KosarajuIntegrationMetricsTest {

    @Test
    void end_to_end_scc_condensation_topo_metrics_nonzero() {
        DirectedGraph g = new DirectedGraph(6);
        // две КСС: (0,1) и (2,3,4); 4→5
        g.addEdge(0,1,1); g.addEdge(1,0,1);
        g.addEdge(2,3,1); g.addEdge(3,4,1); g.addEdge(4,2,1);
        g.addEdge(4,5,1);

        TarjanSCC t = new TarjanSCC(g);
        SCCResult s = t.run();
        assertEquals(3, s.count());

        DirectedGraph dag = CondensationGraph.build(g, s.compId(), s.count());
        TopologicalSortResult topo = KahnTopologicalSort.run(dag);

        assertEquals(s.count(), topo.order().size(), "Топопорядок по DAG конденсации");
        assertTrue(t.metrics().getDfsVisits() > 0);
        assertTrue(topo.metrics().getQueueOps() > 0);
    }
}