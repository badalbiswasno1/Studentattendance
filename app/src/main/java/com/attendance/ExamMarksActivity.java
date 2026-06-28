package com.attendance;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.model.ExamMark;
import com.attendance.model.Student;
import java.util.List;
import java.util.Locale;
public class ExamMarksActivity extends AppCompatActivity {
    private AppDatabase db;
    private LinearLayout llMarks;
    private int profileId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_marks);
        db = AppDatabase.getInstance(this);
        profileId = Prefs.getActiveProfile(this);
        llMarks = findViewById(R.id.llMarks);
        loadMarks();
    }
    private void loadMarks() {
        llMarks.removeAllViews();
        List<Student> students = db.studentDao().getByProfile(profileId);
        for (Student s : students) {
            List<ExamMark> marks = db.examMarkDao().getByStudent(s.id, profileId);
            if (marks.isEmpty()) continue;
            TextView tvHeader = new TextView(this);
            tvHeader.setText(s.rollNumber + " - " + s.name);
            tvHeader.setTextColor(0xFF0F172A); tvHeader.setTextSize(15);
            tvHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            tvHeader.setPadding(8,16,8,4);
            llMarks.addView(tvHeader);
            for (ExamMark m : marks) {
                View row = getLayoutInflater().inflate(R.layout.item_mark_row, llMarks, false);
                ((TextView)row.findViewById(R.id.tvExam)).setText(m.examName + " — " + m.subjectName);
                float pct = m.totalMarks > 0 ? m.marksObtained * 100f / m.totalMarks : 0;
                String label = pct >= 75 ? "Bright" : pct >= 50 ? "Average" : "Needs Improvement";
                ((TextView)row.findViewById(R.id.tvMarks)).setText(
                    String.format(Locale.getDefault(), "%.0f/%.0f (%.0f%%) — %s", m.marksObtained, m.totalMarks, pct, label));
                int color = pct >= 75 ? 0xFF15803D : pct >= 50 ? 0xFFD97706 : 0xFFDC2626;
                ((TextView)row.findViewById(R.id.tvMarks)).setTextColor(color);
                row.findViewById(R.id.btnDeleteMark).setVisibility(View.GONE);
                llMarks.addView(row);
            }
        }
    }
}
