package com.attendance;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class PrivacyPolicyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        ((TextView)findViewById(R.id.tvPolicy)).setText(
            "PRIVACY POLICY\n\nLast Updated: June 2026\n\n" +
            "1. DATA COLLECTION\n" +
            "This app stores all data locally on your device only. We do not collect, transmit, or share any personal data to external servers.\n\n" +
            "2. DATA STORED LOCALLY\n" +
            "- Your name and profile information\n" +
            "- Attendance records\n" +
            "- Subject names\n" +
            "- PIN (stored locally, not encrypted)\n" +
            "- Exam marks\n\n" +
            "3. DATA SECURITY\n" +
            "All data is stored in the device's local SQLite database. Uninstalling the app will permanently delete all data.\n\n" +
            "4. NOTIFICATIONS\n" +
            "If enabled, the app sends local daily reminders. No internet connection is required.\n\n" +
            "5. PERMISSIONS USED\n" +
            "- Storage: For PDF export\n" +
            "- Notifications: For daily reminders\n" +
            "- Alarm: For scheduling reminders\n\n" +
            "6. THIRD PARTY SERVICES\n" +
            "This app does not use any third-party analytics, advertising, or tracking services.\n\n" +
            "7. CHILDREN\n" +
            "This app is suitable for students of all ages. No personal data leaves the device.\n\n" +
            "8. CONTACT\n" +
            "For questions or concerns:\n" +
            "Badal Biswas\n" +
            "badalbiswasno5@gmail.com\n\n" +
            "9. CHANGES\n" +
            "Any updates to this policy will be reflected in the app update notes."
        );
    }
}
