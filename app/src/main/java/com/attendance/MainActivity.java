package com.attendance;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.attendance.db.AppDatabase;
import com.attendance.db.AttendanceDao;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private TextView tvPercent, tvDate, tvGoal, tvNeeded, tvCanMiss, tvStatus,
                     tvStreak, tvPrediction, tvMotivation, tvDev;
    private CardView cardStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db           = AppDatabase.getInstance(this);
        tvPercent    = findViewById(R.id.tvPercent);
        tvDate       = findViewById(R.id.tvDate);
        tvGoal       = findViewById(R.id.tvGoal);
        tvNeeded     = findViewById(R.id.tvNeeded);
        tvCanMiss    = findViewById(R.id.tvCanMiss);
        tvStatus     = findViewById(R.id.tvStatus);
        tvStreak     = findViewById(R.id.tvStreak);
        tvPrediction = findViewById(R.id.tvPrediction);
        tvMotivation = findViewById(R.id.tvMotivation);
        tvDev        = findViewById(R.id.tvDev);
        cardStatus   = findViewById(R.id.cardStatus);
        tvDev.setText("Developed by Badal Biswas  |  badalbiswasno5@gmail.com  |  Help & Feedback");
        tvGoal.setOnClickListener(v -> showGoalPicker());
        tvDev.setOnClickListener(v -> startActivity(new Intent(this, PrivacyPolicyActivity.class)));
        findViewById(R.id.btnMark).setOnClickListener(v -> {
            Intent i = new Intent(this, AttendanceActivity.class);
            i.putExtra("date", getTodayString());
            startActivity(i);
        });
        findViewById(R.id.btnStats).setOnClickListener(v ->
            startActivity(new Intent(this, StatsActivity.class)));
        findViewById(R.id.btnCalendar).setOnClickListener(v ->
            startActivity(new Intent(this, CalendarActivity.class)));
        findViewById(R.id.btnSetup).setOnClickListener(v -> {
            String[] opts = {"Setup Subjects", "Settings", "Privacy Policy"};
            new AlertDialog.Builder(this)
                .setTitle("More Options")
                .setItems(opts, (d, which) -> {
                    if (which == 0) startActivity(new Intent(this, SetupActivity.class));
                    else if (which == 1) startActivity(new Intent(this, SettingsActivity.class));
                    else startActivity(new Intent(this, PrivacyPolicyActivity.class));
                }).show();
        });
        findViewById(R.id.btnSwitch).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileSelectActivity.class));
            finish();
        });
        findViewById(R.id.btnExamTracker).setOnClickListener(v ->
            startActivity(new Intent(this, ExamTrackerActivity.class)));
        findViewById(R.id.btnTimetable).setOnClickListener(v ->
            startActivity(new Intent(this, TimetableActivity.class)));
    }
    @Override
    protected void onResume() { super.onResume(); loadDashboard(); }
    private void loadDashboard() {
        AttendanceDao dao = db.attendanceDao();
        List<String> allDates = dao.getAllDates();
        int goal = Prefs.getGoal(this);
        tvDate.setText(getTodayString());
        tvGoal.setText("Goal: " + goal + "%");
        if (allDates.isEmpty()) {
            tvPercent.setText("0%");
            tvMotivation.setText("Set up subjects and start marking attendance");
            tvNeeded.setText("No data yet");
            tvCanMiss.setText("—");
            tvStreak.setText("0 days");
            tvStatus.setText("No Data");
            tvPrediction.setText("—");
            cardStatus.setCardBackgroundColor(0xFF94A3B8);
            return;
        }
        String minDate = allDates.get(allDates.size() - 1);
        String maxDate = allDates.get(0);
        int present = dao.countPresent(minDate, maxDate);
        int total   = dao.countTotal(minDate, maxDate);
        AttendanceStats stats = new AttendanceStats(present, total, goal);
        float pct = stats.getPercent();
        animatePercent(pct);
        tvCanMiss.setText(stats.canMiss() > 0 ? stats.canMiss() + " classes" : "None");
        int needed = stats.needToAttend();
        tvNeeded.setText(needed > 0 ? "Attend " + needed + " more for " + goal + "%" : "On track ✓");
        tvPrediction.setText(String.format(Locale.getDefault(), "If you miss tomorrow: %.1f%%", stats.percentIfMissNext()));
        tvStreak.setText(stats.getStreakInfo(dao.getPresentDates()) + " days");
        switch (stats.getRiskLevel()) {
            case "SAFE":
                tvStatus.setText(pct >= 90 ? "Excellent Attendance" : "Safe Zone");
                cardStatus.setCardBackgroundColor(0xFF15803D);
                tvMotivation.setText(pct >= 90 ? "Perfect! Keep it up." : "Good attendance. Keep attending.");
                break;
            case "WARNING":
                tvStatus.setText("Warning — Getting Close");
                cardStatus.setCardBackgroundColor(0xFFD97706);
                tvMotivation.setText("Attendance is nearing the limit. Be careful.");
                break;
            default:
                tvStatus.setText("Exam Risk — Below " + goal + "%");
                cardStatus.setCardBackgroundColor(0xFFDC2626);
                tvMotivation.setText("Danger! Attend every class immediately.");
                break;
        }
    }
    private void animatePercent(float target) {
        ValueAnimator a = ValueAnimator.ofFloat(0f, target);
        a.setDuration(900);
        a.addUpdateListener(anim ->
            tvPercent.setText(String.format(Locale.getDefault(), "%.1f%%", (float)anim.getAnimatedValue())));
        a.start();
    }
    private void showGoalPicker() {
        String[] options = {"70%", "75%", "80%", "85%", "90%", "95%"};
        new AlertDialog.Builder(this).setTitle("Set Attendance Goal")
            .setItems(options, (d, which) -> {
                int[] vals = {70, 75, 80, 85, 90, 95};
                Prefs.setGoal(this, vals[which]);
                loadDashboard();
            }).show();
    }
    public static String getTodayString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}
