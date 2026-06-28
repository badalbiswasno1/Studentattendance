package com.attendance.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "daily_notes")
public class DailyNote {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int profileId;
    public String date;
    public String note;
    public DailyNote(int profileId, String date, String note) {
        this.profileId = profileId;
        this.date = date;
        this.note = note;
    }
}
