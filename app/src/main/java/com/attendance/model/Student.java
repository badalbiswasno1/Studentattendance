package com.attendance.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "students")
public class Student {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int profileId;
    public String rollNumber;
    public String name;
    public String phone;
    public String guardianPhone;
    public String address;
    public boolean isActive;
    public Student(int profileId, String rollNumber, String name, String phone) {
        this.profileId = profileId;
        this.rollNumber = rollNumber;
        this.name = name;
        this.phone = phone;
        this.isActive = true;
    }
}
