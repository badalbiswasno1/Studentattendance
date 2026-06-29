package com.attendance;
import android.view.View;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.db.AttendanceDao;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.Subject;
import java.text.SimpleDateFormat;
import java.util.*;
public class WeeklyStatsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_stats);
        AppDatabase db = AppDatabase.getInstance(this);
        AttendanceDao dao = db.attendanceDao();
        List<Subject> subjects = db.subjectDao().getAllActive();
        WeeklyChartView chart = findViewById(R.id.weeklyChart);
        LinearLayout llSummary = findViewById(R.id.llSummary);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DAY_OF_WEEK, -(today == Calendar.SUNDAY ? 6 : today - Calendar.MONDAY));
        float[] values = new float[7];
        String[] labels = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        String[] dates = new String[7];
        for (int i = 0; i < 7; i++) {
            dates[i] = sdf.format(cal.getTime());
            cal.add(Calendar.DATE, 1);
        }
        for (int i = 0; i < 7; i++) {
            List<AttendanceRecord> recs = dao.getByDateRange(dates[i], dates[i]);
            if (recs.isEmpty()) { values[i] = 0; continue; }
            int present = 0, total = 0;
            for (AttendanceRecord r : recs) {
                if (!r.cancelled) { total++; if (r.present) present++; }
            }
            values[i] = total > 0 ? present * 100f / total : 0;
        }
        chart.setValues(values, labels);
        llSummary.removeAllViews();
        for (int i = 0; i < 7; i++) {
            if (values[i] == 0) continue;
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            TextView tvDay = new TextView(this);
            tvDay.setText(labels[i] + "  " + dates[i]);
            tvDay.setTextSize(13); tvDay.setTextColor(0xFF0F172A);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvDay.setLayoutParams(lp);
            TextView tvPct = new TextView(this);
            tvPct.setText(String.format(Locale.getDefault(), "%.0f%%", values[i]));
            tvPct.setTextSize(14); tvPct.setTypeface(null, android.graphics.Typeface.BOLD);
            tvPct.setTextColor(values[i] >= 75 ? 0xFF15803D : values[i] >= 50 ? 0xFFD97706 : 0xFFDC2626);
            row.addView(tvDay); row.addView(tvPct);
            View divider = new View(this);
            divider.setBackgroundColor(0xFFE2E8F0);
            LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            llSummary.addView(row);
            llSummary.addView(divider, dp);
        }
    }
}
