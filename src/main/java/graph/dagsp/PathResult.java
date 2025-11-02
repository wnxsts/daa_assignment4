package graph.dagsp;

import utils.MetricsImpl;

public class PathResult {
    private final double[] dist;
    private final int[] prev;
    private final MetricsImpl mx;

    public PathResult(double[] dist, int[] prev, MetricsImpl mx) {
        this.dist = dist;
        this.prev = prev;
        this.mx = mx;
    }

    public double[] dist() { return dist; }
    public int[] prev() { return prev; }
    public MetricsImpl metrics() { return mx; }
}