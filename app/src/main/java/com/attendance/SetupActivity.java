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
    private Button btnAdd, btnAddGeneric;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        db = AppDatabase.getInstance(this);
        subjectDao = db.subjectDao();
        llSubjects = findViewById(R.id.llSubjects);
        etSubjectName = findViewById(R.id.etSubjectName);
        btnAdd = findViewById(R.id.btnAdd);
        btnAddGeneric = findViewById(R.id.btnAddGeneric);
        btnAdd.setOnClickListener(v -> addSubject());
        btnAddGeneric.setOnClickListener(v -> {
            int count = subjectDao.getActiveCount();
            subjectDao.insert(new Subject("Period " + (count + 1), count));
            loadSubjects();
        });
        loadSubjects();
    }
    private void addSubject() {
        String name = etSubjectName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter subject name", Toast.LENGTH_SHORT).show();
            return;
        }
        int count = subjectDao.getActiveCount();
        subjectDao.insert(new Subject(name, count));
        etSubjectName.setText("");
        loadSubjects();
        Toast.makeText(this, name + " added", Toast.LENGTH_SHORT).show();
    }
    private void loadSubjects() {
        llSubjects.removeAllViews();
        List<Subject> subjects = subjectDao.getAllActive();
        if (subjects.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No subjects yet.");
            empty.setPadding(32, 24, 32, 24);
            llSubjects.addView(empty);
            return;
        }
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            View row = getLayoutInflater().inflate(R.layout.item_subject_setup, llSubjects, false);
            TextView tvName = row.findViewById(R.id.tvSubjectName);
            TextView tvOrder = row.findViewById(R.id.tvOrder);
            Button btnDelete = row.findViewById(R.id.btnDelete);
            tvName.setText(subject.name);
            tvOrder.setText("Slot " + (i + 1));
            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                    .setTitle("Delete " + subject.name + "?")
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
