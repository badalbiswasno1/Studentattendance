package com.attendance;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.model.UserProfile;
public class TeacherMainActivity extends AppCompatActivity {
    private AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        db = AppDatabase.getInstance(this);
        int profileId = Prefs.getActiveProfile(this);
        UserProfile profile = db.userProfileDao().getById(profileId);
        TextView tvName = findViewById(R.id.tvTeacherName);
        if (profile != null) tvName.setText("Teacher: " + profile.name);
        findViewById(R.id.btnMarkAttendance).setOnClickListener(v ->
            startActivity(new Intent(this, TeacherAttendanceActivity.class)));
        findViewById(R.id.btnStudentList).setOnClickListener(v ->
            startActivity(new Intent(this, StudentListActivity.class)));
        findViewById(R.id.btnExamMarks).setOnClickListener(v ->
            startActivity(new Intent(this, ExamMarksActivity.class)));
        findViewById(R.id.btnSwitchProfile).setOnClickListener(v -> {
            Prefs.setActiveProfile(this, -1);
            startActivity(new Intent(this, ProfileSelectActivity.class));
            finish();
        });
        TextView tvDev = findViewById(R.id.tvDev);
        tvDev.setText("Developed by Badal Biswas\nbadalbiswasno5@gmail.com");
    }
}
