package com.attendance.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "subjects")
public class Subject {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public int orderIndex;
    public boolean isActive;
    public Subject(String name, int orderIndex) {
        this.name = name;
        this.orderIndex = orderIndex;
        this.isActive = true;
    }
}
