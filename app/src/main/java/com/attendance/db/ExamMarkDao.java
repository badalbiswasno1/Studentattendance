package com.attendance.db;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.attendance.model.ExamMark;
import java.util.List;
@Dao
public interface ExamMarkDao {
    @Insert
    long insert(ExamMark m);
    @Update
    void update(ExamMark m);
    @Query("SELECT * FROM exam_marks WHERE studentId=:studentId AND profileId=:profileId ORDER BY date DESC")
    List<ExamMark> getByStudent(int studentId, int profileId);
    @Query("SELECT * FROM exam_marks WHERE profileId=:profileId ORDER BY date DESC")
    List<ExamMark> getByProfile(int profileId);
    @Query("DELETE FROM exam_marks WHERE id=:id")
    void deleteById(int id);
}
