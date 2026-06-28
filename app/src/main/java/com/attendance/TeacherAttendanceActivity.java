package com.attendance;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.db.StudentAttendanceDao;
import com.attendance.model.Student;
import com.attendance.model.StudentAttendance;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
public class TeacherAttendanceActivity extends AppCompatActivity {
    private AppDatabase db;
    private LinearLayout llStudents;
    private TextView tvDate;
    private String selectedDate;
    private int profileId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance);
        db = AppDatabase.getInstance(this);
        profileId = Prefs.getActiveProfile(this);
        llStudents = findViewById(R.id.llStudents);
        tvDate = findViewById(R.id.tvDate);
        selectedDate = MainActivity.getTodayString();
        tvDate.setText(selectedDate);
        findViewById(R.id.btnPickDate).setOnClickListener(v -> pickDate());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveAll());
        loadStudents();
    }
    private void pickDate() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (v,y,m,d) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m+1, d);
            tvDate.setText(selectedDate);
            loadStudents();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void loadStudents() {
        llStudents.removeAllViews();
        List<Student> students = db.studentDao().getByProfile(profileId);
        StudentAttendanceDao sadao = db.studentAttendanceDao();
        for (Student s : students) {
            View row = getLayoutInflater().inflate(R.layout.item_teacher_attendance_row, llStudents, false);
            ((TextView)row.findViewById(R.id.tvRoll)).setText(s.rollNumber);
            ((TextView)row.findViewById(R.id.tvName)).setText(s.name);
            CheckBox cb = row.findViewById(R.id.cbPresent);
            StudentAttendance rec = sadao.getRecord(s.id, selectedDate);
            cb.setChecked(rec != null && rec.present);
            cb.setTag(s.id);
            llStudents.addView(row);
        }
    }
    private void saveAll() {
        StudentAttendanceDao sadao = db.studentAttendanceDao();
        for (int i = 0; i < llStudents.getChildCount(); i++) {
            View row = llStudents.getChildAt(i);
            CheckBox cb = row.findViewById(R.id.cbPresent);
            int studentId = (Integer) cb.getTag();
            boolean present = cb.isChecked();
            StudentAttendance rec = sadao.getRecord(studentId, selectedDate);
            if (rec != null) { rec.present = present; sadao.update(rec); }
            else sadao.insert(new StudentAttendance(studentId, profileId, selectedDate, present));
        }
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
    }
}
