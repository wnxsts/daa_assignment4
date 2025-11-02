package core;

import java.util.*;

public class DirectedGraph {
    private final int n;
    private int m=0;
    private final List<List<Edge>> out;

    public DirectedGraph(int n){
        this.n=n;
        out=new ArrayList<>(n);
        for(int i=0;i<n;i++) out.add(new ArrayList<>());
    }

    public void addEdge(int u,int v,double w){ out.get(u).add(new Edge(u,v,w)); m++; }
    public List<Edge> out(int u){ return out.get(u); }
    public int n(){ return n; }
    public int m(){ return m; }
}