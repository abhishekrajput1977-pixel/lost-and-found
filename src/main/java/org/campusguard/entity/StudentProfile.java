package org.campusguard.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true, length = 30)
    private String studentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String year;

    @Column(length = 10)
    private String section;

    @Column(length = 20)
    private String phoneNumber;

    public StudentProfile() {}

    public StudentProfile(Long id, User user, String studentId, Department department, String year, String section, String phoneNumber) {
        this.id = id;
        this.user = user;
        this.studentId = studentId;
        this.department = department;
        this.year = year;
        this.section = section;
        this.phoneNumber = phoneNumber;
    }

    public StudentProfile(User user, String studentId, Department department, String year, String section, String phoneNumber) {
        this.user = user;
        this.studentId = studentId;
        this.department = department;
        this.year = year;
        this.section = section;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
