package com.attendance.db;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.attendance.model.AttendanceRecord;
import java.util.List;
@Dao
public interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AttendanceRecord record);
    @Update
    void update(AttendanceRecord record);
    @Query("SELECT * FROM attendance_records WHERE date = :date AND subjectId = :subjectId LIMIT 1")
    AttendanceRecord getRecord(String date, int subjectId);
    @Query("SELECT * FROM attendance_records WHERE date >= :startDate AND date <= :endDate")
    List<AttendanceRecord> getByDateRange(String startDate, String endDate);
    @Query("SELECT COUNT(*) FROM attendance_records WHERE date >= :s AND date <= :e AND present=1 AND cancelled=0")
    int countPresent(String s, String e);
    @Query("SELECT COUNT(*) FROM attendance_records WHERE date >= :s AND date <= :e AND cancelled=0")
    int countTotal(String s, String e);
    @Query("SELECT DISTINCT date FROM attendance_records WHERE cancelled=0 ORDER BY date DESC")
    List<String> getAllDates();
    @Query("SELECT DISTINCT date FROM attendance_records WHERE present=1 AND cancelled=0 ORDER BY date DESC")
    List<String> getPresentDates();
    @Query("SELECT * FROM attendance_records WHERE date = :date AND cancelled=0")
    List<AttendanceRecord> getByDate(String date);
    @Query("SELECT subjectId, SUM(CASE WHEN present=1 AND cancelled=0 THEN 1 ELSE 0 END) as presentCount, SUM(CASE WHEN cancelled=0 THEN 1 ELSE 0 END) as totalCount FROM attendance_records WHERE date >= :s AND date <= :e GROUP BY subjectId")
    List<SubjectAttendanceSummary> getSubjectSummary(String s, String e);
}
