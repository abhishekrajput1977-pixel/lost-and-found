package org.campusguard.repository;

import org.campusguard.entity.Claim;
import org.campusguard.entity.User;
import org.campusguard.entity.enums.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByClaimantOrderBySubmittedAtDesc(User claimant);

    List<Claim> findByStatusOrderBySubmittedAtDesc(ClaimStatus status);

    long countByStatus(ClaimStatus status);

    boolean existsByClaimantAndFoundItem_IdAndStatusIn(User claimant, Long foundItemId, List<ClaimStatus> statuses);
}
