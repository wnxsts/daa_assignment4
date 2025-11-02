# DAA Assignment 4 – Graph Algorithms
# Optimization of a Smart City Graph Using SCC + DAG Algorithms

**Author:** Samatova Zhanel  
**Group:** SE-2419

---

## Table of Contents
- [Introduction](#introduction)
- [Theory](#theory)
  - [Topological Sort](#topological-sort)
  - [Strongly Connected Components (Kosaraju)](#strongly-connected-components-kosaraju)
  - [Single-Source Shortest Path in a DAG](#single-source-shortest-path-in-a-dag)
- [Datasets](#datasets)
- [Results](#results)
  - [SCC Results](#scc-results)
  - [Topological Sort Results](#topological-sort-results)
  - [DAG Shortest Path Results](#dag-shortest-path-results)
  - [Summary Results](#summary-results)
- [Analysis](#analysis)
  - [SCC Analysis](#scc-analysis)
  - [Topological Sort Analysis](#topological-sort-analysis)
  - [DAG-SP Analysis](#dag-sp-analysis)
  - [Comparison: Theory vs. Practice](#comparison-theory-vs-practice)
- [Conclusions](#conclusions)

---

## Introduction
I implemented three core graph algorithms:
1) **Kosaraju’s SCC** for detecting strongly connected components,
2) **Topological Sort** for DAGs (Kahn/DFS variants),
3) **Single-Source Shortest Path on DAG** (linear-time DP over topological order).

The code uses adjacency lists (ideal for sparse → moderately dense graphs) and tracks both **operation counters** (DFS visits, queue ops, relaxations) and **execution time**. Longest-path (critical path) in DAGs is also included, but the focus here is on shortest paths.

> Reference: Cormen et al., *Introduction to Algorithms* (3rd ed.), Chapter 22.

---

## Theory

### Topological Sort
Orders vertices so every edge \((u \to v)\) respects the order.  
- **Kahn’s algorithm**: compute indegrees, repeatedly pop 0-indegree nodes (queue).  
- **DFS variant**: push on finish; reverse postorder is a valid topo order.  
- **Complexity:** \(\Theta(V+E)\).

### Strongly Connected Components (Kosaraju)
Maximal sets where every vertex reaches every other.  
Kosaraju runs **two DFS passes**:
1) DFS on \(G\) to record finish times.
2) DFS on \(G^T\) (reversed) in decreasing finish order; each DFS tree = one SCC.  
- **Complexity:** \(\Theta(V+E)\).

### Single-Source Shortest Path in a DAG
For DAGs (even with negative edges), we can solve SSSP in linear time:  
1) Compute topological order.  
2) Initialize \(dist[s]=0\), others \(+\infty\).  
3) Traverse vertices in topo order and **relax** outgoing edges once.  
- **Complexity:** \(\Theta(V+E)\).

---

## Datasets
I evaluated 18 directed graphs spanning **sizes**, **densities** (sparse/dense), and **structure variants**:
- `pure_dag` – acyclic,
- `one_cycle`, `two_cycles` – with cycles,
- `multiple_sccs` – several components.

> CSVs are stored in `data/`:
> - `output_scc.csv`
> - `output_topo.csv`
> - `output_shortest.csv`
> - (optionally) `output_longest.csv`
> - `output_combined_out.json` (merged JSON if you export it)

---

## Results

Tables below show **samples** (first rows) for readability.  
 Full results are in the CSV files under `data/`.

### SCC Results
Source: `data/output_scc.csv`  
Columns: `graph_id, vertices, edges, density, variant, scc_count, dfs_visits, time_ms`

| graph_id | vertices | edges | density | variant      | scc_count | dfs_visits | time_ms |
|---------:|---------:|------:|:--------|:-------------|----------:|-----------:|--------:|
| 1 | 6  | 10  | sparse | one_cycle   | 4 | 6  | 0.815 |
| 2 | 8  | 15  | sparse | one_cycle   | 2 | 8  | 0.010 |
| 3 | 10 | 30  | sparse | one_cycle   | 1 | 10 | 0.012 |
| 4 | 12 | 26  | sparse | mixed       | 5 | 12 | 0.012 |
| 5 | 16 | 44  | sparse | one_cycle   | 2 | 16 | 0.015 |
| … | … | … | … | … | … | … | … |

---

### Topological Sort Results
Source: `data/output_topo.csv`  
Columns: `graph_id, vertices, edges, density, variant, topo_len, queue_ops, time_ms`

| graph_id | vertices | edges | density | variant      | topo_len | queue_ops | time_ms |
|---------:|---------:|------:|:--------|:-------------|---------:|----------:|--------:|
| 1 | 6  | 10  | sparse | one_cycle   | 4 | 4 | 0.295 |
| 2 | 8  | 15  | sparse | one_cycle   | 2 | 2 | 0.005 |
| 3 | 10 | 30  | sparse | one_cycle   | 1 | 1 | 0.004 |
| 4 | 12 | 26  | sparse | mixed       | 5 | 5 | 0.006 |
| 5 | 16 | 44  | sparse | one_cycle   | 2 | 2 | 0.008 |
| … | … | … | … | … | … | … | … |

---

### DAG Shortest Path Results
Source: `data/output_shortest.csv`  
Columns: `graph_id, vertices, edges, density, variant, src, sink, total_ops, relaxations, time_ms, path_length`

| graph_id | vertices | edges | density | variant      | src | sink | total_ops | relaxations | time_ms | path_length |
|---------:|---------:|------:|:--------|:-------------|----:|----:|----------:|------------:|--------:|------------:|
| 1 | 6  | 10  | sparse | one_cycle   | 3 | 0 | 13 | 3 | 0.003 | 6.30 |
| 2 | 8  | 15  | sparse | one_cycle   | 1 | 0 | 11 | 1 | 0.001 | 5.80 |
| 3 | 10 | 30  | sparse | one_cycle   | 0 | 0 | 11 | 0 | 0.001 | 0.00 |
| 4 | 12 | 26  | sparse | mixed       | 4 | 0 | 22 | 5 | 0.002 | 1.00 |
| 5 | 16 | 44  | sparse | one_cycle   | 1 | 0 | 19 | 1 | 0.001 | 5.60 |
| … | … | … | … | … | … | … | … | … | … | … |

> Note: For non-DAG inputs some rows can be 0 (the pipeline skips SSSP if condensation still indicates cycles).

---

### Summary Results
If you aggregate per-graph totals (SCC + Topo + Shortest), you’ll get a compact view like:

**Columns** (example):  
`graph_id, vertices, edges, density, variant, total_operations_count, total_execution_time_ms`

| graph_id | vertices | edges | density | variant      | total_operations_count | total_execution_time_ms |
|---------:|---------:|------:|:--------|:-------------|-----------------------:|------------------------:|
| 1 | 6  | 10  | sparse | one_cycle   | 59  | 50.400 |
| 2 | 8  | 15  | sparse | one_cycle   | 75  | 22.600 |
| 3 | 10 | 30  | sparse | one_cycle   | 114 | 26.700 |
| 4 | 12 | 26  | sparse | mixed       | 84  | 52.800 |
| 5 | 16 | 44  | sparse | one_cycle   | 108 | 24.600 |
| … | … | … | … | … | … | … |

> The full version of this table is the **`output.csv` / combined CSV** you generated, or a merge you produce from the three per-algorithm CSVs.

---

## Analysis

### SCC Analysis
- **What dominates:** two DFS passes (+ building \(G^T\)) → still linear.  
- **Structure effects:** more edges and larger components increase DFS work; dense graphs show higher time even with the same \(V\).  
- **Observation:** counts scale ≈ linearly with \(E\); multi-SCC graphs are correctly identified and later condensed to DAGs.

### Topological Sort Analysis
- **What dominates:** counting indegrees + queue operations (Kahn) or one DFS (DFS-based).  
- **Observation:** `queue_ops` is a useful proxy for edge processing; values grow with density.

### DAG-SP Analysis
- **What dominates:** one topological pass + relaxations (one per edge).  
- **Observation:** on pure DAGs, ops/time grow with \(E\); on cyclic inputs the pipeline should rely on **condensation → topo → DAG-SP** (or skip SSSP if still cyclic), which explains zero-rows in some datasets.

### Comparison: Theory vs. Practice
All three algorithms behave **\(\Theta(V+E)\)** as expected:
- SCC usually costs ≈ **2×DFS** work vs topo’s **1×**, so topo tends to be faster.  
- DAG-SP scales with edges relaxed; dense DAGs cost more but remain linear.  
- Cache/memory locality makes dense cases disproportionately slower in nanoseconds, though the asymptotics hold.

---

## Conclusions
- **Use SCC (Kosaraju)** to detect/segment cycles and prepare inputs for DAG processing.  
- **Use Topological Sort** for dependency scheduling in acyclic contexts.  
- **Use DAG-SSSP** for fast shortest paths (and the symmetric **longest/critical path** for scheduling).  
- **Practical tip:** prefer adjacency lists; for cyclic inputs, **condense → topo → paths**. Tracking **operations + time** makes performance trends transparent.

---

### Reproduce / File Map
- Raw inputs: `data/input_sparse.json`, `data/input_dense.json`  
- Outputs:
  - `data/output_scc.csv` — SCC metrics
  - `data/output_topo.csv` — Topo metrics
  - `data/output_shortest.csv` — DAG SSSP metrics
  - *(optional)* `data/output_longest.csv` — DAG critical path
  - `data/output.csv` — combined/summary (if generated)
  - `data/output_sparse.json`, `data/output_dense.json` — JSON exports (optional)
