package org.campusguard.service;

import org.campusguard.audit.AuditService;
import org.campusguard.dto.ClaimRequest;
import org.campusguard.dto.ClaimResponse;
import org.campusguard.dto.VerificationRequest;
import org.campusguard.dto.VerificationResponse;
import org.campusguard.entity.*;
import org.campusguard.entity.enums.*;
import org.campusguard.exception.ClaimVerificationException;
import org.campusguard.exception.DuplicateResourceException;
import org.campusguard.exception.ResourceNotFoundException;
import org.campusguard.fraud.FraudEngine;
import org.campusguard.matching.OwnershipVerifier;
import org.campusguard.notification.NotificationService;
import org.campusguard.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final ItemMatchRepository itemMatchRepository;
    private final VerificationAttemptRepository verificationAttemptRepository;
    private final HandoverRecordRepository handoverRecordRepository;
    private final OwnershipVerifier ownershipVerifier;
    private final FraudEngine fraudEngine;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public ClaimService(ClaimRepository claimRepository,
                        LostItemRepository lostItemRepository,
                        FoundItemRepository foundItemRepository,
                        ItemMatchRepository itemMatchRepository,
                        VerificationAttemptRepository verificationAttemptRepository,
                        HandoverRecordRepository handoverRecordRepository,
                        OwnershipVerifier ownershipVerifier,
                        FraudEngine fraudEngine,
                        NotificationService notificationService,
                        AuditService auditService) {
        this.claimRepository = claimRepository;
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.itemMatchRepository = itemMatchRepository;
        this.verificationAttemptRepository = verificationAttemptRepository;
        this.handoverRecordRepository = handoverRecordRepository;
        this.ownershipVerifier = ownershipVerifier;
        this.fraudEngine = fraudEngine;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional
    public ClaimResponse submitClaim(ClaimRequest request, User claimant) {
        LostItem lostItem = lostItemRepository.findById(request.getLostItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Lost item not found"));
        FoundItem foundItem = foundItemRepository.findById(request.getFoundItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found"));

        // Check if user already submitted pending claim for this item
        if (claimRepository.existsByClaimantAndFoundItem_IdAndStatusIn(claimant, foundItem.getId(),
                List.of(ClaimStatus.SUBMITTED, ClaimStatus.UNDER_VERIFICATION, ClaimStatus.MANUAL_REVIEW, ClaimStatus.APPROVED))) {
            throw new DuplicateResourceException("You have already submitted an active claim for this found item.");
        }

        ItemMatch match = null;
        if (request.getMatchId() != null) {
            match = itemMatchRepository.findById(request.getMatchId()).orElse(null);
        }

        Claim claim = new Claim();
        claim.setLostItem(lostItem);
        claim.setFoundItem(foundItem);
        claim.setMatch(match);
        claim.setClaimant(claimant);
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setReason(request.getReason());

        Claim savedClaim = claimRepository.save(claim);

        // If verification answer provided in initial claim request, verify immediately
        if (request.getVerificationAnswer() != null && !request.getVerificationAnswer().isBlank()) {
            verifyClaimOwnership(new VerificationRequest(savedClaim.getId(), request.getVerificationAnswer()), claimant);
        }

        auditService.logAction(claimant, AuditAction.CLAIM_SUBMITTED, "Claim", savedClaim.getId(), "Submitted item claim", null);

        return mapToResponse(savedClaim);
    }

    @Transactional
    public VerificationResponse verifyClaimOwnership(VerificationRequest request, User claimant) {
        Claim claim = claimRepository.findById(request.getClaimId())
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        if (!claim.getClaimant().getId().equals(claimant.getId())) {
            throw new ClaimVerificationException("Unauthorized claim verification attempt.");
        }

        LostItem lostItem = claim.getLostItem();
        String secretInfo = lostItem.getPrivateIdentifyingInfo();

        double score = ownershipVerifier.evaluateOwnership(secretInfo, request.getAnswer());
        String confidenceCategory = ownershipVerifier.classifyConfidence(score);

        VerificationAttempt attempt = new VerificationAttempt();
        attempt.setClaim(claim);
        attempt.setVerificationQuestion("Secret Identifying Characteristics");
        attempt.setClaimantAnswer(request.getAnswer());
        attempt.setMatchScore(score);
        attempt.setStatus(confidenceCategory);
        verificationAttemptRepository.save(attempt);

        claim.setVerificationScore(score);

        String message;
        if (score >= 90.0) {
            claim.setStatus(ClaimStatus.UNDER_VERIFICATION);
            message = "Verification successful! High ownership confidence (" + score + "%). Claim forwarded for security approval.";
            fraudEngine.adjustTrustScore(claimant, +5);
        } else if (score >= 70.0) {
            claim.setStatus(ClaimStatus.MANUAL_REVIEW);
            message = "Moderate confidence (" + score + "%). Claim flagged for manual administrator verification.";
        } else {
            claim.setStatus(ClaimStatus.REJECTED);
            message = "Verification failed. Low confidence score (" + score + "%). Claim rejected.";
            fraudEngine.adjustTrustScore(claimant, -5);

            // Flag suspicious activity if repeated failed attempts
            long failedCount = verificationAttemptRepository.countByClaim(claim);
            if (failedCount >= 2) {
                fraudEngine.flagSuspiciousActivity(claimant, "CLAIM", claim.getId(),
                        "Repeated failed ownership verification for found item #" + claim.getFoundItem().getId(), FraudSeverity.HIGH);
            }
        }

        claimRepository.save(claim);

        auditService.logAction(claimant, AuditAction.VERIFICATION_ATTEMPTED, "Claim", claim.getId(),
                "Ownership Verification Attempt. Score: " + score + "% (" + confidenceCategory + ")", null);

        return new VerificationResponse(claim.getId(), score, confidenceCategory, claim.getStatus().name(), message);
    }

    @Transactional
    public ClaimResponse reviewClaimByAdmin(Long claimId, boolean approve, String reason, User admin) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        claim.setReviewedAt(LocalDateTime.now());
        claim.setReviewedBy(admin);
        claim.setReason(reason);

        if (approve) {
            claim.setStatus(ClaimStatus.APPROVED);
            claim.getLostItem().setStatus(ItemStatus.CLAIMED);
            claim.getFoundItem().setStatus(ItemStatus.CLAIMED);
            lostItemRepository.save(claim.getLostItem());
            foundItemRepository.save(claim.getFoundItem());

            // Generate unique Handover reference code e.g. CG-HO-2026-000001
            String referenceCode = "CG-HO-2026-" + String.format("%06d", claim.getId());

            HandoverRecord handover = new HandoverRecord();
            handover.setReferenceCode(referenceCode);
            handover.setClaim(claim);
            handover.setClaimant(claim.getClaimant());
            handover.setLocation(claim.getFoundItem().getLocation());
            handover.setScheduledTime(LocalDateTime.now().plusDays(1));
            handover.setStatus(HandoverStatus.SCHEDULED);
            handover.setVerificationNotes("Approved by Admin " + admin.getFullName() + ". Reference: " + referenceCode);
            handoverRecordRepository.save(handover);

            notificationService.createNotification(
                    claim.getClaimant(),
                    "Claim Approved – Handover Scheduled",
                    "Your claim for " + claim.getLostItem().getItemName() + " has been approved! Handover Code: " + referenceCode,
                    NotificationType.CLAIM_UPDATE,
                    "/claims/" + claim.getId()
            );

            fraudEngine.adjustTrustScore(claim.getClaimant(), +10);
            auditService.logAction(admin, AuditAction.CLAIM_APPROVED, "Claim", claim.getId(), "Approved Claim #" + claim.getId(), null);
        } else {
            claim.setStatus(ClaimStatus.REJECTED);
            notificationService.createNotification(
                    claim.getClaimant(),
                    "Claim Update – Rejected",
                    "Your claim for " + claim.getLostItem().getItemName() + " was not approved. Reason: " + reason,
                    NotificationType.CLAIM_UPDATE,
                    "/claims/" + claim.getId()
            );
            auditService.logAction(admin, AuditAction.CLAIM_REJECTED, "Claim", claim.getId(), "Rejected Claim #" + claim.getId() + ". Reason: " + reason, null);
        }

        Claim saved = claimRepository.save(claim);
        return mapToResponse(saved);
    }

    public List<ClaimResponse> getUserClaims(User user) {
        return claimRepository.findByClaimantOrderBySubmittedAtDesc(user)
                .stream().map(this::mapToResponse).toList();
    }

    public List<ClaimResponse> getClaimsByStatus(ClaimStatus status) {
        return claimRepository.findByStatusOrderBySubmittedAtDesc(status)
                .stream().map(this::mapToResponse).toList();
    }

    public List<ClaimResponse> getAllClaims() {
        return claimRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public ClaimResponse getClaimById(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
        return mapToResponse(claim);
    }

    public ClaimResponse mapToResponse(Claim claim) {
        ClaimResponse res = new ClaimResponse();
        res.setId(claim.getId());
        res.setLostItemId(claim.getLostItem().getId());
        res.setLostItemName(claim.getLostItem().getItemName());
        res.setFoundItemId(claim.getFoundItem().getId());
        res.setFoundItemName(claim.getFoundItem().getItemName());
        if (claim.getMatch() != null) {
            res.setMatchId(claim.getMatch().getId());
        }
        res.setClaimantId(claim.getClaimant().getId());
        res.setClaimantName(claim.getClaimant().getFullName());
        res.setClaimantEmail(claim.getClaimant().getEmail());
        if (claim.getClaimant().getStudentProfile() != null) {
            res.setClaimantStudentId(claim.getClaimant().getStudentProfile().getStudentId());
        }
        res.setVerificationScore(claim.getVerificationScore());
        res.setStatus(claim.getStatus());
        res.setSubmittedAt(claim.getSubmittedAt());
        res.setReviewedAt(claim.getReviewedAt());
        if (claim.getReviewedBy() != null) {
            res.setReviewedByName(claim.getReviewedBy().getFullName());
        }
        res.setReason(claim.getReason());

        // Find associated handover reference code if available
        handoverRecordRepository.findAll().stream()
                .filter(h -> h.getClaim().getId().equals(claim.getId()))
                .findFirst()
                .ifPresent(h -> res.setHandoverReference(h.getReferenceCode()));

        return res;
    }
}
