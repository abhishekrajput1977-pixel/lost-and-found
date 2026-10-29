package org.campusguard.dto;

public class VerificationResponse {

    private Long claimId;
    private Double verificationScore;
    private String confidenceCategory; // HIGH_CONFIDENCE (90-100), MANUAL_REVIEW (70-89), SUSPICIOUS_REJECTED (<70)
    private String status;
    private String message;

    public VerificationResponse() {}

    public VerificationResponse(Long claimId, Double verificationScore, String confidenceCategory, String status, String message) {
        this.claimId = claimId;
        this.verificationScore = verificationScore;
        this.confidenceCategory = confidenceCategory;
        this.status = status;
        this.message = message;
    }

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public Double getVerificationScore() { return verificationScore; }
    public void setVerificationScore(Double verificationScore) { this.verificationScore = verificationScore; }

    public String getConfidenceCategory() { return confidenceCategory; }
    public void setConfidenceCategory(String confidenceCategory) { this.confidenceCategory = confidenceCategory; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
