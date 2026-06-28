package com.attendance;
import android.content.Context;
import android.content.SharedPreferences;
public class Prefs {
    private static final String FILE = "attendance_prefs";
    public static void setGoal(Context c, int g) { sp(c).edit().putInt("goal", g).apply(); }
    public static int getGoal(Context c) { return sp(c).getInt("goal", 75); }
    public static void setActiveProfile(Context c, int id) { sp(c).edit().putInt("active_profile", id).apply(); }
    public static int getActiveProfile(Context c) { return sp(c).getInt("active_profile", -1); }
    public static void setMode(Context c, String mode) { sp(c).edit().putString("mode", mode).apply(); }
    public static String getMode(Context c) { return sp(c).getString("mode", "student"); }
    private static SharedPreferences sp(Context c) { return c.getSharedPreferences(FILE, Context.MODE_PRIVATE); }
}
