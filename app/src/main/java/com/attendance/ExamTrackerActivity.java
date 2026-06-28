package com.attendance;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.model.ExamTracker;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
public class ExamTrackerActivity extends AppCompatActivity {
    private AppDatabase db;
    private int profileId;
    private LinearLayout llExams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_tracker);
        db = AppDatabase.getInstance(this);
        profileId = Prefs.getActiveProfile(this);
        llExams = findViewById(R.id.llExams);
        findViewById(R.id.btnAddExam).setOnClickListener(v -> showAddExam());
        loadExams();
    }
    @Override
    protected void onResume() { super.onResume(); loadExams(); }
    private void loadExams() {
        llExams.removeAllViews();
        List<ExamTracker> exams = db.examTrackerDao().getByProfile(profileId);
        String today = MainActivity.getTodayString();
        if (exams.isEmpty()) {
            TextView t = new TextView(this);
            t.setText("No upcoming exams. Add one!");
            t.setTextColor(0xFF475569); t.setPadding(16,24,16,16);
            llExams.addView(t); return;
        }
        for (ExamTracker e : exams) {
            long daysLeft = getDaysLeft(today, e.examDate);
            View row = getLayoutInflater().inflate(R.layout.item_exam_tracker_row, llExams, false);
            ((TextView)row.findViewById(R.id.tvExamName)).setText(e.examName);
            ((TextView)row.findViewById(R.id.tvSubject)).setText(e.subject);
            ((TextView)row.findViewById(R.id.tvDate)).setText(e.examDate);
            TextView tvDays = row.findViewById(R.id.tvDaysLeft);
            if (daysLeft < 0) {
                tvDays.setText("Completed");
                tvDays.setTextColor(0xFF64748B);
            } else if (daysLeft == 0) {
                tvDays.setText("TODAY!");
                tvDays.setTextColor(0xFFDC2626);
            } else {
                tvDays.setText(daysLeft + " days left");
                tvDays.setTextColor(daysLeft <= 3 ? 0xFFDC2626 : daysLeft <= 7 ? 0xFFD97706 : 0xFF15803D);
            }
            if (e.notes != null && !e.notes.isEmpty())
                ((TextView)row.findViewById(R.id.tvNotes)).setText(e.notes);
            row.findViewById(R.id.btnDelete).setOnClickListener(v -> {
                db.examTrackerDao().deleteById(e.id);
                loadExams();
            });
            llExams.addView(row);
        }
    }
    private long getDaysLeft(String today, String examDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            long diff = sdf.parse(examDate).getTime() - sdf.parse(today).getTime();
            return TimeUnit.MILLISECONDS.toDays(diff);
        } catch (Exception e) { return 0; }
    }
    private String[] selectedDate = {MainActivity.getTodayString()};
    private void showAddExam() {
        View v = getLayoutInflater().inflate(R.layout.dialog_add_exam, null);
        EditText etName = v.findViewById(R.id.etExamName);
        EditText etSubject = v.findViewById(R.id.etSubject);
        EditText etNotes = v.findViewById(R.id.etNotes);
        TextView tvDate = v.findViewById(R.id.tvSelectedDate);
        tvDate.setText(selectedDate[0]);
        v.findViewById(R.id.btnPickDate).setOnClickListener(btn -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (dp, y, m, d) -> {
                selectedDate[0] = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m+1, d);
                tvDate.setText(selectedDate[0]);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
        new AlertDialog.Builder(this).setTitle("Add Upcoming Exam").setView(v)
            .setPositiveButton("Add", (d, w) -> {
                String name = etName.getText().toString().trim();
                String sub = etSubject.getText().toString().trim();
                String notes = etNotes.getText().toString().trim();
                if (name.isEmpty()) { Toast.makeText(this,"Enter exam name",Toast.LENGTH_SHORT).show(); return; }
                db.examTrackerDao().insert(new ExamTracker(profileId, name, sub, selectedDate[0], notes));
                loadExams();
            }).setNegativeButton("Cancel", null).show();
    }
}
