package com.attendance.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "student_attendance")
public class StudentAttendance {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int studentId;
    public int profileId;
    public String date;
    public boolean present;
    public StudentAttendance(int studentId, int profileId, String date, boolean present) {
        this.studentId = studentId;
        this.profileId = profileId;
        this.date = date;
        this.present = present;
    }
}
