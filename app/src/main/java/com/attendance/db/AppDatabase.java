package com.attendance.db;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.Subject;
@Database(entities = {Subject.class, AttendanceRecord.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SubjectDao subjectDao();
    public abstract AttendanceDao attendanceDao();
    private static volatile AppDatabase INSTANCE;
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE attendance_records ADD COLUMN cancelled INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE subjects ADD COLUMN displayName TEXT");
        }
    };
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "attendance_db")
                        .addMigrations(MIGRATION_1_2)
                        .allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
