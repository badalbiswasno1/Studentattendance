package com.attendance.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "user_profiles")
public class UserProfile {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String pin;
    public String role;
    public long createdAt;
    public UserProfile(String name, String pin, String role) {
        this.name = name;
        this.pin = pin;
        this.role = role;
        this.createdAt = System.currentTimeMillis();
    }
}
