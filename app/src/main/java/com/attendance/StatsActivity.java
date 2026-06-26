package com.attendance;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.db.AttendanceDao;
import com.attendance.db.SubjectAttendanceSummary;
import com.attendance.db.SubjectDao;
import com.attendance.model.Subject;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
public class StatsActivity extends AppCompatActivity {
    private AppDatabase db;
    private AttendanceDao attendanceDao;
    private SubjectDao subjectDao;
    private String startDate = "", endDate = "";
    private TextView tvStartDate, tvEndDate, tvOverallPct, tvOverallDetail, tvWarningMsg, tvDev;
    private LinearLayout llSubjectStats;
    private Button btnPickStart, btnPickEnd, btnFilter;
    private View warningBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        db = AppDatabase.getInstance(this);
        attendanceDao = db.attendanceDao();
        subjectDao = db.subjectDao();
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvOverallPct = findViewById(R.id.tvOverallPct);
        tvOverallDetail = findViewById(R.id.tvOverallDetail);
        llSubjectStats = findViewById(R.id.llSubjectStats);
        btnPickStart = findViewById(R.id.btnPickStart);
        btnPickEnd = findViewById(R.id.btnPickEnd);
        btnFilter = findViewById(R.id.btnFilter);
        warningBar = findViewById(R.id.warningBar);
        tvWarningMsg = findViewById(R.id.tvWarningMsg);
        tvDev = findViewById(R.id.tvDev);
        tvDev.setText("Developed by Badal Biswas");
        btnPickStart.setOnClickListener(v -> pickDate(true));
        btnPickEnd.setOnClickListener(v -> pickDate(false));
        btnFilter.setOnClickListener(v -> loadStats());
        List<String> allDates = attendanceDao.getAllDates();
        if (!allDates.isEmpty()) {
            endDate = allDates.get(0);
            startDate = allDates.get(allDates.size() - 1);
        } else {
            startDate = MainActivity.getTodayString();
            endDate = MainActivity.getTodayString();
        }
        tvStartDate.setText(startDate);
        tvEndDate.setText(endDate);
        loadStats();
    }
    private void pickDate(boolean isStart) {
        String current = isStart ? startDate : endDate;
        int y = 2026, m = 5, d = 27;
        try {
            String[] parts = current.split("-");
            y = Integer.parseInt(parts[0]);
            m = Integer.parseInt(parts[1]) - 1;
            d = Integer.parseInt(parts[2]);
        } catch (Exception ignored) {}
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
            if (isStart) {
                startDate = date;
                tvStartDate.setText(date);
            } else {
                endDate = date;
                tvEndDate.setText(date);
            }
        }, y, m, d).show();
    }
    private void loadStats() {
        if (startDate.isEmpty() || endDate.isEmpty()) return;
        String from = startDate.compareTo(endDate) <= 0 ? startDate : endDate;
        String to = startDate.compareTo(endDate) <= 0 ? endDate : startDate;
        int present = attendanceDao.countPresent(from, to);
        int total = attendanceDao.countTotal(from, to);
        float pct = total > 0 ? (present * 100f / total) : 0;
        tvOverallPct.setText(String.format(Locale.getDefault(), "%.1f%%", pct));
        tvOverallDetail.setText(present + " present / " + total + " total");
        if (pct < 75 && total > 0) {
            warningBar.setVisibility(View.VISIBLE);
            int needed = (int) Math.ceil((75.0 * total - 100.0 * present) / 25.0);
            tvWarningMsg.setText("Need " + needed + " more classes to reach 75%");
        } else {
            warningBar.setVisibility(View.GONE);
        }
        llSubjectStats.removeAllViews();
        List<SubjectAttendanceSummary> summaries = attendanceDao.getSubjectSummary(from, to);
        for (SubjectAttendanceSummary s : summaries) {
            Subject found = null;
            for (Subject sub : subjectDao.getAll()) {
                if (sub.id == s.subjectId) { found = sub; break; }
            }
            String subName = found != null ? found.name : "Subject #" + s.subjectId;
            float subPct = s.totalCount > 0 ? (s.presentCount * 100f / s.totalCount) : 0;
            View row = getLayoutInflater().inflate(R.layout.item_stat_row, llSubjectStats, false);
            TextView tvName = row.findViewById(R.id.tvSubjectName);
            TextView tvPct = row.findViewById(R.id.tvPct);
            View bar = row.findViewById(R.id.progressBar);
            tvName.setText(subName);
            tvPct.setText(String.format(Locale.getDefault(), "%.0f%% (%d/%d)", subPct, s.presentCount, s.totalCount));
            android.view.ViewGroup.LayoutParams params = bar.getLayoutParams();
            params.width = (int) (subPct * 6);
            bar.setLayoutParams(params);
            bar.setBackgroundColor(subPct >= 75 ? getResources().getColor(R.color.green, null) : getResources().getColor(R.color.red, null));
            llSubjectStats.addView(row);
        }
    }
}
