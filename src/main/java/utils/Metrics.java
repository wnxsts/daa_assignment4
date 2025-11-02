package utils;
public interface Metrics {
    int getDfsVisits(); void addDfsVisit();
    int getQueueOps();  void addQueueOp();
    int getRelaxations(); void addRelaxation();
}