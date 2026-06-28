package com.attendance.db;
import androidx.room.Dao;
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
    @Query("SELECT * FROM subjects WHERE isActive=1 ORDER BY orderIndex ASC")
    List<Subject> getAllActive();
    @Query("SELECT * FROM subjects ORDER BY orderIndex ASC")
    List<Subject> getAll();
    @Query("SELECT COUNT(*) FROM subjects WHERE isActive=1")
    int getActiveCount();
    @Query("SELECT * FROM subjects WHERE id=:id LIMIT 1")
    Subject getById(int id);
}
