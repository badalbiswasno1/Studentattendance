package com.attendance.notification;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
public class NotificationHelper {
    public static void scheduleDailyReminder(Context context, int hour, int minute) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }
    public static void cancelReminder(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        am.cancel(pi);
    }
}
