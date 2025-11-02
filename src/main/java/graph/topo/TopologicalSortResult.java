package graph.topo;

import utils.MetricsImpl;

import java.util.ArrayList;
import java.util.List;

public class TopologicalSortResult {
    private final List<Integer> order;
    private final MetricsImpl mx;

    public TopologicalSortResult() {
        this.order = new ArrayList<>();
        this.mx = new MetricsImpl();
    }

    public TopologicalSortResult(List<Integer> order, MetricsImpl mx) {
        this.order = order;
        this.mx = mx;
    }

    public List<Integer> order() { return order; }
    public MetricsImpl metrics() { return mx; }
}