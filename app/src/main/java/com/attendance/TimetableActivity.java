package com.attendance;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.model.Timetable;
import java.util.List;
public class TimetableActivity extends AppCompatActivity {
    private AppDatabase db;
    private int profileId;
    private LinearLayout llTimetable;
    private final String[] DAYS = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        db = AppDatabase.getInstance(this);
        profileId = Prefs.getActiveProfile(this);
        llTimetable = findViewById(R.id.llTimetable);
        findViewById(R.id.btnAddSlot).setOnClickListener(v -> showAddSlot());
        loadTimetable();
    }
    @Override
    protected void onResume() { super.onResume(); loadTimetable(); }
    private void loadTimetable() {
        llTimetable.removeAllViews();
        for (int day = 1; day <= 6; day++) {
            List<Timetable> slots = db.timetableDao().getByDay(profileId, day);
            if (slots.isEmpty()) continue;
            TextView tvDay = new TextView(this);
            tvDay.setText(DAYS[day]);
            tvDay.setTextColor(0xFF1A56DB);
            tvDay.setTextSize(15);
            tvDay.setTypeface(null, android.graphics.Typeface.BOLD);
            tvDay.setPadding(8, 20, 8, 6);
            llTimetable.addView(tvDay);
            for (Timetable t : slots) {
                View row = getLayoutInflater().inflate(R.layout.item_timetable_row, llTimetable, false);
                ((TextView)row.findViewById(R.id.tvSlot)).setText("Period " + t.slotNumber);
                ((TextView)row.findViewById(R.id.tvSubject)).setText(t.subjectName);
                row.findViewById(R.id.btnDelete).setOnClickListener(v -> {
                    db.timetableDao().deleteById(t.id);
                    loadTimetable();
                });
                llTimetable.addView(row);
            }
        }
    }
    private void showAddSlot() {
        View v = getLayoutInflater().inflate(R.layout.dialog_add_timetable, null);
        Spinner spinDay = v.findViewById(R.id.spinDay);
        Spinner spinSlot = v.findViewById(R.id.spinSlot);
        EditText etSubject = v.findViewById(R.id.etSubject);
        spinDay.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            new String[]{"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"}));
        spinSlot.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            new String[]{"1","2","3","4","5","6","7","8"}));
        new AlertDialog.Builder(this).setTitle("Add Timetable Slot").setView(v)
            .setPositiveButton("Add", (d, w) -> {
                int day = spinDay.getSelectedItemPosition() + 1;
                int slot = spinSlot.getSelectedItemPosition() + 1;
                String sub = etSubject.getText().toString().trim();
                if (sub.isEmpty()) { Toast.makeText(this,"Enter subject",Toast.LENGTH_SHORT).show(); return; }
                db.timetableDao().insert(new Timetable(profileId, day, slot, sub));
                loadTimetable();
            }).setNegativeButton("Cancel", null).show();
    }
}
