package graph.scc;

import core.*;
import java.util.*;

public class CondensationGraph {
    public static DirectedGraph build(DirectedGraph g, int[] compId, int compCnt){
        DirectedGraph dag = new DirectedGraph(compCnt);
        Set<Long> seen = new HashSet<>();
        for(int u=0; u<g.n(); u++){
            int cu=compId[u];
            for(var e: g.out(u)){
                int cv=compId[e.v];
                if(cu!=cv){
                    long key=(((long)cu)<<32) | (cv & 0xffffffffL);
                    if(seen.add(key)) dag.addEdge(cu, cv, e.w);
                }
            }
        }
        return dag;
    }
}