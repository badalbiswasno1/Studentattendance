package com.attendance.db;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.attendance.model.Badge;
import java.util.List;
@Dao
public interface BadgeDao {
    @Insert
    void insert(Badge b);
    @Query("SELECT * FROM badges WHERE profileId=:pid ORDER BY earnedDate DESC")
    List<Badge> getByProfile(int pid);
    @Query("SELECT COUNT(*) FROM badges WHERE profileId=:pid AND badgeKey=:key")
    int hasEarned(int pid, String key);
}
