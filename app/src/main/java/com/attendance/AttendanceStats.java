package com.attendance;
public class AttendanceStats {
    public int present;
    public int total;
    public int goal;
    public AttendanceStats(int present, int total, int goal) {
        this.present = present;
        this.total = total;
        this.goal = goal;
    }
    public float getPercent() {
        return total > 0 ? (present * 100f / total) : 0;
    }
    public int canMiss() {
        if (total == 0) return 0;
        int canMiss = 0;
        int t = total;
        int p = present;
        while (true) {
            t++;
            float newPct = p * 100f / t;
            if (newPct < goal) break;
            canMiss++;
            if (canMiss > 999) break;
        }
        return canMiss;
    }
    public int needToAttend() {
        if (getPercent() >= goal) return 0;
        int t = total;
        int p = present;
        int needed = 0;
        while (p * 100f / t < goal) {
            p++; t++; needed++;
            if (needed > 999) break;
        }
        return needed;
    }
    public float percentIfMissNext() {
        if (total == 0) return 0;
        return present * 100f / (total + 1);
    }
    public String getRiskLevel() {
        float pct = getPercent();
        if (pct >= goal) return "SAFE";
        if (pct >= goal - 5) return "WARNING";
        return "DANGER";
    }
    public String getStreakInfo(java.util.List<String> presentDates) {
        if (presentDates.isEmpty()) return "0";
        java.util.Collections.sort(presentDates, java.util.Collections.reverseOrder());
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.util.Calendar cal = java.util.Calendar.getInstance();
            String today = sdf.format(cal.getTime());
            String yesterday = sdf.format(new java.util.Date(cal.getTimeInMillis() - 86400000));
            if (!presentDates.get(0).equals(today) && !presentDates.get(0).equals(yesterday)) return "0";
            int streak = 0;
            java.util.Date prev = sdf.parse(presentDates.get(0));
            for (String d : presentDates) {
                java.util.Date cur = sdf.parse(d);
                long diff = (prev.getTime() - cur.getTime()) / 86400000;
                if (diff <= 1) { streak++; prev = cur; }
                else break;
            }
            return String.valueOf(streak);
        } catch (Exception e) { return "0"; }
    }
}
