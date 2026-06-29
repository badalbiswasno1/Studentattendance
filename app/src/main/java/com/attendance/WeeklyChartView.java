package com.attendance;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
public class WeeklyChartView extends View {
    private float[] values = new float[7];
    private String[] labels = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
    private Paint barPaint, textPaint, bgPaint, linePaint;
    public WeeklyChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public WeeklyChartView(Context context) {
        super(context);
        init();
    }
    private void init() {
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(Color.parseColor("#1A56DB"));
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#64748B"));
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#EFF6FF"));
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#E2E8F0"));
        linePaint.setStrokeWidth(2f);
    }
    public void setValues(float[] vals, String[] lbls) {
        this.values = vals;
        this.labels = lbls;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();
        int n = values.length;
        if (n == 0) return;
        int labelH = 50, topPad = 20;
        int chartH = h - labelH - topPad;
        float barW = (float) w / n;
        float maxVal = 100f;
        canvas.drawLine(0, topPad + chartH, w, topPad + chartH, linePaint);
        for (int i = 0; i < n; i++) {
            float barH = (values[i] / maxVal) * chartH;
            float left = i * barW + barW * 0.15f;
            float right = (i + 1) * barW - barW * 0.15f;
            float top = topPad + chartH - barH;
            float bottom = topPad + chartH;
            if (values[i] >= 75) barPaint.setColor(Color.parseColor("#15803D"));
            else if (values[i] >= 50) barPaint.setColor(Color.parseColor("#D97706"));
            else if (values[i] > 0) barPaint.setColor(Color.parseColor("#DC2626"));
            else barPaint.setColor(Color.parseColor("#E2E8F0"));
            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rect, 8, 8, barPaint);
            if (values[i] > 0) {
                Paint pctPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                pctPaint.setColor(Color.parseColor("#0F172A"));
                pctPaint.setTextSize(22f);
                pctPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText((int)values[i] + "%", i * barW + barW / 2, top - 8, pctPaint);
            }
            canvas.drawText(labels[i], i * barW + barW / 2, h - 10, textPaint);
        }
    }
}
