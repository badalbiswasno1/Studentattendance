package com.attendance;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.db.StudentAttendanceDao;
import com.attendance.model.Student;
import java.util.List;
import java.util.Locale;
public class DefaulterListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defaulter_list);
        AppDatabase db = AppDatabase.getInstance(this);
        int profileId = Prefs.getActiveProfile(this);
        LinearLayout ll = findViewById(R.id.llDefaulters);
        StudentAttendanceDao sadao = db.studentAttendanceDao();
        List<Student> students = db.studentDao().getByProfile(profileId);
        int goal = Prefs.getGoal(this);
        boolean anyFound = false;
        for (Student s : students) {
            int present = sadao.countPresent(s.id);
            int total = sadao.countTotal(s.id);
            float pct = total > 0 ? present * 100f / total : 0;
            if (pct < goal) {
                anyFound = true;
                View row = getLayoutInflater().inflate(R.layout.item_defaulter_row, ll, false);
                ((TextView)row.findViewById(R.id.tvRoll)).setText(s.rollNumber);
                ((TextView)row.findViewById(R.id.tvName)).setText(s.name);
                String pctText = String.format(Locale.getDefault(), "%.1f%%", pct);
                TextView tvPct = row.findViewById(R.id.tvPct);
                tvPct.setText(pctText);
                tvPct.setTextColor(pct < 60 ? 0xFFDC2626 : 0xFFD97706);
                ((TextView)row.findViewById(R.id.tvDetail)).setText(present + "/" + total + " classes");
                ll.addView(row);
            }
        }
        if (!anyFound) {
            TextView t = new TextView(this);
            t.setText("No defaulters. All students above " + goal + "%.");
            t.setTextColor(0xFF15803D);
            t.setTextSize(15);
            t.setPadding(16, 32, 16, 16);
            ll.addView(t);
        }
    }
}
