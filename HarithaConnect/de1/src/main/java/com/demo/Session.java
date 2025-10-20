package com.demo;

public class Session {

    private static Object currentUser; // can be LocalUser, PanchayatOfficial, or KudumbasreeWorker

    // ---- SET USER ----
    public static void setCurrentUser(Object user) {
        currentUser = user;
    }

    // ---- GET USER OBJECT ----
    public static Object getCurrentUser() {
        return currentUser;
    }

    // ---- GET USERNAME / ID ----
    public static String getCurrentUsername() {
        if (currentUser == null) return null;

        if (currentUser instanceof LocalUser) {
            return ((LocalUser) currentUser).getUsername();
        } else if (currentUser instanceof PanchayatOfficial) {
            return ((PanchayatOfficial) currentUser).getPanchayatId();
        } else if (currentUser instanceof KudumbasreeWorker) {
            return ((KudumbasreeWorker) currentUser).getWorkerId();
        }

        return null;
    }

    // ---- GET USER TYPE AS STRING ----
    public static String getCurrentUserType() {
        if (currentUser == null) return null;

        if (currentUser instanceof LocalUser) return "LocalUser";
        if (currentUser instanceof PanchayatOfficial) return "PanchayatOfficial";
        if (currentUser instanceof KudumbasreeWorker) return "KudumbasreeWorker";

        return "Unknown";
    }

    // ---- CLEAR SESSION ----
    public static void clear() {
        currentUser = null;
    }

    // ---- CONVENIENCE CHECKS ----
    public static boolean isLocalUser() {
        return currentUser instanceof LocalUser;
    }

    public static boolean isPanchayatUser() {
        return currentUser instanceof PanchayatOfficial;
    }

    public static boolean isKudumbasreeUser() {
        return currentUser instanceof KudumbasreeWorker;
    }
}
