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
    @Query("SELECT * FROM attendance_records WHERE date = :date")
    List<AttendanceRecord> getByDate(String date);
    @Query("SELECT * FROM attendance_records WHERE date = :date AND subjectId = :subjectId LIMIT 1")
    AttendanceRecord getRecord(String date, int subjectId);
    @Query("SELECT * FROM attendance_records WHERE date >= :startDate AND date <= :endDate")
    List<AttendanceRecord> getByDateRange(String startDate, String endDate);
    @Query("SELECT COUNT(*) FROM attendance_records WHERE date >= :startDate AND date <= :endDate AND present = 1")
    int countPresent(String startDate, String endDate);
    @Query("SELECT COUNT(*) FROM attendance_records WHERE date >= :startDate AND date <= :endDate")
    int countTotal(String startDate, String endDate);
    @Query("SELECT DISTINCT date FROM attendance_records ORDER BY date DESC")
    List<String> getAllDates();
    @Query("SELECT subjectId, SUM(CASE WHEN present=1 THEN 1 ELSE 0 END) as presentCount, COUNT(*) as totalCount FROM attendance_records WHERE date >= :startDate AND date <= :endDate GROUP BY subjectId")
    List<SubjectAttendanceSummary> getSubjectSummary(String startDate, String endDate);
}
