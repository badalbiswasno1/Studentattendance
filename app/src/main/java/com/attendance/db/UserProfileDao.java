package com.attendance.db;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.attendance.model.UserProfile;
import java.util.List;
@Dao
public interface UserProfileDao {
    @Insert
    long insert(UserProfile p);
    @Update
    void update(UserProfile p);
    @Query("SELECT * FROM user_profiles ORDER BY createdAt ASC")
    List<UserProfile> getAll();
    @Query("SELECT * FROM user_profiles WHERE id=:id LIMIT 1")
    UserProfile getById(int id);
    @Query("DELETE FROM user_profiles WHERE id=:id")
    void deleteById(int id);
}
