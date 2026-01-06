package com.example.crs2025.models;

import java.io.Serializable;

public class Job implements Serializable {
    private String jobId;
    private String companyId;
    private String companyName;
    private String jobTitle;
    private String skills;
    private String cgpa;
    private String jobType;
    private boolean internshipRequired;
    private String location; // Ensure this field exists

    public Job() {
        // Default constructor required for Firebase
    }

    // Definitive constructor with all 9 fields
    public Job(String jobId, String companyId, String companyName, String jobTitle, String skills, String cgpa, String jobType, boolean internshipRequired, String location) {
        this.jobId = jobId;
        this.companyId = companyId;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.skills = skills;
        this.cgpa = cgpa;
        this.jobType = jobType;
        this.internshipRequired = internshipRequired;
        this.location = location;
    }

    // --- Getters and Setters ---
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getCgpa() { return cgpa; }
    public void setCgpa(String cgpa) { this.cgpa = cgpa; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public boolean isInternshipRequired() { return internshipRequired; }
    public void setInternshipRequired(boolean internshipRequired) { this.internshipRequired = internshipRequired; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}