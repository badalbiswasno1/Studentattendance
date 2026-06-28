package com.attendance.db;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.attendance.model.*;
@Database(entities = {Subject.class, AttendanceRecord.class, UserProfile.class, Student.class, StudentAttendance.class, ExamMark.class, ExamTracker.class, DailyNote.class, Timetable.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SubjectDao subjectDao();
    public abstract AttendanceDao attendanceDao();
    public abstract UserProfileDao userProfileDao();
    public abstract StudentDao studentDao();
    public abstract StudentAttendanceDao studentAttendanceDao();
    public abstract ExamMarkDao examMarkDao();
    public abstract ExamTrackerDao examTrackerDao();
    public abstract DailyNoteDao dailyNoteDao();
    public abstract TimetableDao timetableDao();
    private static volatile AppDatabase INSTANCE;
    static final Migration M1_2 = new Migration(1, 2) {
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE attendance_records ADD COLUMN cancelled INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE subjects ADD COLUMN displayName TEXT");
        }
    };
    static final Migration M2_3 = new Migration(2, 3) {
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS user_profiles (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT, pin TEXT, role TEXT, createdAt INTEGER NOT NULL)");
            db.execSQL("CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, profileId INTEGER NOT NULL, rollNumber TEXT, name TEXT, phone TEXT, isActive INTEGER NOT NULL)");
            db.execSQL("CREATE TABLE IF NOT EXISTS student_attendance (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, studentId INTEGER NOT NULL, profileId INTEGER NOT NULL, date TEXT, present INTEGER NOT NULL)");
            db.execSQL("CREATE TABLE IF NOT EXISTS exam_marks (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, studentId INTEGER NOT NULL, profileId INTEGER NOT NULL, subjectName TEXT, marksObtained REAL NOT NULL, totalMarks REAL NOT NULL, examName TEXT, date TEXT)");
        }
    };
    static final Migration M3_4 = new Migration(3, 4) {
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS exam_tracker (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, profileId INTEGER NOT NULL, examName TEXT, examDate TEXT, subject TEXT, notes TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS daily_notes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, profileId INTEGER NOT NULL, date TEXT, note TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS timetable (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, profileId INTEGER NOT NULL, dayOfWeek INTEGER NOT NULL, slotNumber INTEGER NOT NULL, subjectName TEXT)");
            db.execSQL("ALTER TABLE students ADD COLUMN guardianPhone TEXT");
            db.execSQL("ALTER TABLE students ADD COLUMN address TEXT");
        }
    };
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "attendance_db")
                        .addMigrations(M1_2, M2_3, M3_4).allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
