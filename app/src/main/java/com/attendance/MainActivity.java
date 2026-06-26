package com.attendance;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.db.AttendanceDao;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private TextView tvPercentage, tvPresent, tvTotal, tvDateLabel, tvWarning;
    private Button btnMarkToday, btnStats, btnSetup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getInstance(this);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvPresent = findViewById(R.id.tvPresent);
        tvTotal = findViewById(R.id.tvTotal);
        tvDateLabel = findViewById(R.id.tvDateLabel);
        tvWarning = findViewById(R.id.tvWarning);
        btnMarkToday = findViewById(R.id.btnMarkToday);
        btnStats = findViewById(R.id.btnStats);
        btnSetup = findViewById(R.id.btnSetup);
        btnMarkToday.setOnClickListener(v -> {
            Intent intent = new Intent(this, AttendanceActivity.class);
            intent.putExtra("date", getTodayString());
            startActivity(intent);
        });
        btnStats.setOnClickListener(v -> startActivity(new Intent(this, StatsActivity.class)));
        btnSetup.setOnClickListener(v -> startActivity(new Intent(this, SetupActivity.class)));
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
    }
    private void loadStats() {
        String today = getTodayString();
        tvDateLabel.setText("Today: " + today);
        AttendanceDao dao = db.attendanceDao();
        List<String> allDates = dao.getAllDates();
        if (allDates.isEmpty()) {
            tvPercentage.setText("0%");
            tvPresent.setText("Present: 0");
            tvTotal.setText("Total: 0");
            tvWarning.setVisibility(View.GONE);
            return;
        }
        String minDate = allDates.get(allDates.size() - 1);
        String maxDate = allDates.get(0);
        int presentCount = dao.countPresent(minDate, maxDate);
        int totalCount = dao.countTotal(minDate, maxDate);
        float pct = totalCount > 0 ? (presentCount * 100f / totalCount) : 0;
        tvPercentage.setText(String.format(Locale.getDefault(), "%.1f%%", pct));
        tvPresent.setText("Present: " + presentCount);
        tvTotal.setText("Total classes: " + totalCount);
        if (pct < 75 && totalCount > 0) {
            tvWarning.setVisibility(View.VISIBLE);
            tvWarning.setText("Below 75% attendance!");
        } else {
            tvWarning.setVisibility(View.GONE);
        }
    }
    public static String getTodayString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
