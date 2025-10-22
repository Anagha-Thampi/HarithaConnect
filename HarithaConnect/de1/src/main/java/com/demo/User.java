package com.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class User {
    protected String username;
    protected String password;
    public User() {
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public abstract String getDataFileName(); // implemented by subclasses

    public boolean login(String enteredUsername, String enteredPassword) {
        String csvPath = "src/main/resources/com/demo/data/" + getDataFileName();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(csvPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Clean up any weird characters or extra line endings
                line = line.replaceAll("[\\r\\n]+", "").replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String csvUsername = parts[0].trim();
                    String csvPassword = parts[1].trim();

                    if (Objects.equals(csvUsername, enteredUsername.trim()) &&
                            Objects.equals(csvPassword, enteredPassword.trim())) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading login file: " + e.getMessage());
        }
        return false;
    }
}
