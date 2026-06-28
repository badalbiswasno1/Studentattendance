package com.attendance.db;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.attendance.model.DailyNote;
import java.util.List;
@Dao
public interface DailyNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DailyNote n);
    @Query("SELECT * FROM daily_notes WHERE profileId=:pid AND date=:date LIMIT 1")
    DailyNote getByDate(int pid, String date);
    @Query("SELECT * FROM daily_notes WHERE profileId=:pid ORDER BY date DESC")
    List<DailyNote> getAll(int pid);
}
