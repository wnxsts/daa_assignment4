package graph.dagsp;

import java.util.*;

public class CriticalPathResult {
    public final double length; public final java.util.List<Integer> path;
    public CriticalPathResult(double L, java.util.List<Integer> P){ length=L; path=P; }
}