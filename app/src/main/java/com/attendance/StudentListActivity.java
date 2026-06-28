package com.attendance;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.db.StudentAttendanceDao;
import com.attendance.model.Student;
import java.util.List;
public class StudentListActivity extends AppCompatActivity {
    private AppDatabase db;
    private LinearLayout llStudents;
    private int profileId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        db = AppDatabase.getInstance(this);
        profileId = Prefs.getActiveProfile(this);
        llStudents = findViewById(R.id.llStudents);
        findViewById(R.id.btnAddStudent).setOnClickListener(v -> showAddStudent());
        loadStudents();
    }
    @Override
    protected void onResume() { super.onResume(); loadStudents(); }
    private void loadStudents() {
        llStudents.removeAllViews();
        List<Student> students = db.studentDao().getByProfile(profileId);
        StudentAttendanceDao sadao = db.studentAttendanceDao();
        if (students.isEmpty()) {
            TextView t = new TextView(this);
            t.setText("No students yet. Add below.");
            t.setTextColor(0xFF0F172A); t.setPadding(16,16,16,16);
            llStudents.addView(t); return;
        }
        for (Student s : students) {
            View row = getLayoutInflater().inflate(R.layout.item_student_row, llStudents, false);
            ((TextView)row.findViewById(R.id.tvRoll)).setText(s.rollNumber);
            ((TextView)row.findViewById(R.id.tvName)).setText(s.name);
            int present = sadao.countPresent(s.id);
            int total = sadao.countTotal(s.id);
            float pct = total > 0 ? present * 100f / total : 0;
            TextView tvPct = row.findViewById(R.id.tvPct);
            tvPct.setText(String.format(java.util.Locale.getDefault(), "%.0f%% (%d/%d)", pct, present, total));
            tvPct.setTextColor(pct >= 75 ? 0xFF15803D : pct >= 60 ? 0xFFD97706 : 0xFFDC2626);
            row.setOnClickListener(v -> {
                Intent i = new Intent(this, StudentDetailActivity.class);
                i.putExtra("studentId", s.id);
                startActivity(i);
            });
            row.findViewById(R.id.btnDelete).setOnClickListener(v ->
                new AlertDialog.Builder(this).setTitle("Delete " + s.name + "?")
                    .setPositiveButton("Delete", (d,w) -> {
                        s.isActive = false;
                        db.studentDao().update(s);
                        loadStudents();
                    }).setNegativeButton("Cancel", null).show());
            llStudents.addView(row);
        }
    }
    private void showAddStudent() {
        View v = getLayoutInflater().inflate(R.layout.dialog_add_student, null);
        EditText etRoll     = v.findViewById(R.id.etRoll);
        EditText etName     = v.findViewById(R.id.etName);
        EditText etPhone    = v.findViewById(R.id.etPhone);
        EditText etGuardian = v.findViewById(R.id.etGuardian);
        EditText etAddress  = v.findViewById(R.id.etAddress);
        new AlertDialog.Builder(this).setTitle("Add Student").setView(v)
            .setPositiveButton("Add", (d,w) -> {
                String roll = etRoll.getText().toString().trim();
                String name = etName.getText().toString().trim();
                if (TextUtils.isEmpty(roll) || TextUtils.isEmpty(name)) {
                    Toast.makeText(this,"Roll and Name required",Toast.LENGTH_SHORT).show(); return;
                }
                Student student = new Student(profileId, roll, name, etPhone.getText().toString().trim());
                student.guardianPhone = etGuardian.getText().toString().trim();
                student.address = etAddress.getText().toString().trim();
                db.studentDao().insert(student);
                loadStudents();
            }).setNegativeButton("Cancel", null).show();
    }
}
