package org.campusguard.repository;

import org.campusguard.entity.HandoverRecord;
import org.campusguard.entity.User;
import org.campusguard.entity.enums.HandoverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HandoverRecordRepository extends JpaRepository<HandoverRecord, Long> {

    Optional<HandoverRecord> findByReferenceCode(String referenceCode);

    List<HandoverRecord> findByStatusOrderByScheduledTimeAsc(HandoverStatus status);

    List<HandoverRecord> findByClaimantOrderByScheduledTimeDesc(User claimant);

    long countByStatus(HandoverStatus status);
}
