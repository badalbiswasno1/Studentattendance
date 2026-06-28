package com.attendance.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "exam_tracker")
public class ExamTracker {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int profileId;
    public String examName;
    public String examDate;
    public String subject;
    public String notes;
    public ExamTracker(int profileId, String examName, String subject, String examDate, String notes) {
        this.profileId = profileId;
        this.examName = examName;
        this.subject = subject;
        this.examDate = examDate;
        this.notes = notes;
    }
}
