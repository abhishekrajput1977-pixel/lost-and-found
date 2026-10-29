package org.campusguard.service;

import org.campusguard.audit.AuditService;
import org.campusguard.dto.HandoverRequest;
import org.campusguard.dto.HandoverResponse;
import org.campusguard.entity.*;
import org.campusguard.entity.enums.AuditAction;
import org.campusguard.entity.enums.HandoverStatus;
import org.campusguard.entity.enums.ItemStatus;
import org.campusguard.entity.enums.NotificationType;
import org.campusguard.exception.ResourceNotFoundException;
import org.campusguard.exception.ValidationException;
import org.campusguard.notification.NotificationService;
import org.campusguard.repository.HandoverRecordRepository;
import org.campusguard.repository.ItemMatchRepository;
import org.campusguard.repository.LostItemRepository;
import org.campusguard.repository.FoundItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HandoverService {

    private final HandoverRecordRepository handoverRecordRepository;
    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final ItemMatchRepository itemMatchRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public HandoverService(HandoverRecordRepository handoverRecordRepository,
                           LostItemRepository lostItemRepository,
                           FoundItemRepository foundItemRepository,
                           ItemMatchRepository itemMatchRepository,
                           NotificationService notificationService,
                           AuditService auditService) {
        this.handoverRecordRepository = handoverRecordRepository;
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.itemMatchRepository = itemMatchRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional
    public HandoverResponse completeHandover(Long handoverId, String verificationNotes, User securityOfficer) {
        HandoverRecord record = handoverRecordRepository.findById(handoverId)
                .orElseThrow(() -> new ResourceNotFoundException("Handover record not found"));

        if (record.getStatus() == HandoverStatus.COMPLETED) {
            throw new ValidationException("This handover has already been completed.");
        }

        record.setStatus(HandoverStatus.COMPLETED);
        record.setCompletedAt(LocalDateTime.now());
        record.setSecurityOfficer(securityOfficer);
        if (verificationNotes != null && !verificationNotes.isBlank()) {
            record.setVerificationNotes(record.getVerificationNotes() + " | Security Verification: " + verificationNotes);
        }

        // Update items to RETURNED
        Claim claim = record.getClaim();
        LostItem lostItem = claim.getLostItem();
        FoundItem foundItem = claim.getFoundItem();

        lostItem.setStatus(ItemStatus.RETURNED);
        foundItem.setStatus(ItemStatus.RETURNED);

        lostItemRepository.save(lostItem);
        foundItemRepository.save(foundItem);

        HandoverRecord savedRecord = handoverRecordRepository.save(record);

        // Send confirmation notification
        notificationService.createNotification(
                claim.getClaimant(),
                "Item Successfully Returned!",
                "Your item " + lostItem.getItemName() + " has been verified and returned to you by Campus Security. Reference: " + record.getReferenceCode(),
                NotificationType.ITEM_RETURNED,
                "/claims/" + claim.getId()
        );

        auditService.logAction(securityOfficer, AuditAction.HANDOVER_COMPLETED, "HandoverRecord", record.getId(),
                "Completed Item Handover. Reference: " + record.getReferenceCode(), null);
        auditService.logAction(securityOfficer, AuditAction.ITEM_RETURNED, "LostItem", lostItem.getId(),
                "Item Returned to Owner: " + lostItem.getItemName(), null);

        return mapToResponse(savedRecord);
    }

    public HandoverResponse getByReferenceCode(String referenceCode) {
        HandoverRecord record = handoverRecordRepository.findByReferenceCode(referenceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Handover reference code not found: " + referenceCode));
        return mapToResponse(record);
    }

    public List<HandoverResponse> getScheduledHandovers() {
        return handoverRecordRepository.findByStatusOrderByScheduledTimeAsc(HandoverStatus.SCHEDULED)
                .stream().map(this::mapToResponse).toList();
    }

    public List<HandoverResponse> getAllHandovers() {
        return handoverRecordRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public HandoverResponse mapToResponse(HandoverRecord record) {
        HandoverResponse res = new HandoverResponse();
        res.setId(record.getId());
        res.setReferenceCode(record.getReferenceCode());
        res.setClaimId(record.getClaim().getId());
        res.setItemName(record.getClaim().getLostItem().getItemName());
        res.setClaimantId(record.getClaimant().getId());
        res.setClaimantName(record.getClaimant().getFullName());
        if (record.getClaimant().getStudentProfile() != null) {
            res.setClaimantStudentId(record.getClaimant().getStudentProfile().getStudentId());
        }
        if (record.getSecurityOfficer() != null) {
            res.setSecurityOfficerId(record.getSecurityOfficer().getId());
            res.setSecurityOfficerName(record.getSecurityOfficer().getFullName());
        }
        res.setLocationId(record.getLocation().getId());
        res.setLocationName(record.getLocation().getName());
        res.setScheduledTime(record.getScheduledTime());
        res.setCompletedAt(record.getCompletedAt());
        res.setStatus(record.getStatus());
        res.setVerificationNotes(record.getVerificationNotes());
        return res;
    }
}
