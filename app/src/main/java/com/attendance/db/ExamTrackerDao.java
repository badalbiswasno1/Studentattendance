package com.attendance.db;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.attendance.model.ExamTracker;
import java.util.List;
@Dao
public interface ExamTrackerDao {
    @Insert
    long insert(ExamTracker e);
    @Query("SELECT * FROM exam_tracker WHERE profileId=:pid ORDER BY examDate ASC")
    List<ExamTracker> getByProfile(int pid);
    @Query("DELETE FROM exam_tracker WHERE id=:id")
    void deleteById(int id);
}
