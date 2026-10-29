package org.campusguard.repository;

import org.campusguard.entity.Claim;
import org.campusguard.entity.VerificationAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerificationAttemptRepository extends JpaRepository<VerificationAttempt, Long> {

    List<VerificationAttempt> findByClaimOrderByVerifiedAtDesc(Claim claim);

    long countByClaim(Claim claim);
}
