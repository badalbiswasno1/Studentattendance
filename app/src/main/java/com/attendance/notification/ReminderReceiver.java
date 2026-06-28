package com.attendance.notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.attendance.R;
public class ReminderReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "attendance_reminder";
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "Attendance Reminder", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(ch);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Attendance Reminder")
            .setContentText("Don't forget to mark today's attendance!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true);
        nm.notify(1001, builder.build());
    }
}
