package com.example.crs2025.models;

public class Feedback {
    private String feedbackId;
    private String authorId;
    private String authorName;
    private String authorRole; // "student" or "company"
    private String feedbackText;
    private long timestamp;

    public Feedback() {
        // Default constructor required for calls to DataSnapshot.getValue(Feedback.class)
    }

    public Feedback(String feedbackId, String authorId, String authorName, String authorRole, String feedbackText, long timestamp) {
        this.feedbackId = feedbackId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.feedbackText = feedbackText;
        this.timestamp = timestamp;
    }

    // --- Getters and Setters ---

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}