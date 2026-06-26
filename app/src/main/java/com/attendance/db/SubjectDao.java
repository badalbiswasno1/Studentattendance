package com.attendance.db;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.attendance.model.Subject;
import java.util.List;
@Dao
public interface SubjectDao {
    @Insert
    long insert(Subject subject);
    @Update
    void update(Subject subject);
    @Delete
    void delete(Subject subject);
    @Query("SELECT * FROM subjects WHERE isActive = 1 ORDER BY orderIndex ASC")
    List<Subject> getAllActive();
    @Query("SELECT * FROM subjects ORDER BY orderIndex ASC")
    List<Subject> getAll();
    @Query("SELECT COUNT(*) FROM subjects WHERE isActive = 1")
    int getActiveCount();
}
