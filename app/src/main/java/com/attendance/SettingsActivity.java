package com.attendance;
import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.attendance.notification.NotificationHelper;
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch swNotification  = findViewById(R.id.swNotification);
        View btnSetTime        = findViewById(R.id.btnSetTime);
        View btnExportPdf      = findViewById(R.id.btnExportPdf);
        View btnPrivacy        = findViewById(R.id.btnPrivacy);
        View btnBackup         = findViewById(R.id.btnBackup);
        View btnReset          = findViewById(R.id.btnReset);
        TextView tvReminderTime= findViewById(R.id.tvReminderTime);
        TextView tvDev         = findViewById(R.id.tvDev);
        tvDev.setText("Developed by Badal Biswas  |  badalbiswasno5@gmail.com");
        int h = Prefs.sp(this).getInt("notif_h", 8);
        int m = Prefs.sp(this).getInt("notif_m", 0);
        tvReminderTime.setText(String.format("%d:%02d %s", h > 12 ? h-12 : h, m, h >= 12 ? "PM" : "AM"));
        boolean notifOn = Prefs.sp(this).getBoolean("notif_on", false);
        swNotification.setChecked(notifOn);
        swNotification.setOnCheckedChangeListener((btn, checked) -> {
            Prefs.sp(this).edit().putBoolean("notif_on", checked).apply();
            if (checked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
                NotificationHelper.scheduleDailyReminder(this,
                    Prefs.sp(this).getInt("notif_h", 8), Prefs.sp(this).getInt("notif_m", 0));
                Toast.makeText(this, "Reminder enabled", Toast.LENGTH_SHORT).show();
            } else {
                NotificationHelper.cancelReminder(this);
                Toast.makeText(this, "Reminder disabled", Toast.LENGTH_SHORT).show();
            }
        });
        btnSetTime.setOnClickListener(v -> {
            int ch = Prefs.sp(this).getInt("notif_h", 8);
            int cm = Prefs.sp(this).getInt("notif_m", 0);
            new TimePickerDialog(this, (tp, hour, min) -> {
                Prefs.sp(this).edit().putInt("notif_h", hour).putInt("notif_m", min).apply();
                tvReminderTime.setText(String.format("%d:%02d %s", hour > 12 ? hour-12 : hour, min, hour >= 12 ? "PM" : "AM"));
                if (swNotification.isChecked()) NotificationHelper.scheduleDailyReminder(this, hour, min);
            }, ch, cm, false).show();
        });
        btnExportPdf.setOnClickListener(v -> {
            String today = MainActivity.getTodayString();
            PdfExporter.exportReport(this, today.substring(0,7) + "-01", today);
        });
        btnPrivacy.setOnClickListener(v ->
            startActivity(new Intent(this, PrivacyPolicyActivity.class)));
        btnBackup.setOnClickListener(v ->
            new AlertDialog.Builder(this)
                .setTitle("Data Storage")
                .setMessage("All your data is stored locally on this device only.\n\nNo data is sent to any server.\n\nTo backup: use PDF Export regularly.\n\nIf you uninstall the app, all data will be permanently deleted.")
                .setPositiveButton("OK", null).show());
        btnReset.setOnClickListener(v ->
            new AlertDialog.Builder(this)
                .setTitle("Reset All Data?")
                .setMessage("This will permanently delete ALL attendance records, subjects, notes, and exam data. This cannot be undone.")
                .setPositiveButton("Reset", (d, w) -> {
                    new Thread(() -> {
                        AppDatabase db = AppDatabase.getInstance(this);
                        db.clearAllTables();
                        runOnUiThread(() -> {
                            Toast.makeText(this, "All data deleted", Toast.LENGTH_SHORT).show();
                            Prefs.setActiveProfile(this, -1);
                            startActivity(new Intent(this, ProfileSelectActivity.class));
                            finish();
                        });
                    }).start();
                })
                .setNegativeButton("Cancel", null).show());
    }
}
