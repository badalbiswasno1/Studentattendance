package com.attendance;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.db.StudentAttendanceDao;
import com.attendance.model.Student;
import com.attendance.model.StudentAttendance;
import com.attendance.model.UserProfile;
import java.util.List;
import java.util.Locale;
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
        TextView tvName = findViewById(R.id.tvTeacherName);
        if (profile != null) tvName.setText("Welcome, " + profile.name);
        findViewById(R.id.btnMarkAttendance).setOnClickListener(v ->
            startActivity(new Intent(this, TeacherAttendanceActivity.class)));
        findViewById(R.id.btnStudentList).setOnClickListener(v ->
            startActivity(new Intent(this, StudentListActivity.class)));
        findViewById(R.id.btnExamMarks).setOnClickListener(v ->
            startActivity(new Intent(this, ExamMarksActivity.class)));
        findViewById(R.id.btnDefaulterList).setOnClickListener(v ->
            startActivity(new Intent(this, DefaulterListActivity.class)));
        findViewById(R.id.btnSwitchProfile).setOnClickListener(v -> {
            Prefs.setActiveProfile(this, -1);
            startActivity(new Intent(this, ProfileSelectActivity.class));
            finish();
        });
        ((TextView)findViewById(R.id.tvDev)).setText("Developed by Badal Biswas  badalbiswasno5@gmail.com");
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
    }
    private void loadStats() {
        StudentAttendanceDao sadao = db.studentAttendanceDao();
        List<Student> students = db.studentDao().getByProfile(profileId);
        String today = MainActivity.getTodayString();
        int totalStudents = students.size();
        int todayPresent = 0, todayAbsent = 0;
        for (Student s : students) {
            StudentAttendance rec = sadao.getRecord(s.id, today);
            if (rec != null) {
                if (rec.present) todayPresent++;
                else todayAbsent++;
            }
        }
        float rate = totalStudents > 0 ? todayPresent * 100f / totalStudents : 0;
        ((TextView)findViewById(R.id.tvTotalStudents)).setText(String.valueOf(totalStudents));
        ((TextView)findViewById(R.id.tvTodayPresent)).setText(String.valueOf(todayPresent));
        ((TextView)findViewById(R.id.tvTodayAbsent)).setText(String.valueOf(todayAbsent));
        ((TextView)findViewById(R.id.tvTodayRate)).setText(String.format(Locale.getDefault(), "%.0f%%", rate));
    }
}
