package com.attendance.db;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.attendance.model.Timetable;
import java.util.List;
@Dao
public interface TimetableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Timetable t);
    @Query("SELECT * FROM timetable WHERE profileId=:pid AND dayOfWeek=:day ORDER BY slotNumber ASC")
    List<Timetable> getByDay(int pid, int day);
    @Query("SELECT * FROM timetable WHERE profileId=:pid ORDER BY dayOfWeek, slotNumber ASC")
    List<Timetable> getAll(int pid);
    @Query("DELETE FROM timetable WHERE id=:id")
    void deleteById(int id);
    @Query("DELETE FROM timetable WHERE profileId=:pid AND dayOfWeek=:day")
    void deleteByDay(int pid, int day);
}
