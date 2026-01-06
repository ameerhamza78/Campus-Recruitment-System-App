package com.example.crs2025.models;

import java.io.Serializable;

public class Interview implements Serializable {
    private String interviewId;
    private String applicationId;
    private String studentId;
    private String companyId;
    private String jobTitle;
    private String studentName;
    private String companyName;
    private String interviewDate;
    private String interviewTime;
    private String location; // Standardized from 'venue'
    private String interviewType; // Added missing field
    private String status;

    public Interview() {
        // Default constructor for Firebase
    }

    // Definitive Constructor
    public Interview(String interviewId, String applicationId, String studentId, String companyId, String jobTitle, String studentName, String companyName, String interviewDate, String interviewTime, String location, String interviewType, String status) {
        this.interviewId = interviewId;
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.companyId = companyId;
        this.jobTitle = jobTitle;
        this.studentName = studentName;
        this.companyName = companyName;
        this.interviewDate = interviewDate;
        this.interviewTime = interviewTime;
        this.location = location;
        this.interviewType = interviewType;
        this.status = status;
    }

    // --- Getters and Setters ---
    public String getInterviewId() { return interviewId; }
    public void setInterviewId(String interviewId) { this.interviewId = interviewId; }
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getInterviewDate() { return interviewDate; }
    public void setInterviewDate(String interviewDate) { this.interviewDate = interviewDate; }
    public String getInterviewTime() { return interviewTime; }
    public void setInterviewTime(String interviewTime) { this.interviewTime = interviewTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getInterviewType() { return interviewType; }
    public void setInterviewType(String interviewType) { this.interviewType = interviewType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}