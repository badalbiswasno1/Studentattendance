package com.attendance.db;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.attendance.model.StudentAttendance;
import java.util.List;
@Dao
public interface StudentAttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StudentAttendance a);
    @Update
    void update(StudentAttendance a);
    @Query("SELECT * FROM student_attendance WHERE studentId=:studentId AND date=:date LIMIT 1")
    StudentAttendance getRecord(int studentId, String date);
    @Query("SELECT * FROM student_attendance WHERE profileId=:profileId AND date=:date")
    List<StudentAttendance> getByDate(int profileId, String date);
    @Query("SELECT COUNT(*) FROM student_attendance WHERE studentId=:studentId AND present=1")
    int countPresent(int studentId);
    @Query("SELECT COUNT(*) FROM student_attendance WHERE studentId=:studentId")
    int countTotal(int studentId);
    @Query("SELECT DISTINCT date FROM student_attendance WHERE profileId=:profileId ORDER BY date DESC")
    List<String> getAllDates(int profileId);
    @Query("SELECT COUNT(*) FROM student_attendance WHERE studentId=:sid AND present=1 AND date>=:s AND date<=:e")
    int countPresentRange(int sid, String s, String e);
    @Query("SELECT COUNT(*) FROM student_attendance WHERE studentId=:sid AND date>=:s AND date<=:e")
    int countTotalRange(int sid, String s, String e);
}
