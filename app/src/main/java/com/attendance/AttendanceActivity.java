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
    private TextView tvDate, tvLocked;
    private LinearLayout llSubjects;
    private Button btnDatePicker, btnSave;
    private static final int STATE_ABSENT = 0;
    private static final int STATE_PRESENT = 1;
    private static final int STATE_CANCELED = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        db = AppDatabase.getInstance(this);
        attendanceDao = db.attendanceDao();
        subjectDao = db.subjectDao();
        tvDate = findViewById(R.id.tvSelectedDate);
        tvLocked = findViewById(R.id.tvLocked);
        llSubjects = findViewById(R.id.llSubjects);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnSave = findViewById(R.id.btnSave);
        selectedDate = getIntent().getStringExtra("date");
        if (selectedDate == null) selectedDate = MainActivity.getTodayString();
        btnDatePicker.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveAttendance());
        loadSubjects();
    }
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (v, y, m, d) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
            loadSubjects();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
    private boolean isLocked(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            long diff = sdf.parse(MainActivity.getTodayString()).getTime() - sdf.parse(date).getTime();
            return diff / 86400000 > 2;
        } catch (Exception e) { return false; }
    }
    private void loadSubjects() {
        tvDate.setText("Date: " + selectedDate);
        llSubjects.removeAllViews();
        boolean locked = isLocked(selectedDate);
        tvLocked.setVisibility(locked ? View.VISIBLE : View.GONE);
        if (locked) tvLocked.setText("Locked: older than 2 days");
        btnSave.setEnabled(!locked);
        List<Subject> subjects = subjectDao.getAllActive();
        if (subjects.isEmpty()) {
            TextView t = new TextView(this);
            t.setText("No subjects. Go to Setup.");
            t.setPadding(32, 32, 32, 32);
            llSubjects.addView(t);
            return;
        }
        for (Subject subject : subjects) {
            AttendanceRecord rec = attendanceDao.getRecord(selectedDate, subject.id);
            int state = STATE_ABSENT;
            if (rec != null) {
                if (rec.canceled) state = STATE_CANCELED;
                else if (rec.present) state = STATE_PRESENT;
            }
            View card = getLayoutInflater().inflate(R.layout.item_subject_card, llSubjects, false);
            TextView tvName = card.findViewById(R.id.tvSubjectName);
            TextView tvStatus = card.findViewById(R.id.tvStatus);
            View indicator = card.findViewById(R.id.viewIndicator);
            Button btnPresent = card.findViewById(R.id.btnPresent);
            Button btnAbsent = card.findViewById(R.id.btnAbsent);
            Button btnCancel = card.findViewById(R.id.btnCancel);
            tvName.setText(subject.getDisplayName());
            final int[] curState = {state};
            updateCardState(tvStatus, indicator, (CardView) card, curState[0]);
            if (!locked) {
                btnPresent.setOnClickListener(v -> { curState[0] = STATE_PRESENT; updateCardState(tvStatus, indicator, (CardView) card, curState[0]); card.setTag(curState[0]); });
                btnAbsent.setOnClickListener(v -> { curState[0] = STATE_ABSENT; updateCardState(tvStatus, indicator, (CardView) card, curState[0]); card.setTag(curState[0]); });
                btnCancel.setOnClickListener(v -> { curState[0] = STATE_CANCELED; updateCardState(tvStatus, indicator, (CardView) card, curState[0]); card.setTag(curState[0]); });
            } else {
                btnPresent.setEnabled(false); btnAbsent.setEnabled(false); btnCancel.setEnabled(false);
            }
            card.setTag(curState[0]);
            llSubjects.addView(card);
        }
    }
    private void updateCardState(TextView tvStatus, View indicator, CardView card, int state) {
        switch (state) {
            case STATE_PRESENT:
                tvStatus.setText("Present");
                tvStatus.setTextColor(0xFF15803D);
                indicator.setBackgroundColor(0xFF15803D);
                card.setCardBackgroundColor(0xFFDCFCE7);
                break;
            case STATE_CANCELED:
                tvStatus.setText("Canceled");
                tvStatus.setTextColor(0xFF92400E);
                indicator.setBackgroundColor(0xFFF59E0B);
                card.setCardBackgroundColor(0xFFFEF3C7);
                break;
            default:
                tvStatus.setText("Absent");
                tvStatus.setTextColor(0xFFB91C1C);
                indicator.setBackgroundColor(0xFFB91C1C);
                card.setCardBackgroundColor(0xFFFECACA);
                break;
        }
    }
    private void saveAttendance() {
        List<Subject> subjects = subjectDao.getAllActive();
        for (int i = 0; i < llSubjects.getChildCount() && i < subjects.size(); i++) {
            View card = llSubjects.getChildAt(i);
            Object tag = card.getTag();
            int state = tag instanceof Integer ? (Integer) tag : STATE_ABSENT;
            Subject subject = subjects.get(i);
            AttendanceRecord existing = attendanceDao.getRecord(selectedDate, subject.id);
            boolean present = state == STATE_PRESENT;
            boolean canceled = state == STATE_CANCELED;
            if (existing != null) {
                existing.present = present;
                existing.canceled = canceled;
                attendanceDao.update(existing);
            } else {
                attendanceDao.insert(new AttendanceRecord(selectedDate, subject.id, present, canceled));
            }
        }
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
