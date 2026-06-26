package com.attendance.db;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.Subject;
@Database(entities = {Subject.class, AttendanceRecord.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SubjectDao subjectDao();
    public abstract AttendanceDao attendanceDao();
    private static volatile AppDatabase INSTANCE;
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "attendance_db").allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
