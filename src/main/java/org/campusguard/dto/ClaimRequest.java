package org.campusguard.dto;

import jakarta.validation.constraints.NotNull;

public class ClaimRequest {

    @NotNull(message = "Lost item ID is required")
    private Long lostItemId;

    @NotNull(message = "Found item ID is required")
    private Long foundItemId;

    private Long matchId;
    private String verificationAnswer;
    private String reason;

    public ClaimRequest() {}

    public Long getLostItemId() { return lostItemId; }
    public void setLostItemId(Long lostItemId) { this.lostItemId = lostItemId; }

    public Long getFoundItemId() { return foundItemId; }
    public void setFoundItemId(Long foundItemId) { this.foundItemId = foundItemId; }

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public String getVerificationAnswer() { return verificationAnswer; }
    public void setVerificationAnswer(String verificationAnswer) { this.verificationAnswer = verificationAnswer; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
