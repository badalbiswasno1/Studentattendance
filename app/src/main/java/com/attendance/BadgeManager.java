package com.attendance;
import com.attendance.db.AppDatabase;
import com.attendance.db.AttendanceDao;
import com.attendance.model.Badge;
import java.util.ArrayList;
import java.util.List;
public class BadgeManager {
    public static List<Badge> checkAndAward(android.content.Context ctx, int profileId) {
        AppDatabase db = AppDatabase.getInstance(ctx);
        AttendanceDao dao = db.attendanceDao();
        List<String> allDates = dao.getAllDates();
        List<Badge> newBadges = new ArrayList<>();
        if (allDates.isEmpty()) return newBadges;
        String minDate = allDates.get(allDates.size() - 1);
        String maxDate = allDates.get(0);
        int present = dao.countPresent(minDate, maxDate);
        int total = dao.countTotal(minDate, maxDate);
        float pct = total > 0 ? present * 100f / total : 0;
        String today = MainActivity.getTodayString();
        com.attendance.AttendanceStats stats = new com.attendance.AttendanceStats(present, total, 75);
        String streak = stats.getStreakInfo(dao.getPresentDates());
        int streakDays = 0;
        try { streakDays = Integer.parseInt(streak); } catch (Exception ignored) {}
        newBadges.addAll(awardIfNew(db, profileId, "first_class", "First Step", "Marked your first attendance", today, total >= 1));
        newBadges.addAll(awardIfNew(db, profileId, "streak_3", "3-Day Streak", "Present 3 days in a row", today, streakDays >= 3));
        newBadges.addAll(awardIfNew(db, profileId, "streak_7", "7-Day Streak", "Present 7 days in a row", today, streakDays >= 7));
        newBadges.addAll(awardIfNew(db, profileId, "streak_14", "14-Day Streak", "Present 14 days in a row", today, streakDays >= 14));
        newBadges.addAll(awardIfNew(db, profileId, "streak_30", "Attendance King", "Present 30 days in a row", today, streakDays >= 30));
        newBadges.addAll(awardIfNew(db, profileId, "pct_75", "75% Achiever", "Reached 75% attendance", today, pct >= 75));
        newBadges.addAll(awardIfNew(db, profileId, "pct_85", "85% Star", "Reached 85% attendance", today, pct >= 85));
        newBadges.addAll(awardIfNew(db, profileId, "pct_95", "95% Legend", "Reached 95% attendance", today, pct >= 95));
        newBadges.addAll(awardIfNew(db, profileId, "pct_100", "Perfect Score", "100% attendance!", today, pct >= 100));
        newBadges.addAll(awardIfNew(db, profileId, "classes_10", "10 Classes", "Attended 10 classes", today, present >= 10));
        newBadges.addAll(awardIfNew(db, profileId, "classes_50", "50 Classes", "Attended 50 classes", today, present >= 50));
        newBadges.addAll(awardIfNew(db, profileId, "classes_100", "Century", "Attended 100 classes", today, present >= 100));
        return newBadges;
    }
    private static List<Badge> awardIfNew(AppDatabase db, int profileId, String key, String name, String desc, String date, boolean condition) {
        List<Badge> result = new ArrayList<>();
        if (condition && db.badgeDao().hasEarned(profileId, key) == 0) {
            Badge b = new Badge(profileId, key, name, desc, date);
            db.badgeDao().insert(b);
            result.add(b);
        }
        return result;
    }
    public static String getBadgeEmoji(String key) {
        switch (key) {
            case "first_class": return "🎯";
            case "streak_3": return "🔥";
            case "streak_7": return "⚡";
            case "streak_14": return "💪";
            case "streak_30": return "👑";
            case "pct_75": return "✅";
            case "pct_85": return "⭐";
            case "pct_95": return "🌟";
            case "pct_100": return "🏆";
            case "classes_10": return "📚";
            case "classes_50": return "🎖";
            case "classes_100": return "🥇";
            default: return "🏅";
        }
    }
}
