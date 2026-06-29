package com.attendance;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.attendance.db.AppDatabase;
import com.attendance.db.StudentAttendanceDao;
import com.attendance.model.ExamMark;
import android.content.Intent;
import android.net.Uri;
import com.attendance.model.Student;
import java.util.*;
public class ExamMarksActivity extends AppCompatActivity {
    private AppDatabase db;
    private int profileId;
    private LinearLayout llContent;
    private TextView tvClassAvg, tvTopStudent, tvNeedHelp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_marks);
        db = AppDatabase.getInstance(this);
        profileId = Prefs.getActiveProfile(this);
        llContent   = findViewById(R.id.llContent);
        tvClassAvg  = findViewById(R.id.tvClassAvg);
        tvTopStudent= findViewById(R.id.tvTopStudent);
        tvNeedHelp  = findViewById(R.id.tvNeedHelp);
        loadAll();
    }
    private void loadAll() {
        llContent.removeAllViews();
        List<Student> students = db.studentDao().getByProfile(profileId);
        StudentAttendanceDao sadao = db.studentAttendanceDao();
        if (students.isEmpty()) {
            TextView t = new TextView(this);
            t.setText("No students added yet.\nGo to Student List to add students.");
            t.setTextColor(0xFF64748B); t.setTextSize(14);
            t.setGravity(Gravity.CENTER); t.setPadding(16, 48, 16, 16);
            llContent.addView(t);
            tvClassAvg.setText("Class Avg: —");
            tvTopStudent.setText("Top: —");
            tvNeedHelp.setText("Needs Help: —");
            return;
        }
        List<StudentScore> scores = new ArrayList<>();
        for (Student s : students) {
            List<ExamMark> marks = db.examMarkDao().getByStudent(s.id, profileId);
            int present = sadao.countPresent(s.id);
            int total = sadao.countTotal(s.id);
            float attPct = total > 0 ? present * 100f / total : 0;
            float markPct = 0;
            float totalObt = 0, totalMax = 0;
            for (ExamMark m : marks) { totalObt += m.marksObtained; totalMax += m.totalMarks; }
            if (totalMax > 0) markPct = totalObt * 100f / totalMax;
            scores.add(new StudentScore(s, marks, attPct, markPct, totalObt, totalMax));
        }
        scores.sort((a, b) -> Float.compare(b.markPct, a.markPct));
        float sumPct = 0; int countWithMarks = 0;
        String topName = "—", needName = "—";
        for (StudentScore sc : scores) {
            if (sc.totalMax > 0) { sumPct += sc.markPct; countWithMarks++; }
        }
        float classAvg = countWithMarks > 0 ? sumPct / countWithMarks : 0;
        if (!scores.isEmpty() && scores.get(0).totalMax > 0) topName = scores.get(0).student.name;
        for (int i = scores.size()-1; i >= 0; i--) {
            if (scores.get(i).totalMax > 0) { needName = scores.get(i).student.name; break; }
        }
        tvClassAvg.setText(String.format(java.util.Locale.getDefault(), "Class Avg: %.1f%%", classAvg));
        tvTopStudent.setText("Top: " + topName);
        tvNeedHelp.setText("Needs Help: " + needName);
        String[] categories = {"Top Students (75%+)", "Average (50–74%)", "Needs Improvement (<50%)", "No Marks Yet"};
        int[][] ranges = {{75,101},{50,75},{0,50},{-1,-1}};
        for (int cat = 0; cat < 4; cat++) {
            List<StudentScore> group = new ArrayList<>();
            for (StudentScore sc : scores) {
                if (cat == 3 && sc.totalMax == 0) group.add(sc);
                else if (cat != 3 && sc.totalMax > 0 && sc.markPct >= ranges[cat][0] && sc.markPct < ranges[cat][1]) group.add(sc);
            }
            if (group.isEmpty()) continue;
            TextView tvCat = new TextView(this);
            tvCat.setText(categories[cat]);
            tvCat.setTextSize(13); tvCat.setTypeface(null, android.graphics.Typeface.BOLD);
            int[] catColors = {0xFF15803D, 0xFFD97706, 0xFFDC2626, 0xFF64748B};
            tvCat.setTextColor(catColors[cat]);
            tvCat.setPadding(0, 20, 0, 8);
            llContent.addView(tvCat);
            for (StudentScore sc : group) {
                addStudentCard(sc, sadao);
            }
        }
    }
    private void addStudentCard(StudentScore sc, StudentAttendanceDao sadao) {
        Student s = sc.student;
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cp.setMargins(0, 0, 0, 12);
        card.setLayoutParams(cp);
        card.setRadius(32f);
        card.setCardElevation(4f);
        card.setCardBackgroundColor(0xFFFFFFFF);
        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(20, 16, 20, 16);
        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setGravity(Gravity.CENTER_VERTICAL);
        TextView tvRoll = new TextView(this);
        tvRoll.setText(s.rollNumber);
        tvRoll.setTextSize(13); tvRoll.setTextColor(0xFF1A56DB);
        tvRoll.setTypeface(null, android.graphics.Typeface.BOLD);
        tvRoll.setMinWidth(120);
        TextView tvName = new TextView(this);
        tvName.setText(s.name);
        tvName.setTextSize(15); tvName.setTextColor(0xFF0F172A);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvName.setLayoutParams(nameLp);
        TextView tvMark = new TextView(this);
        if (sc.totalMax > 0) {
            tvMark.setText(String.format(java.util.Locale.getDefault(), "%.0f%%", sc.markPct));
            tvMark.setTextColor(sc.markPct >= 75 ? 0xFF15803D : sc.markPct >= 50 ? 0xFFD97706 : 0xFFDC2626);
        } else {
            tvMark.setText("No marks");
            tvMark.setTextColor(0xFF94A3B8);
        }
        tvMark.setTextSize(15); tvMark.setTypeface(null, android.graphics.Typeface.BOLD);
        row1.addView(tvRoll); row1.addView(tvName); row1.addView(tvMark);
        inner.addView(row1);
        TextView tvAtt = new TextView(this);
        tvAtt.setText(String.format(java.util.Locale.getDefault(),
            "Attendance: %.0f%%  |  Marks: %.0f / %.0f", sc.attPct, sc.totalObt, sc.totalMax));
        tvAtt.setTextSize(12); tvAtt.setTextColor(0xFF64748B);
        tvAtt.setPadding(0, 4, 0, 0);
        inner.addView(tvAtt);
        if (sc.marks != null && !sc.marks.isEmpty()) {
            for (ExamMark m : sc.marks) {
                float pct = m.totalMarks > 0 ? m.marksObtained * 100f / m.totalMarks : 0;
                TextView tvM = new TextView(this);
                tvM.setText(String.format(java.util.Locale.getDefault(),
                    "  %s — %s: %.0f/%.0f (%.0f%%)", m.examName, m.subjectName, m.marksObtained, m.totalMarks, pct));
                tvM.setTextSize(12);
                tvM.setTextColor(pct >= 75 ? 0xFF15803D : pct >= 50 ? 0xFFD97706 : 0xFFDC2626);
                tvM.setPadding(0, 2, 0, 2);
                inner.addView(tvM);
            }
        }
        if (s.phone != null && !s.phone.isEmpty()) {
            LinearLayout contactRow = new LinearLayout(this);
            contactRow.setOrientation(LinearLayout.HORIZONTAL);
            contactRow.setPadding(0, 8, 0, 0);
            Button btnCall = new Button(this);
            btnCall.setText("Call Student");
            btnCall.setTextSize(11);
            LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            blp.setMargins(0, 0, 6, 0);
            btnCall.setLayoutParams(blp);
            btnCall.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1A56DB));
            btnCall.setTextColor(0xFFFFFFFF);
            btnCall.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + s.phone));
                startActivity(intent);
            });
            contactRow.addView(btnCall);
            if (s.guardianPhone != null && !s.guardianPhone.isEmpty()) {
                Button btnGuardian = new Button(this);
                btnGuardian.setText("Call Guardian");
                btnGuardian.setTextSize(11);
                LinearLayout.LayoutParams glp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                btnGuardian.setLayoutParams(glp);
                btnGuardian.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF7C3AED));
                btnGuardian.setTextColor(0xFFFFFFFF);
                btnGuardian.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + s.guardianPhone));
                    startActivity(intent);
                });
                contactRow.addView(btnGuardian);
            }
            inner.addView(contactRow);
        }
        Button btnDetail = new Button(this);
        btnDetail.setText("View Full Details");
        btnDetail.setTextSize(12);
        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dlp.setMargins(0, 8, 0, 0);
        btnDetail.setLayoutParams(dlp);
        btnDetail.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF1F5F9));
        btnDetail.setTextColor(0xFF0F172A);
        btnDetail.setOnClickListener(v -> {
            Intent i = new Intent(this, StudentDetailActivity.class);
            i.putExtra("studentId", s.id);
            startActivity(i);
        });
        inner.addView(btnDetail);
        card.addView(inner);
        llContent.addView(card);
    }
    private static class StudentScore {
        Student student;
        List<ExamMark> marks;
        float attPct, markPct, totalObt, totalMax;
        StudentScore(Student s, List<ExamMark> m, float a, float mp, float to, float tm) {
            student=s; marks=m; attPct=a; markPct=mp; totalObt=to; totalMax=tm;
        }
    }
}
