package graph.scc;

import core.*;
import utils.*;
import java.util.*;

public class TarjanSCC {
    private final DirectedGraph g;
    private final int[] disc, low, compId;
    private final boolean[] onst;
    private final Deque<Integer> st = new ArrayDeque<>();
    private int t = 0, comp = 0;
    public final MetricsImpl mx = new MetricsImpl();
    private final List<List<Integer>> comps = new ArrayList<>();

    public TarjanSCC(DirectedGraph g) {
        this.g = g;
        int n = g.n();
        disc = new int[n]; Arrays.fill(disc, -1);
        low  = new int[n];
        compId = new int[n]; Arrays.fill(compId, -1);
        onst = new boolean[n];
    }

    public SCCResult run() {
        for (int v = 0; v < g.n(); v++) if (disc[v] == -1) dfs(v);
        return new SCCResult(comp, compId, comps);
    }

    public MetricsImpl metrics() { return mx; }

    private void dfs(int u) {
        mx.addDfsVisit();
        disc[u] = low[u] = t++;
        st.push(u);
        onst[u] = true;

        for (var e : g.out(u)) {
            int v = e.v;
            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onst[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> c = new ArrayList<>();
            while (true) {
                int v = st.pop();
                onst[v] = false;
                compId[v] = comp;
                c.add(v);
                if (v == u) break;
            }
            comps.add(c);
            comp++;
        }
    }
}