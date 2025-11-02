package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class JsonWriter {

    private static void ensureParent(Path p) throws IOException {
        if (p.getParent() != null) {
            Files.createDirectories(p.getParent());
        }
    }

    public static void appendCsv(Path p, String headerIfNew, String line) throws IOException {
        ensureParent(p);
        if (!Files.exists(p) && headerIfNew != null) {
            Files.writeString(p, headerIfNew + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
        if (line != null && !line.isEmpty()) {
            Files.writeString(p, line + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    public static void appendCsv(Path p, String line) throws IOException {
        appendCsv(p, null, line);
    }

    public static void writeJson(Path p, JsonObject obj) throws IOException {
        ensureParent(p);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String s = gson.toJson(obj);
        Files.writeString(p, s,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }


}