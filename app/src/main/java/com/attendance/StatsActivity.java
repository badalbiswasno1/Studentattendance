package com.attendance;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.db.AttendanceDao;
import com.attendance.db.SubjectDao;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.Subject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class StatsActivity extends AppCompatActivity {
    private AppDatabase db;
    private AttendanceDao attendanceDao;
    private SubjectDao subjectDao;
    private String startDate = "", endDate = "";
    private TextView tvStartDate, tvEndDate, tvOverallPct, tvOverallDetail, tvWarningMsg, tvDev;
    private LinearLayout llSubjectStats, llSubjectFilter;
    private Button btnPickStart, btnPickEnd, btnFilter;
    private View warningBar;
    private List<Subject> allSubjects = new ArrayList<>();
    private List<CheckBox> checkBoxes = new ArrayList<>();
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
        llSubjectFilter = findViewById(R.id.llSubjectFilter);
        btnPickStart = findViewById(R.id.btnPickStart);
        btnPickEnd = findViewById(R.id.btnPickEnd);
        btnFilter = findViewById(R.id.btnFilter);
        warningBar = findViewById(R.id.warningBar);
        tvWarningMsg = findViewById(R.id.tvWarningMsg);
        tvDev = findViewById(R.id.tvDev);
        tvDev.setText("Developed by Badal Biswas");
        allSubjects = subjectDao.getAll();
        buildSubjectCheckboxes();
        btnPickStart.setOnClickListener(v -> pickDate(true));
        btnPickEnd.setOnClickListener(v -> pickDate(false));
        btnFilter.setOnClickListener(v -> loadStats());
        startDate = MainActivity.getTodayString();
        endDate = MainActivity.getTodayString();
        tvStartDate.setText(startDate);
        tvEndDate.setText(endDate);
        loadStats();
    }
    private void buildSubjectCheckboxes() {
        llSubjectFilter.removeAllViews();
        checkBoxes.clear();
        for (Subject s : allSubjects) {
            CheckBox cb = new CheckBox(this);
            cb.setText(s.name);
            cb.setChecked(true);
            cb.setTextColor(0xFF0F172A);
            cb.setTextSize(14);
            cb.setPadding(8, 8, 8, 8);
            checkBoxes.add(cb);
            llSubjectFilter.addView(cb);
        }
    }
    private List<Integer> getSelectedSubjectIds() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size() && i < allSubjects.size(); i++) {
            if (checkBoxes.get(i).isChecked()) {
                ids.add(allSubjects.get(i).id);
            }
        }
        return ids;
    }
    private List<String> getDatesBetween(String from, String to) {
        List<String> dates = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date start = sdf.parse(from);
            Date end = sdf.parse(to);
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);
            while (!cal.getTime().after(end)) {
                dates.add(sdf.format(cal.getTime()));
                cal.add(Calendar.DATE, 1);
            }
        } catch (Exception ignored) {}
        return dates;
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
            if (isStart) { startDate = date; tvStartDate.setText(date); }
            else { endDate = date; tvEndDate.setText(date); }
            loadStats();
        }, y, m, d).show();
    }
    private void loadStats() {
        String from = startDate.compareTo(endDate) <= 0 ? startDate : endDate;
        String to = startDate.compareTo(endDate) <= 0 ? endDate : startDate;
        List<Integer> selectedIds = getSelectedSubjectIds();
        if (selectedIds.isEmpty()) {
            tvOverallPct.setText("0%");
            tvOverallDetail.setText("No subjects selected");
            warningBar.setVisibility(View.GONE);
            llSubjectStats.removeAllViews();
            return;
        }
        List<String> dates = getDatesBetween(from, to);
        int totalPresent = 0;
        int totalClasses = 0;
        llSubjectStats.removeAllViews();
        for (int subId : selectedIds) {
            Subject subject = null;
            for (Subject s : allSubjects) { if (s.id == subId) { subject = s; break; } }
            int subPresent = 0;
            int subTotal = dates.size();
            for (String date : dates) {
                AttendanceRecord rec = attendanceDao.getRecord(date, subId);
                if (rec != null && rec.present) subPresent++;
            }
            totalPresent += subPresent;
            totalClasses += subTotal;
            float subPct = subTotal > 0 ? (subPresent * 100f / subTotal) : 0;
            View row = getLayoutInflater().inflate(R.layout.item_stat_row, llSubjectStats, false);
            TextView tvName = row.findViewById(R.id.tvSubjectName);
            TextView tvPct = row.findViewById(R.id.tvPct);
            View bar = row.findViewById(R.id.progressBar);
            tvName.setText(subject != null ? subject.name : "Subject #" + subId);
            tvPct.setText(String.format(Locale.getDefault(), "%.0f%% (%d/%d)", subPct, subPresent, subTotal));
            android.view.ViewGroup.LayoutParams params = bar.getLayoutParams();
            params.width = (int) (subPct * 6);
            bar.setLayoutParams(params);
            bar.setBackgroundColor(subPct >= 75 ? getResources().getColor(R.color.green, null) : getResources().getColor(R.color.red, null));
            llSubjectStats.addView(row);
        }
        float pct = totalClasses > 0 ? (totalPresent * 100f / totalClasses) : 0;
        tvOverallPct.setText(String.format(Locale.getDefault(), "%.1f%%", pct));
        tvOverallDetail.setText(totalPresent + " present / " + totalClasses + " total");
        if (pct < 75 && totalClasses > 0) {
            warningBar.setVisibility(View.VISIBLE);
            int needed = (int) Math.ceil((75.0 * totalClasses - 100.0 * totalPresent) / 25.0);
            tvWarningMsg.setText("Need " + needed + " more classes to reach 75%");
        } else {
            warningBar.setVisibility(View.GONE);
        }
    }
}
