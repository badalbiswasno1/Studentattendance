package com.attendance;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private TextView tvPercent, tvDate, tvGoal, tvNeeded, tvCanMiss, tvStatus, tvStreak, tvPrediction, tvMotivation, tvDev;
    private CardView cardStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getInstance(this);
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
        tvDev.setText("Developed by Badal Biswas • badalbiswasno5@gmail.com");
        tvGoal.setOnClickListener(v -> showGoalPicker());
        findViewById(R.id.btnMark).setOnClickListener(v -> {
            Intent i = new Intent(this, AttendanceActivity.class);
            i.putExtra("date", getTodayString());
            startActivity(i);
        });
        findViewById(R.id.btnStats).setOnClickListener(v -> startActivity(new Intent(this, StatsActivity.class)));
        findViewById(R.id.btnSetup).setOnClickListener(v -> startActivity(new Intent(this, SetupActivity.class)));
        findViewById(R.id.btnCalendar).setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        findViewById(R.id.btnSwitch).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileSelectActivity.class));
            finish();
        });
    }
    @Override
    protected void onResume() { super.onResume(); loadDashboard(); }
    private void loadDashboard() {
        AttendanceDao dao = db.attendanceDao();
        List<String> allDates = dao.getAllDates();
        int goal = Prefs.getGoal(this);
        tvDate.setText(getTodayString());
        tvGoal.setText("Goal: " + goal + "% (tap to change)");
        if (allDates.isEmpty()) {
            tvPercent.setText("0%"); tvNeeded.setText("Mark attendance to begin");
            tvCanMiss.setText("-"); tvStreak.setText("Streak: 0 days");
            tvStatus.setText("No Data"); tvPrediction.setText("Start marking attendance");
            tvMotivation.setText("Welcome! Set up subjects and start tracking.");
            cardStatus.setCardBackgroundColor(0xFF94A3B8); return;
        }
        String minDate = allDates.get(allDates.size() - 1);
        String maxDate = allDates.get(0);
        int present = dao.countPresent(minDate, maxDate);
        int total = dao.countTotal(minDate, maxDate);
        AttendanceStats stats = new AttendanceStats(present, total, goal);
        float pct = stats.getPercent();
        animatePercent(pct);
        tvCanMiss.setText(stats.canMiss() > 0 ? "Can miss " + stats.canMiss() + " more" : "Cannot miss any!");
        int needed = stats.needToAttend();
        tvNeeded.setText(needed > 0 ? "Attend " + needed + " more to reach " + goal + "%" : "On track for " + goal + "% goal");
        tvPrediction.setText(String.format(Locale.getDefault(), "If miss tomorrow: %.1f%%", stats.percentIfMissNext()));
        List<String> presentDates = dao.getPresentDates();
        tvStreak.setText("Streak: " + stats.getStreakInfo(presentDates) + " days");
        switch (stats.getRiskLevel()) {
            case "SAFE":
                tvStatus.setText("SAFE"); cardStatus.setCardBackgroundColor(0xFF16A34A);
                tvMotivation.setText(pct >= 90 ? "Excellent! Perfect attendance." : "Great! Keep attending."); break;
            case "WARNING":
                tvStatus.setText("WARNING"); cardStatus.setCardBackgroundColor(0xFFF59E0B);
                tvMotivation.setText("Getting close to limit. Be careful!"); break;
            default:
                tvStatus.setText("EXAM RISK"); cardStatus.setCardBackgroundColor(0xFFDC2626);
                tvMotivation.setText("Danger! Attend all classes immediately."); break;
        }
    }
    private void animatePercent(float target) {
        ValueAnimator a = ValueAnimator.ofFloat(0f, target);
        a.setDuration(800);
        a.addUpdateListener(anim -> tvPercent.setText(String.format(Locale.getDefault(), "%.1f%%", (float)anim.getAnimatedValue())));
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
