package com.attendance;
import android.content.Context;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;
import com.attendance.db.AppDatabase;
import com.attendance.db.StudentAttendanceDao;
import com.attendance.model.Student;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
public class TeacherPdfExporter {
    public static void exportClassReport(Context context, int profileId, String startDate, String endDate) {
        AppDatabase db = AppDatabase.getInstance(context);
        StudentAttendanceDao sadao = db.studentAttendanceDao();
        List<Student> students = db.studentDao().getByProfile(profileId);
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = doc.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint title = new Paint();
        title.setColor(Color.parseColor("#7C3AED")); title.setTextSize(22); title.setFakeBoldText(true);
        canvas.drawText("Class Attendance Report", 40, 60, title);
        Paint sub = new Paint(); sub.setColor(Color.DKGRAY); sub.setTextSize(12);
        canvas.drawText("Period: " + startDate + " to " + endDate, 40, 82, sub);
        canvas.drawText("Total Students: " + students.size(), 40, 98, sub);
        Paint line = new Paint(); line.setColor(Color.LTGRAY);
        canvas.drawLine(40, 108, 555, 108, line);
        Paint hdr = new Paint(); hdr.setTextSize(12); hdr.setFakeBoldText(true); hdr.setColor(Color.BLACK);
        canvas.drawText("Roll", 40, 128, hdr);
        canvas.drawText("Name", 100, 128, hdr);
        canvas.drawText("Present", 340, 128, hdr);
        canvas.drawText("Total", 410, 128, hdr);
        canvas.drawText("%", 470, 128, hdr);
        canvas.drawText("Status", 505, 128, hdr);
        canvas.drawLine(40, 134, 555, 134, line);
        Paint rowP = new Paint(); rowP.setTextSize(11);
        int y = 152;
        for (Student s : students) {
            int present = sadao.countPresentRange(s.id, startDate, endDate);
            int total = sadao.countTotalRange(s.id, startDate, endDate);
            float pct = total > 0 ? present * 100f / total : 0;
            String status = pct >= 75 ? "OK" : "LOW";
            rowP.setColor(pct >= 75 ? Color.parseColor("#15803D") : Color.parseColor("#DC2626"));
            canvas.drawText(s.rollNumber != null ? s.rollNumber : "-", 40, y, rowP);
            rowP.setColor(Color.BLACK);
            String name = s.name != null && s.name.length() > 28 ? s.name.substring(0,28) : s.name;
            canvas.drawText(name != null ? name : "-", 100, y, rowP);
            canvas.drawText(String.valueOf(present), 340, y, rowP);
            canvas.drawText(String.valueOf(total), 410, y, rowP);
            rowP.setColor(pct >= 75 ? Color.parseColor("#15803D") : Color.parseColor("#DC2626"));
            canvas.drawText(String.format(Locale.getDefault(), "%.0f%%", pct), 470, y, rowP);
            canvas.drawText(status, 505, y, rowP);
            y += 20;
            if (y > 800) break;
        }
        Paint devP = new Paint(); devP.setColor(Color.GRAY); devP.setTextSize(9);
        canvas.drawText("Developed by Badal Biswas | badalbiswasno5@gmail.com", 40, 830, devP);
        doc.finishPage(page);
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AttendanceReports");
            dir.mkdirs();
            String fname = "class_report_" + startDate + "_to_" + endDate + ".pdf";
            File f = new File(dir, fname);
            doc.writeTo(new FileOutputStream(f));
            Toast.makeText(context, "PDF saved: Downloads/AttendanceReports/" + fname, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        doc.close();
    }
}
