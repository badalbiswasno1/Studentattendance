package com.attendance.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "exam_marks")
public class ExamMark {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int studentId;
    public int profileId;
    public String subjectName;
    public float marksObtained;
    public float totalMarks;
    public String examName;
    public String date;
    public ExamMark(int studentId, int profileId, String subjectName, float marksObtained, float totalMarks, String examName, String date) {
        this.studentId = studentId;
        this.profileId = profileId;
        this.subjectName = subjectName;
        this.marksObtained = marksObtained;
        this.totalMarks = totalMarks;
        this.examName = examName;
        this.date = date;
    }
}
