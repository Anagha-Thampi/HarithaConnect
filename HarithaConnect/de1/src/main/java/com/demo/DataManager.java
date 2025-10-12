package com.demo;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Central CSV helper. Uses DATA_DIR + filename for read/write.
 */
public class DataManager {
    public static final String DATA_DIR = "src/main/resources/com/demo/data/";

    public static List<String[]> readCsv(String filename) {
        List<String[]> rows = new ArrayList<>();
        Path path = Paths.get(DATA_DIR, filename);
        if (!Files.exists(path)) return rows;
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // simple CSV split - cells should not contain commas in this project
                rows.add(line.split(",", -1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public static void appendCsv(String filename, String... values) {
        Path path = Paths.get(DATA_DIR, filename);
        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                bw.write(Arrays.stream(values)
                        .map(s -> s == null ? "" : s.replace("\n"," ").replace(",", " ")) // sanitize
                        .collect(Collectors.joining(",")));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void overwriteCsv(String filename, List<String[]> rows) {
        Path path = Paths.get(DATA_DIR, filename);
        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (String[] row : rows) {
                    bw.write(Arrays.stream(row).map(s -> s == null ? "" : s.replace("\n"," ").replace(",", " ")).collect(Collectors.joining(",")));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

