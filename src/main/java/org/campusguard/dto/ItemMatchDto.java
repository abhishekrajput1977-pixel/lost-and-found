package org.campusguard.dto;

import org.campusguard.entity.enums.MatchConfidence;
import org.campusguard.entity.enums.MatchStatus;

import java.time.LocalDateTime;

public class ItemMatchDto {

    private Long id;
    private LostItemResponse lostItem;
    private FoundItemResponse foundItem;
    private Double matchScore;
    private MatchConfidence confidenceLevel;
    private Double textSim;
    private Double locationSim;
    private Double dateSim;
    private Double categorySim;
    private Double brandSim;
    private Double colorSim;
    private MatchStatus status;
    private LocalDateTime createdAt;

    public ItemMatchDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LostItemResponse getLostItem() { return lostItem; }
    public void setLostItem(LostItemResponse lostItem) { this.lostItem = lostItem; }

    public FoundItemResponse getFoundItem() { return foundItem; }
    public void setFoundItem(FoundItemResponse foundItem) { this.foundItem = foundItem; }

    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }

    public MatchConfidence getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(MatchConfidence confidenceLevel) { this.confidenceLevel = confidenceLevel; }

    public Double getTextSim() { return textSim; }
    public void setTextSim(Double textSim) { this.textSim = textSim; }

    public Double getLocationSim() { return locationSim; }
    public void setLocationSim(Double locationSim) { this.locationSim = locationSim; }

    public Double getDateSim() { return dateSim; }
    public void setDateSim(Double dateSim) { this.dateSim = dateSim; }

    public Double getCategorySim() { return categorySim; }
    public void setCategorySim(Double categorySim) { this.categorySim = categorySim; }

    public Double getBrandSim() { return brandSim; }
    public void setBrandSim(Double brandSim) { this.brandSim = brandSim; }

    public Double getColorSim() { return colorSim; }
    public void setColorSim(Double colorSim) { this.colorSim = colorSim; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
