package org.campusguard.dto;

import org.campusguard.entity.enums.HandoverStatus;

import java.time.LocalDateTime;

public class HandoverResponse {

    private Long id;
    private String referenceCode;
    private Long claimId;
    private String itemName;
    private Long claimantId;
    private String claimantName;
    private String claimantStudentId;
    private Long securityOfficerId;
    private String securityOfficerName;
    private Long locationId;
    private String locationName;
    private LocalDateTime scheduledTime;
    private LocalDateTime completedAt;
    private HandoverStatus status;
    private String verificationNotes;

    public HandoverResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Long getClaimantId() { return claimantId; }
    public void setClaimantId(Long claimantId) { this.claimantId = claimantId; }

    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }

    public String getClaimantStudentId() { return claimantStudentId; }
    public void setClaimantStudentId(String claimantStudentId) { this.claimantStudentId = claimantStudentId; }

    public Long getSecurityOfficerId() { return securityOfficerId; }
    public void setSecurityOfficerId(Long securityOfficerId) { this.securityOfficerId = securityOfficerId; }

    public String getSecurityOfficerName() { return securityOfficerName; }
    public void setSecurityOfficerName(String securityOfficerName) { this.securityOfficerName = securityOfficerName; }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public HandoverStatus getStatus() { return status; }
    public void setStatus(HandoverStatus status) { this.status = status; }

    public String getVerificationNotes() { return verificationNotes; }
    public void setVerificationNotes(String verificationNotes) { this.verificationNotes = verificationNotes; }
}
