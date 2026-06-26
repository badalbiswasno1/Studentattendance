package com.attendance;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.attendance.db.AppDatabase;
import com.attendance.db.AttendanceDao;
import com.attendance.db.SubjectDao;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.Subject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class AttendanceActivity extends AppCompatActivity {
    private AppDatabase db;
    private AttendanceDao attendanceDao;
    private SubjectDao subjectDao;
    private String selectedDate;
    private TextView tvSelectedDate, tvLocked;
    private LinearLayout llSubjects;
    private Button btnDatePicker, btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        db = AppDatabase.getInstance(this);
        attendanceDao = db.attendanceDao();
        subjectDao = db.subjectDao();
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvLocked = findViewById(R.id.tvLocked);
        llSubjects = findViewById(R.id.llSubjects);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnSave = findViewById(R.id.btnSave);
        selectedDate = getIntent().getStringExtra("date");
        if (selectedDate == null) selectedDate = MainActivity.getTodayString();
        btnDatePicker.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveAttendance());
        loadSubjectsForDate();
    }
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
            loadSubjectsForDate();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
    private boolean isLocked(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date d = sdf.parse(date);
            Date today = sdf.parse(MainActivity.getTodayString());
            long diffDays = (today.getTime() - d.getTime()) / (1000 * 60 * 60 * 24);
            return diffDays > 2;
        } catch (Exception e) {
            return false;
        }
    }
    private void loadSubjectsForDate() {
        tvSelectedDate.setText("Date: " + selectedDate);
        llSubjects.removeAllViews();
        boolean locked = isLocked(selectedDate);
        if (locked) {
            tvLocked.setVisibility(View.VISIBLE);
            tvLocked.setText("Locked: older than 2 days");
            btnSave.setEnabled(false);
        } else {
            tvLocked.setVisibility(View.GONE);
            btnSave.setEnabled(true);
        }
        List<Subject> subjects = subjectDao.getAllActive();
        if (subjects.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No subjects. Go to Setup first.");
            empty.setPadding(32, 32, 32, 32);
            llSubjects.addView(empty);
            return;
        }
        for (Subject subject : subjects) {
            AttendanceRecord record = attendanceDao.getRecord(selectedDate, subject.id);
            boolean isPresent = record != null && record.present;
            CardView card = (CardView) getLayoutInflater().inflate(R.layout.item_subject_card, llSubjects, false);
            TextView tvName = card.findViewById(R.id.tvSubjectName);
            TextView tvStatus = card.findViewById(R.id.tvStatus);
            View indicator = card.findViewById(R.id.viewIndicator);
            tvName.setText(subject.name);
            updateCardUI(tvStatus, indicator, card, isPresent);
            if (!locked) {
                final boolean[] cur = {isPresent};
                card.setOnClickListener(v -> {
                    cur[0] = !cur[0];
                    updateCardUI(tvStatus, indicator, card, cur[0]);
                    card.setTag(cur[0]);
                });
            }
            card.setTag(isPresent);
            llSubjects.addView(card);
        }
    }
    private void updateCardUI(TextView tvStatus, View indicator, CardView card, boolean present) {
        if (present) {
            tvStatus.setText("Present");
            tvStatus.setTextColor(getResources().getColor(R.color.green, null));
            indicator.setBackgroundColor(getResources().getColor(R.color.green, null));
            card.setCardBackgroundColor(getResources().getColor(R.color.greenLight, null));
        } else {
            tvStatus.setText("Absent");
            tvStatus.setTextColor(getResources().getColor(R.color.red, null));
            indicator.setBackgroundColor(getResources().getColor(R.color.red, null));
            card.setCardBackgroundColor(getResources().getColor(R.color.redLight, null));
        }
    }
    private void saveAttendance() {
        List<Subject> subjects = subjectDao.getAllActive();
        for (int i = 0; i < llSubjects.getChildCount() && i < subjects.size(); i++) {
            CardView card = (CardView) llSubjects.getChildAt(i);
            Boolean present = (Boolean) card.getTag();
            if (present == null) present = false;
            Subject subject = subjects.get(i);
            AttendanceRecord existing = attendanceDao.getRecord(selectedDate, subject.id);
            if (existing != null) {
                existing.present = present;
                attendanceDao.update(existing);
            } else {
                attendanceDao.insert(new AttendanceRecord(selectedDate, subject.id, present));
            }
        }
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
