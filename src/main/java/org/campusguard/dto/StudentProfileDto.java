package org.campusguard.dto;

public class StudentProfileDto {

    private String studentId;
    private String departmentCode;
    private String departmentName;
    private String year;
    private String section;
    private String phoneNumber;

    public StudentProfileDto() {}

    public StudentProfileDto(String studentId, String departmentCode, String departmentName, String year, String section, String phoneNumber) {
        this.studentId = studentId;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.year = year;
        this.section = section;
        this.phoneNumber = phoneNumber;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
