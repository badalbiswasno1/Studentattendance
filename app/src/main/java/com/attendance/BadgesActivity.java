package com.attendance;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.db.AppDatabase;
import com.attendance.model.Badge;
import java.util.List;
public class BadgesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badges);
        AppDatabase db = AppDatabase.getInstance(this);
        int profileId = Prefs.getActiveProfile(this);
        List<Badge> newBadges = BadgeManager.checkAndAward(this, profileId);
        LinearLayout llBadges = findViewById(R.id.llBadges);
        TextView tvTotal = findViewById(R.id.tvTotal);
        List<Badge> allBadges = db.badgeDao().getByProfile(profileId);
        tvTotal.setText(allBadges.size() + " badges earned");
        if (allBadges.isEmpty()) {
            TextView t = new TextView(this);
            t.setText("No badges yet.\nMark attendance to earn your first badge!");
            t.setTextColor(0xFF64748B);
            t.setTextSize(14);
            t.setGravity(Gravity.CENTER);
            t.setPadding(16, 48, 16, 16);
            llBadges.addView(t);
            return;
        }
        LinearLayout row = null;
        for (int i = 0; i < allBadges.size(); i++) {
            if (i % 2 == 0) {
                row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                rp.setMargins(0, 0, 0, 12);
                row.setLayoutParams(rp);
                llBadges.addView(row);
            }
            Badge b = allBadges.get(i);
            android.widget.LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);
            card.setBackgroundColor(0xFFFFFFFF);
            card.setPadding(16, 20, 16, 20);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            cp.setMargins(i % 2 == 0 ? 0 : 6, 0, i % 2 == 0 ? 6 : 0, 0);
            card.setLayoutParams(cp);
            TextView tvEmoji = new TextView(this);
            tvEmoji.setText(BadgeManager.getBadgeEmoji(b.badgeKey));
            tvEmoji.setTextSize(36);
            tvEmoji.setGravity(Gravity.CENTER);
            card.addView(tvEmoji);
            TextView tvName = new TextView(this);
            tvName.setText(b.badgeName);
            tvName.setTextSize(13);
            tvName.setTextColor(0xFF0F172A);
            tvName.setTypeface(null, android.graphics.Typeface.BOLD);
            tvName.setGravity(Gravity.CENTER);
            tvName.setPadding(0, 6, 0, 2);
            card.addView(tvName);
            TextView tvDesc = new TextView(this);
            tvDesc.setText(b.badgeDesc);
            tvDesc.setTextSize(11);
            tvDesc.setTextColor(0xFF64748B);
            tvDesc.setGravity(Gravity.CENTER);
            card.addView(tvDesc);
            TextView tvDate = new TextView(this);
            tvDate.setText(b.earnedDate);
            tvDate.setTextSize(10);
            tvDate.setTextColor(0xFF94A3B8);
            tvDate.setGravity(Gravity.CENTER);
            tvDate.setPadding(0, 4, 0, 0);
            card.addView(tvDate);
            if (row != null) row.addView(card);
        }
        if (allBadges.size() % 2 != 0 && row != null) {
            LinearLayout empty = new LinearLayout(this);
            LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            ep.setMargins(6, 0, 0, 0);
            empty.setLayoutParams(ep);
            row.addView(empty);
        }
    }
}
