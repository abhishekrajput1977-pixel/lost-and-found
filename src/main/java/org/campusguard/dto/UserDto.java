package org.campusguard.dto;

import org.campusguard.entity.enums.Role;

public class UserDto {

    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private Boolean enabled;
    private StudentProfileDto studentProfile;

    public UserDto() {}

    public UserDto(Long id, String email, String fullName, Role role, Boolean enabled, StudentProfileDto studentProfile) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.enabled = enabled;
        this.studentProfile = studentProfile;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public StudentProfileDto getStudentProfile() { return studentProfile; }
    public void setStudentProfile(StudentProfileDto studentProfile) { this.studentProfile = studentProfile; }
}
