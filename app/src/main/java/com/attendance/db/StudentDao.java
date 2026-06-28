package com.attendance.db;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.attendance.model.Student;
import java.util.List;
@Dao
public interface StudentDao {
    @Insert
    long insert(Student s);
    @Update
    void update(Student s);
    @Query("SELECT * FROM students WHERE profileId=:profileId AND isActive=1 ORDER BY rollNumber ASC")
    List<Student> getByProfile(int profileId);
    @Query("SELECT * FROM students WHERE id=:id LIMIT 1")
    Student getById(int id);
    @Query("SELECT COUNT(*) FROM students WHERE profileId=:profileId AND isActive=1")
    int countByProfile(int profileId);
}
