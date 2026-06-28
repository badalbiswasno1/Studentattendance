package com.attendance;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;
import com.attendance.db.AppDatabase;
import com.attendance.db.AttendanceDao;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.Subject;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
public class PdfExporter {
    public static void exportReport(Context context, String startDate, String endDate) {
        AppDatabase db = AppDatabase.getInstance(context);
        AttendanceDao dao = db.attendanceDao();
        List<Subject> subjects = db.subjectDao().getAll();
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = doc.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint title = new Paint();
        title.setColor(Color.parseColor("#1A56DB"));
        title.setTextSize(22);
        title.setFakeBoldText(true);
        canvas.drawText("Attendance Report", 40, 60, title);
        Paint sub = new Paint();
        sub.setColor(Color.DKGRAY);
        sub.setTextSize(12);
        canvas.drawText("Period: " + startDate + " to " + endDate, 40, 85, sub);
        canvas.drawText("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()), 40, 100, sub);
        Paint line = new Paint();
        line.setColor(Color.LTGRAY);
        canvas.drawLine(40, 110, 555, 110, line);
        Paint header = new Paint();
        header.setColor(Color.BLACK);
        header.setTextSize(13);
        header.setFakeBoldText(true);
        canvas.drawText("Subject", 40, 135, header);
        canvas.drawText("Present", 280, 135, header);
        canvas.drawText("Total", 380, 135, header);
        canvas.drawText("%", 470, 135, header);
        canvas.drawLine(40, 142, 555, 142, line);
        Paint row = new Paint();
        row.setTextSize(12);
        int y = 162;
        int totalP = 0, totalT = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            List<String> dates = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);
            while (!cal.getTime().after(end)) {
                dates.add(sdf.format(cal.getTime()));
                cal.add(Calendar.DATE, 1);
            }
            for (Subject s : subjects) {
                if (!s.isActive) continue;
                int present = 0;
                for (String d : dates) {
                    AttendanceRecord rec = dao.getRecord(d, s.id);
                    if (rec != null && rec.present && !rec.cancelled) present++;
                }
                int total = dates.size();
                float pct = total > 0 ? present * 100f / total : 0;
                row.setColor(pct >= 75 ? Color.parseColor("#15803D") : Color.parseColor("#DC2626"));
                canvas.drawText(s.name, 40, y, row);
                row.setColor(Color.BLACK);
                canvas.drawText(String.valueOf(present), 280, y, row);
                canvas.drawText(String.valueOf(total), 380, y, row);
                canvas.drawText(String.format(Locale.getDefault(), "%.1f%%", pct), 470, y, row);
                totalP += present; totalT += total;
                y += 24;
            }
        } catch (Exception e) { e.printStackTrace(); }
        canvas.drawLine(40, y, 555, y, line);
        y += 20;
        Paint totalPaint = new Paint();
        totalPaint.setTextSize(13); totalPaint.setFakeBoldText(true);
        float overall = totalT > 0 ? totalP * 100f / totalT : 0;
        totalPaint.setColor(overall >= 75 ? Color.parseColor("#15803D") : Color.parseColor("#DC2626"));
        canvas.drawText("OVERALL", 40, y, totalPaint);
        canvas.drawText(String.valueOf(totalP), 280, y, totalPaint);
        canvas.drawText(String.valueOf(totalT), 380, y, totalPaint);
        canvas.drawText(String.format(Locale.getDefault(), "%.1f%%", overall), 470, y, totalPaint);
        y += 40;
        Paint devPaint = new Paint();
        devPaint.setColor(Color.GRAY); devPaint.setTextSize(10);
        canvas.drawText("Developed by Badal Biswas | badalbiswasno5@gmail.com", 40, y, devPaint);
        doc.finishPage(page);
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AttendanceReports");
            dir.mkdirs();
            String fname = "attendance_" + startDate + "_to_" + endDate + ".pdf";
            File f = new File(dir, fname);
            doc.writeTo(new FileOutputStream(f));
            Toast.makeText(context, "PDF saved: Downloads/AttendanceReports/" + fname, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        doc.close();
    }
}
