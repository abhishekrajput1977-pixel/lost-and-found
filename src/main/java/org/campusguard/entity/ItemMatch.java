package org.campusguard.entity;

import jakarta.persistence.*;
import org.campusguard.entity.enums.MatchConfidence;
import org.campusguard.entity.enums.MatchStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "item_matches", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"lost_item_id", "found_item_id"})
})
public class ItemMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lost_item_id", nullable = false)
    private LostItem lostItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "found_item_id", nullable = false)
    private FoundItem foundItem;

    @Column(nullable = false)
    private Double matchScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MatchConfidence confidenceLevel;

    private Double textSim;
    private Double locationSim;
    private Double dateSim;
    private Double categorySim;
    private Double brandSim;
    private Double colorSim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MatchStatus status = MatchStatus.PENDING;

    private LocalDateTime createdAt;

    public ItemMatch() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LostItem getLostItem() { return lostItem; }
    public void setLostItem(LostItem lostItem) { this.lostItem = lostItem; }

    public FoundItem getFoundItem() { return foundItem; }
    public void setFoundItem(FoundItem foundItem) { this.foundItem = foundItem; }

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
