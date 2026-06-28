package com.attendance;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.db.AttendanceDao;
import com.attendance.model.AttendanceRecord;
import java.text.SimpleDateFormat;
import java.util.*;
public class CalendarActivity extends AppCompatActivity {
    private AppDatabase db;
    private LinearLayout llCalendar;
    private int profileId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        db = AppDatabase.getInstance(this);
        profileId = Prefs.getActiveProfile(this);
        llCalendar = findViewById(R.id.llCalendar);
        ((TextView)findViewById(R.id.tvDev)).setText("Developed by Badal Biswas • badalbiswasno5@gmail.com");
        buildCalendar();
    }
    private void buildCalendar() {
        llCalendar.removeAllViews();
        AttendanceDao dao = db.attendanceDao();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        for (int month = 0; month < 12; month++) {
            TextView tvMonth = new TextView(this);
            tvMonth.setText(monthNames[month] + " " + currentYear);
            tvMonth.setTextColor(0xFF0F172A); tvMonth.setTextSize(16);
            tvMonth.setTypeface(null, android.graphics.Typeface.BOLD);
            tvMonth.setPadding(8, 24, 8, 8);
            llCalendar.addView(tvMonth);
            LinearLayout weekRow = null;
            Calendar dayCal = Calendar.getInstance();
            dayCal.set(currentYear, month, 1);
            int daysInMonth = dayCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            int startDow = dayCal.get(Calendar.DAY_OF_WEEK) - 1;
            int cellCount = 0;
            weekRow = newWeekRow();
            for (int blank = 0; blank < startDow; blank++) {
                weekRow.addView(emptyCell()); cellCount++;
            }
            for (int day = 1; day <= daysInMonth; day++) {
                String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", currentYear, month+1, day);
                List<AttendanceRecord> records = dao.getByDateRange(dateStr, dateStr);
                int totalSubjects = db.subjectDao().getActiveCount();
                int presentCount = 0;
                for (AttendanceRecord r : records) { if (r.present && !r.cancelled) presentCount++; }
                int bgColor;
                if (records.isEmpty() || totalSubjects == 0) {
                    bgColor = 0xFFE2E8F0;
                } else {
                    float pct = presentCount * 100f / totalSubjects;
                    if (pct >= 80) bgColor = 0xFF16A34A;
                    else if (pct >= 50) bgColor = 0xFFF97316;
                    else bgColor = 0xFFDC2626;
                }
                TextView tv = new TextView(this);
                tv.setText(String.valueOf(day));
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(records.isEmpty() ? 0xFF94A3B8 : 0xFFFFFFFF);
                tv.setTextSize(12);
                tv.setPadding(4,8,4,8);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                lp.setMargins(2,2,2,2);
                tv.setLayoutParams(lp);
                tv.setBackgroundColor(bgColor);
                weekRow.addView(tv); cellCount++;
                if (cellCount % 7 == 0) {
                    llCalendar.addView(weekRow);
                    weekRow = newWeekRow();
                }
            }
            while (cellCount % 7 != 0) { weekRow.addView(emptyCell()); cellCount++; }
            llCalendar.addView(weekRow);
        }
        LinearLayout legend = new LinearLayout(this);
        legend.setOrientation(LinearLayout.HORIZONTAL);
        legend.setPadding(8,16,8,8);
        legend.addView(legendItem(0xFF16A34A, "High (≥80%)"));
        legend.addView(legendItem(0xFFF97316, "Medium (≥50%)"));
        legend.addView(legendItem(0xFFDC2626, "Low (<50%)"));
        llCalendar.addView(legend);
    }
    private LinearLayout newWeekRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        return row;
    }
    private TextView emptyCell() {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tv.setLayoutParams(lp);
        return tv;
    }
    private LinearLayout legendItem(int color, String label) {
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(0,0,16,0);
        ll.setGravity(android.view.Gravity.CENTER_VERTICAL);
        TextView dot = new TextView(this); dot.setText("  ");
        dot.setBackgroundColor(color);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(28, 28);
        lp.setMargins(0,0,6,0);
        dot.setLayoutParams(lp);
        TextView t = new TextView(this); t.setText(label);
        t.setTextColor(0xFF475569); t.setTextSize(11);
        ll.addView(dot); ll.addView(t);
        return ll;
    }
}
