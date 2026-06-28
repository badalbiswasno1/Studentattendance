package com.attendance;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.db.ExamMarkDao;
import com.attendance.db.StudentAttendanceDao;
import com.attendance.model.ExamMark;
import com.attendance.model.Student;
import java.util.List;
import java.util.Locale;
public class StudentDetailActivity extends AppCompatActivity {
    private AppDatabase db;
    private int studentId, profileId;
    private LinearLayout llMarks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        db = AppDatabase.getInstance(this);
        studentId = getIntent().getIntExtra("studentId", -1);
        profileId = Prefs.getActiveProfile(this);
        Student s = db.studentDao().getById(studentId);
        if (s == null) { finish(); return; }
        ((TextView)findViewById(R.id.tvName)).setText(s.name);
        ((TextView)findViewById(R.id.tvRoll)).setText("Roll: " + s.rollNumber);
        TextView tvPhone = findViewById(R.id.tvPhone);
        if (s.phone != null && !s.phone.isEmpty()) tvPhone.setText("Phone: " + s.phone);
        else tvPhone.setVisibility(View.GONE);
        TextView tvGuardian = findViewById(R.id.tvGuardian);
        if (s.guardianPhone != null && !s.guardianPhone.isEmpty()) tvGuardian.setText("Guardian: " + s.guardianPhone);
        else tvGuardian.setVisibility(View.GONE);
        TextView tvAddress = findViewById(R.id.tvAddress);
        if (s.address != null && !s.address.isEmpty()) tvAddress.setText("Address: " + s.address);
        else tvAddress.setVisibility(View.GONE);
        StudentAttendanceDao sadao = db.studentAttendanceDao();
        int present = sadao.countPresent(studentId);
        int total = sadao.countTotal(studentId);
        float pct = total > 0 ? present * 100f / total : 0;
        TextView tvAtt = findViewById(R.id.tvAttendance);
        tvAtt.setText(String.format(Locale.getDefault(), "Attendance: %.1f%% (%d/%d)", pct, present, total));
        tvAtt.setTextColor(pct >= 75 ? 0xFF15803D : pct >= 60 ? 0xFFD97706 : 0xFFDC2626);
        llMarks = findViewById(R.id.llMarks);
        findViewById(R.id.btnAddMark).setOnClickListener(v -> showAddMark());
        loadMarks();
    }
    private void loadMarks() {
        llMarks.removeAllViews();
        List<ExamMark> marks = db.examMarkDao().getByStudent(studentId, profileId);
        if (marks.isEmpty()) {
            TextView t = new TextView(this);
            t.setText("No exam marks yet.");
            t.setTextColor(0xFF475569); t.setPadding(8,8,8,8);
            llMarks.addView(t); return;
        }
        for (ExamMark m : marks) {
            View row = getLayoutInflater().inflate(R.layout.item_mark_row, llMarks, false);
            ((TextView)row.findViewById(R.id.tvExam)).setText(m.examName + " - " + m.subjectName);
            float pct = m.totalMarks > 0 ? m.marksObtained * 100f / m.totalMarks : 0;
            String label = pct >= 75 ? "Bright" : pct >= 50 ? "Average" : "Needs Improvement";
            TextView tvMarks = row.findViewById(R.id.tvMarks);
            tvMarks.setText(String.format(Locale.getDefault(), "%.0f / %.0f (%.0f%%) - %s", m.marksObtained, m.totalMarks, pct, label));
            tvMarks.setTextColor(pct >= 75 ? 0xFF15803D : pct >= 50 ? 0xFFD97706 : 0xFFDC2626);
            row.findViewById(R.id.btnDeleteMark).setOnClickListener(v -> {
                db.examMarkDao().deleteById(m.id);
                loadMarks();
            });
            llMarks.addView(row);
        }
    }
    private void showAddMark() {
        View v = getLayoutInflater().inflate(R.layout.dialog_add_mark, null);
        EditText etExam     = v.findViewById(R.id.etExam);
        EditText etSubject  = v.findViewById(R.id.etSubject);
        EditText etObtained = v.findViewById(R.id.etObtained);
        EditText etTotal    = v.findViewById(R.id.etTotal);
        new AlertDialog.Builder(this).setTitle("Add Exam Mark").setView(v)
            .setPositiveButton("Save", (d,w) -> {
                try {
                    String exam    = etExam.getText().toString().trim();
                    String subject = etSubject.getText().toString().trim();
                    float obtained = Float.parseFloat(etObtained.getText().toString().trim());
                    float total    = Float.parseFloat(etTotal.getText().toString().trim());
                    db.examMarkDao().insert(new ExamMark(studentId, profileId, subject, obtained, total, exam, MainActivity.getTodayString()));
                    loadMarks();
                } catch (Exception e) { Toast.makeText(this,"Invalid input",Toast.LENGTH_SHORT).show(); }
            }).setNegativeButton("Cancel", null).show();
    }
}
