package org.campusguard.dto;

import org.campusguard.entity.enums.FraudFlagStatus;
import org.campusguard.entity.enums.FraudSeverity;

import java.time.LocalDateTime;

public class FraudFlagDto {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String itemType;
    private Long itemId;
    private String reason;
    private FraudSeverity severity;
    private FraudFlagStatus status;
    private LocalDateTime createdAt;
    private String reviewedByName;

    public FraudFlagDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public FraudSeverity getSeverity() { return severity; }
    public void setSeverity(FraudSeverity severity) { this.severity = severity; }

    public FraudFlagStatus getStatus() { return status; }
    public void setStatus(FraudFlagStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getReviewedByName() { return reviewedByName; }
    public void setReviewedByName(String reviewedByName) { this.reviewedByName = reviewedByName; }
}
