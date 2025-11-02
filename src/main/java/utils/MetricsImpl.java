package utils;
public class MetricsImpl implements Metrics {
    private int dfs, q, relax;
    public int getDfsVisits(){ return dfs; }     public void addDfsVisit(){ dfs++; }
    public int getQueueOps(){ return q; }        public void addQueueOp(){ q++; }
    public int getRelaxations(){ return relax; } public void addRelaxation(){ relax++; }
}