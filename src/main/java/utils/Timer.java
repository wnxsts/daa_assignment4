package utils;
public class Timer {
    private long s;
    public void start(){ s=System.nanoTime(); }
    public double stop(){ return (System.nanoTime()-s)/1_000_000.0; }
}