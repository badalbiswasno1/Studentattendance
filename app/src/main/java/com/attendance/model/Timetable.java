package com.attendance.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "timetable")
public class Timetable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int profileId;
    public int dayOfWeek;
    public int slotNumber;
    public String subjectName;
    public Timetable(int profileId, int dayOfWeek, int slotNumber, String subjectName) {
        this.profileId = profileId;
        this.dayOfWeek = dayOfWeek;
        this.slotNumber = slotNumber;
        this.subjectName = subjectName;
    }
}
