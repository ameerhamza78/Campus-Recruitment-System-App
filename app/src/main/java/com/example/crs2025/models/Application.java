package com.example.crs2025.models;

import java.io.Serializable;

public class Application implements Serializable {
    private String applicationId;
    private String studentId;
    private String jobId;
    private String companyId;
    private String jobTitle;
    private String companyName;
    private String fullName; // Student's full name
    private String email;    // Student's email
    private String reasonToApply;
    private String resumeLink;
    private String status;

    public Application() {
        // Default constructor
    }

    // Corrected, simpler constructor
    public Application(String applicationId, String studentId, String jobId, String companyId, String jobTitle, String companyName, String fullName, String email, String reasonToApply, String resumeLink, String status) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.jobId = jobId;
        this.companyId = companyId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.fullName = fullName;
        this.email = email;
        this.reasonToApply = reasonToApply;
        this.resumeLink = resumeLink;
        this.status = status;
    }

    // --- Getters and Setters ---
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getReasonToApply() { return reasonToApply; }
    public void setReasonToApply(String reasonToApply) { this.reasonToApply = reasonToApply; }
    public String getResumeLink() { return resumeLink; }
    public void setResumeLink(String resumeLink) { this.resumeLink = resumeLink; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}