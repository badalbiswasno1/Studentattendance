package com.attendance;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.db.StudentAttendanceDao;
import com.attendance.model.Student;
import com.attendance.model.UserProfile;
import java.util.*;
public class TeacherMainActivity extends AppCompatActivity {
    private AppDatabase db;
    private int profileId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        db = AppDatabase.getInstance(this);
        profileId = Prefs.getActiveProfile(this);
        UserProfile profile = db.userProfileDao().getById(profileId);
        ((TextView)findViewById(R.id.tvTeacherName)).setText(profile != null ? "Welcome, " + profile.name : "");
        ((TextView)findViewById(R.id.tvDev)).setText("Developed by Badal Biswas  |  badalbiswasno5@gmail.com");
        findViewById(R.id.btnMarkAttendance).setOnClickListener(v ->
            startActivity(new Intent(this, TeacherAttendanceActivity.class)));
        findViewById(R.id.btnStudentList).setOnClickListener(v ->
            startActivity(new Intent(this, StudentListActivity.class)));
        findViewById(R.id.btnDefaulterList).setOnClickListener(v ->
            startActivity(new Intent(this, DefaulterListActivity.class)));
        findViewById(R.id.btnExamMarks).setOnClickListener(v ->
            startActivity(new Intent(this, ExamMarksActivity.class)));
        findViewById(R.id.btnExportPdf).setOnClickListener(v -> pickDateRangeAndExport());
        findViewById(R.id.btnSwitchProfile).setOnClickListener(v -> {
            Prefs.setActiveProfile(this, -1);
            startActivity(new Intent(this, ProfileSelectActivity.class));
            finish();
        });
    }
    @Override
    protected void onResume() { super.onResume(); loadStats(); }
    private void loadStats() {
        StudentAttendanceDao sadao = db.studentAttendanceDao();
        List<Student> students = db.studentDao().getByProfile(profileId);
        String today = MainActivity.getTodayString();
        int total = students.size(), present = 0, absent = 0;
        for (Student s : students) {
            com.attendance.model.StudentAttendance rec = sadao.getRecord(s.id, today);
            if (rec != null) { if (rec.present) present++; else absent++; }
        }
        float rate = total > 0 ? present * 100f / total : 0;
        ((TextView)findViewById(R.id.tvTotalStudents)).setText(String.valueOf(total));
        ((TextView)findViewById(R.id.tvTodayPresent)).setText(String.valueOf(present));
        ((TextView)findViewById(R.id.tvTodayAbsent)).setText(String.valueOf(absent));
        ((TextView)findViewById(R.id.tvTodayRate)).setText(String.format(Locale.getDefault(), "%.0f%%", rate));
    }
    private String[] pickedStart = {MainActivity.getTodayString().substring(0,7) + "-01"};
    private String[] pickedEnd = {MainActivity.getTodayString()};
    private void pickDateRangeAndExport() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (v, y, m, d) -> {
            pickedStart[0] = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m+1, d);
            new DatePickerDialog(this, (v2, y2, m2, d2) -> {
                pickedEnd[0] = String.format(Locale.getDefault(), "%04d-%02d-%02d", y2, m2+1, d2);
                TeacherPdfExporter.exportClassReport(this, profileId, pickedStart[0], pickedEnd[0]);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}
