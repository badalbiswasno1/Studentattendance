package com.attendance;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.model.UserProfile;
import java.util.List;
public class ProfileSelectActivity extends AppCompatActivity {
    private AppDatabase db;
    private LinearLayout llProfiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_select);
        db = AppDatabase.getInstance(this);
        llProfiles = findViewById(R.id.llProfiles);
        findViewById(R.id.btnAddStudent).setOnClickListener(v -> showAddProfile("student"));
        findViewById(R.id.btnAddTeacher).setOnClickListener(v -> showAddProfile("teacher"));
        loadProfiles();
    }
    @Override
    protected void onResume() { super.onResume(); loadProfiles(); }
    private void loadProfiles() {
        llProfiles.removeAllViews();
        List<UserProfile> profiles = db.userProfileDao().getAll();
        if (profiles.isEmpty()) {
            TextView t = new TextView(this);
            t.setText("No profiles yet. Add one below.");
            t.setTextColor(0xFF475569); t.setPadding(16,16,16,16);
            llProfiles.addView(t); return;
        }
        for (UserProfile p : profiles) {
            View row = getLayoutInflater().inflate(R.layout.item_profile_row, llProfiles, false);
            ((TextView)row.findViewById(R.id.tvName)).setText(p.name);
            String roleLabel = "teacher".equals(p.role) ? "Teacher" : "Student";
            ((TextView)row.findViewById(R.id.tvRole)).setText(roleLabel);
            row.findViewById(R.id.btnEnter).setOnClickListener(v -> enterProfile(p));
            row.findViewById(R.id.btnDelete).setOnClickListener(v ->
                new AlertDialog.Builder(this).setTitle("Delete " + p.name + "?")
                    .setPositiveButton("Delete", (d,w) -> { db.userProfileDao().deleteById(p.id); loadProfiles(); })
                    .setNegativeButton("Cancel", null).show());
            llProfiles.addView(row);
        }
    }
    private void enterProfile(UserProfile p) {
        if (p.pin != null && !p.pin.isEmpty()) {
            EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            et.setHint("Enter PIN");
            new AlertDialog.Builder(this).setTitle("Enter PIN for " + p.name)
                .setView(et)
                .setPositiveButton("Enter", (d,w) -> {
                    if (et.getText().toString().equals(p.pin)) { openProfile(p); }
                    else Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("Cancel", null).show();
        } else { openProfile(p); }
    }
    private void openProfile(UserProfile p) {
        Prefs.setActiveProfile(this, p.id);
        Prefs.setMode(this, p.role);
        if ("teacher".equals(p.role)) {
            startActivity(new Intent(this, TeacherMainActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
    private void showAddProfile(String role) {
        View v = getLayoutInflater().inflate(R.layout.dialog_add_profile, null);
        EditText etName = v.findViewById(R.id.etName);
        EditText etPin = v.findViewById(R.id.etPin);
        new AlertDialog.Builder(this)
            .setTitle("Add " + ("teacher".equals(role) ? "Teacher" : "Student") + " Profile")
            .setView(v)
            .setPositiveButton("Add", (d,w) -> {
                String name = etName.getText().toString().trim();
                String pin = etPin.getText().toString().trim();
                if (name.isEmpty()) { Toast.makeText(this,"Enter name",Toast.LENGTH_SHORT).show(); return; }
                db.userProfileDao().insert(new UserProfile(name, pin, role));
                loadProfiles();
            }).setNegativeButton("Cancel", null).show();
    }
}
