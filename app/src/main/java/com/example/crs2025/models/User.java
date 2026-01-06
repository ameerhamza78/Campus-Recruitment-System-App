package com.example.crs2025.models;

public class User {
    private String userId;
    private String name;
    private String email;
    private String address;
    private String role;
    private String enrollmentNo;
    private String branch;
    private String institute;
    private String skills;
    private String cgpa;

    public User() {
        // Default constructor required for Firebase
    }

    // Constructor for Student
    public User(String userId, String name, String email, String address, String role, String enrollmentNo, String branch, String institute, String skills, String cgpa) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.address = address;
        this.role = role;
        this.enrollmentNo = enrollmentNo;
        this.branch = branch;
        this.institute = institute;
        this.skills = skills;
        this.cgpa = cgpa;
    }

    // Constructor for Company
    public User(String userId, String name, String email, String address, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.address = address;
        this.role = role;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEnrollmentNo() { return enrollmentNo; }
    public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getInstitute() { return institute; }
    public void setInstitute(String institute) { this.institute = institute; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getCgpa() { return cgpa; }
    public void setCgpa(String cgpa) { this.cgpa = cgpa; }
}
