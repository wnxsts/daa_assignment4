package graph.scc;

import java.util.List;

public class SCCResult {
    private final int count;
    private final int[] compId;
    private final List<List<Integer>> components;

    public SCCResult(int count, int[] compId, List<List<Integer>> components) {
        this.count = count;
        this.compId = compId;
        this.components = components;
    }

    public int count() { return count; }
    public int[] compId() { return compId; }

    public List<List<Integer>> components() { return components; }
}