package com.attendance;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.db.SubjectDao;
import com.attendance.model.Subject;
import java.util.List;
public class SetupActivity extends AppCompatActivity {
    private AppDatabase db;
    private SubjectDao subjectDao;
    private LinearLayout llSubjects;
    private EditText etSubjectName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        db = AppDatabase.getInstance(this);
        subjectDao = db.subjectDao();
        llSubjects = findViewById(R.id.llSubjects);
        etSubjectName = findViewById(R.id.etSubjectName);
        findViewById(R.id.btnAdd).setOnClickListener(v -> addSubject());
        findViewById(R.id.btnAddGeneric).setOnClickListener(v -> {
            int count = subjectDao.getActiveCount();
            subjectDao.insert(new Subject("Period " + (count + 1), count));
            loadSubjects();
        });
        loadSubjects();
    }
    private void addSubject() {
        String name = etSubjectName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) { Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show(); return; }
        subjectDao.insert(new Subject(name, subjectDao.getActiveCount()));
        etSubjectName.setText("");
        loadSubjects();
    }
    private void loadSubjects() {
        llSubjects.removeAllViews();
        List<Subject> subjects = subjectDao.getAllActive();
        if (subjects.isEmpty()) {
            TextView t = new TextView(this);
            t.setText("No subjects yet.");
            t.setPadding(32, 24, 32, 24);
            t.setTextColor(0xFF0F172A);
            llSubjects.addView(t);
            return;
        }
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            View row = getLayoutInflater().inflate(R.layout.item_subject_setup, llSubjects, false);
            ((TextView) row.findViewById(R.id.tvOrder)).setText("Slot " + (i + 1));
            ((TextView) row.findViewById(R.id.tvSubjectName)).setText(subject.getDisplayName());
            row.findViewById(R.id.btnEdit).setOnClickListener(v -> {
                EditText et = new EditText(this);
                et.setText(subject.getDisplayName());
                et.setTextColor(0xFF0F172A);
                new AlertDialog.Builder(this)
                    .setTitle("Edit Subject Name")
                    .setView(et)
                    .setPositiveButton("Save", (d, w) -> {
                        String newName = et.getText().toString().trim();
                        if (!TextUtils.isEmpty(newName)) {
                            subject.name = newName;
                            subject.displayName = newName;
                            subjectDao.update(subject);
                            loadSubjects();
                        }
                    })
                    .setNegativeButton("Cancel", null).show();
            });
            row.findViewById(R.id.btnDelete).setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                    .setTitle("Delete " + subject.getDisplayName() + "?")
                    .setPositiveButton("Delete", (d, w) -> {
                        subject.isActive = false;
                        subjectDao.update(subject);
                        loadSubjects();
                    })
                    .setNegativeButton("Cancel", null).show();
            });
            llSubjects.addView(row);
        }
    }
}
