package com.attendance.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "badges")
public class Badge {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int profileId;
    public String badgeKey;
    public String badgeName;
    public String badgeDesc;
    public String earnedDate;
    public Badge(int profileId, String badgeKey, String badgeName, String badgeDesc, String earnedDate) {
        this.profileId = profileId;
        this.badgeKey = badgeKey;
        this.badgeName = badgeName;
        this.badgeDesc = badgeDesc;
        this.earnedDate = earnedDate;
    }
}
