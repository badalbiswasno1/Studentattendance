package com.attendance.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "attendance_records")
public class AttendanceRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String date;
    public int subjectId;
    public boolean present;
    public long createdAt;
    public AttendanceRecord(String date, int subjectId, boolean present) {
        this.date = date;
        this.subjectId = subjectId;
        this.present = present;
        this.createdAt = System.currentTimeMillis();
    }
}
