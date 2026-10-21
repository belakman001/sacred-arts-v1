package com.sacredarts.client;

/**
 * Client-side mirror of the server's sacred artist data, kept fresh by
 * SyncDataPacket (sent on login, on every change, and every 2 seconds).
 * Screens and the HUD read from here — never touch the capability directly.
 */
public class ClientData {
    public static boolean enrolled = false;
    public static String pathId = "black_flame";
    public static String stageId = "foundation";
    public static double madra = 0;
    public static double maxMadra = 20;
    public static int mastery = 0;
    public static int kills = 0;
    public static double cycledTotal = 0;
    public static String ironBodyId = "none";
    public static boolean goldsign = false;

    public static boolean cycling = false;

    public static void update(boolean enrolled, String pathId, String stageId,
                              double madra, double maxMadra, int mastery, int kills,
                              double cycledTotal, String ironBodyId, boolean goldsign,
                              boolean cycling) {
        ClientData.enrolled = enrolled;
        ClientData.pathId = pathId;
        ClientData.stageId = stageId;
        ClientData.madra = madra;
        ClientData.maxMadra = maxMadra;
        ClientData.mastery = mastery;
        ClientData.kills = kills;
        ClientData.cycledTotal = cycledTotal;
        ClientData.ironBodyId = ironBodyId;
        ClientData.goldsign = goldsign;
        ClientData.cycling = cycling;
    }
}
