package com.attendance;
import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.attendance.notification.NotificationHelper;
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch swNotification = findViewById(R.id.swNotification);
        Button btnSetTime     = findViewById(R.id.btnSetTime);
        Button btnExportPdf   = findViewById(R.id.btnExportPdf);
        Button btnBackup      = findViewById(R.id.btnBackup);
        TextView tvDev        = findViewById(R.id.tvDev);
        tvDev.setText("Developed by Badal Biswas\nbadalbiswasno5@gmail.com");
        boolean notifOn = Prefs.sp(this).getBoolean("notif_on", false);
        swNotification.setChecked(notifOn);
        swNotification.setOnCheckedChangeListener((btn, checked) -> {
            Prefs.sp(this).edit().putBoolean("notif_on", checked).apply();
            if (checked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
                }
                int h = Prefs.sp(this).getInt("notif_h", 8);
                int m = Prefs.sp(this).getInt("notif_m", 0);
                NotificationHelper.scheduleDailyReminder(this, h, m);
                Toast.makeText(this, "Reminder set!", Toast.LENGTH_SHORT).show();
            } else {
                NotificationHelper.cancelReminder(this);
                Toast.makeText(this, "Reminder cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        btnSetTime.setOnClickListener(v -> {
            int h = Prefs.sp(this).getInt("notif_h", 8);
            int m = Prefs.sp(this).getInt("notif_m", 0);
            new TimePickerDialog(this, (tp, hour, min) -> {
                Prefs.sp(this).edit().putInt("notif_h", hour).putInt("notif_m", min).apply();
                if (swNotification.isChecked()) {
                    NotificationHelper.scheduleDailyReminder(this, hour, min);
                }
                Toast.makeText(this, "Reminder time set: " + hour + ":" + String.format("%02d", min), Toast.LENGTH_SHORT).show();
            }, h, m, true).show();
        });
        btnExportPdf.setOnClickListener(v -> {
            String today = MainActivity.getTodayString();
            String start = today.substring(0, 7) + "-01";
            PdfExporter.exportReport(this, start, today);
        });
        btnBackup.setOnClickListener(v ->
            Toast.makeText(this, "Data is stored locally on this device. Uninstalling will erase data.", Toast.LENGTH_LONG).show());
    }
}
