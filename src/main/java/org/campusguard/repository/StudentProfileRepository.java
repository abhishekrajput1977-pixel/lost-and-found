package org.campusguard.repository;

import org.campusguard.entity.StudentProfile;
import org.campusguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByStudentId(String studentId);

    Optional<StudentProfile> findByUser(User user);

    boolean existsByStudentId(String studentId);
}
