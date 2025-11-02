package utils;

import com.google.gson.*;
import core.DirectedGraph;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class JsonReader {

    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    // читаем оба входных файла и ДОПИСЫВАЕМ "file": "<имя>" в каждый граф
    public static List<JsonObject> readInputs(Path dataDir) throws IOException {
        List<JsonObject> out = new ArrayList<>();
        for (String name : new String[]{"input_sparse.json", "input_dense.json"}) {
            Path p = dataDir.resolve(name);
            if (!Files.exists(p)) continue;

            JsonObject root = JsonParser.parseString(Files.readString(p)).getAsJsonObject();
            JsonArray arr = root.getAsJsonArray("graphs");
            if (arr == null) continue;

            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                if (!obj.has("file")) obj.addProperty("file", name); // чтобы понять dense/sparse
                out.add(obj);
            }
        }
        return out;
    }

    public static DirectedGraph graphFrom(JsonObject item) {
        JsonObject obj = item;
        if (item.has("g") && item.get("g").isJsonObject()) obj = item.getAsJsonObject("g");
        if (item.has("graph") && item.get("graph").isJsonObject()) obj = item.getAsJsonObject("graph");

        int n = obj.get("n").getAsInt();
        DirectedGraph g = new DirectedGraph(n);
        JsonArray edges = obj.getAsJsonArray("edges");
        for (JsonElement e : edges) {
            JsonObject ee = e.getAsJsonObject();
            int u = ee.get("u").getAsInt();
            int v = ee.get("v").getAsInt();
            double w = ee.has("w") ? ee.get("w").getAsDouble() : 1.0;
            g.addEdge(u, v, w);
        }
        return g;
    }

    public static JsonObject buildOutputJson(int id,
                                             DirectedGraph g,
                                             Object scc,
                                             Object topo,
                                             Object sp,
                                             Object lp,
                                             double ms) {
        JsonObject o = new JsonObject();
        o.addProperty("id", id);
        o.addProperty("n", g.n());
        o.addProperty("m", g.m());
        o.addProperty("time_ms", ms);
        return o;
    }

    public static JsonObject wrapResults(JsonArray arr) {
        JsonObject root = new JsonObject();
        root.add("results", arr);
        return root;
    }

    public static int anySource(DirectedGraph g) {
        int n = g.n();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) for (var e : g.out(u)) indeg[e.v]++;
        for (int i = 0; i < n; i++) if (indeg[i] == 0) return i;
        return -1;
    }

    public static int anySink(DirectedGraph g) {
        for (int i = 0; i < g.n(); i++) if (g.out(i).isEmpty()) return i;
        return -1;
    }
}