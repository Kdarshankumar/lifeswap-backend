package com.lifeswapplus.server.model;

public class MatchResult {

    private Long userId;
    private String userName;
    private String userEmail;

    private Long theirSkillId;
    private String theirSkillName;
    private String theirSkillLevel;
    private String theirSkillCategory;

    private String yourWantSkillName;

    private int score;

    public MatchResult() {}

    public MatchResult(
            Long userId,
            String userName,
            String userEmail,
            Long theirSkillId,
            String theirSkillName,
            String theirSkillLevel,
            String theirSkillCategory,
            String yourWantSkillName,
            int score
    ) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.theirSkillId = theirSkillId;
        this.theirSkillName = theirSkillName;
        this.theirSkillLevel = theirSkillLevel;
        this.theirSkillCategory = theirSkillCategory;
        this.yourWantSkillName = yourWantSkillName;
        this.score = score;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Long getTheirSkillId() { return theirSkillId; }
    public void setTheirSkillId(Long theirSkillId) { this.theirSkillId = theirSkillId; }

    public String getTheirSkillName() { return theirSkillName; }
    public void setTheirSkillName(String theirSkillName) { this.theirSkillName = theirSkillName; }

    public String getTheirSkillLevel() { return theirSkillLevel; }
    public void setTheirSkillLevel(String theirSkillLevel) { this.theirSkillLevel = theirSkillLevel; }

    public String getTheirSkillCategory() { return theirSkillCategory; }
    public void setTheirSkillCategory(String theirSkillCategory) { this.theirSkillCategory = theirSkillCategory; }

    public String getYourWantSkillName() { return yourWantSkillName; }
    public void setYourWantSkillName(String yourWantSkillName) { this.yourWantSkillName = yourWantSkillName; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
