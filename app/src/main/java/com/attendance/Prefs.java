package com.attendance;
import android.content.Context;
import android.content.SharedPreferences;
public class Prefs {
    private static final String FILE = "attendance_prefs";
    private static final String KEY_GOAL = "attendance_goal";
    public static void setGoal(Context ctx, int goal) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putInt(KEY_GOAL, goal).apply();
    }
    public static int getGoal(Context ctx) {
        return ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt(KEY_GOAL, 75);
    }
}
