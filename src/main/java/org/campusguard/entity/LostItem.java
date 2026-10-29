package org.campusguard.entity;

import jakarta.persistence.*;
import org.campusguard.entity.enums.ItemStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "lost_items", indexes = {
    @Index(name = "idx_lost_item_name", columnList = "itemName"),
    @Index(name = "idx_lost_category", columnList = "category_id"),
    @Index(name = "idx_lost_location", columnList = "location_id"),
    @Index(name = "idx_lost_status", columnList = "status"),
    @Index(name = "idx_lost_date", columnList = "lostDate")
})
public class LostItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String itemName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 80)
    private String brand;

    @Column(length = 50)
    private String color;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private LocalDate lostDate;

    private LocalTime lostTime;

    @Column(length = 255)
    private String photoUrl;

    @Column(columnDefinition = "TEXT")
    private String uniqueCharacteristics;

    @Column(length = 100)
    private String serialNumber;

    @Column(columnDefinition = "TEXT")
    private String privateIdentifyingInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemStatus status = ItemStatus.LOST;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_id", nullable = false)
    private User reportedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LostItem() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public LocalDate getLostDate() { return lostDate; }
    public void setLostDate(LocalDate lostDate) { this.lostDate = lostDate; }

    public LocalTime getLostTime() { return lostTime; }
    public void setLostTime(LocalTime lostTime) { this.lostTime = lostTime; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getUniqueCharacteristics() { return uniqueCharacteristics; }
    public void setUniqueCharacteristics(String uniqueCharacteristics) { this.uniqueCharacteristics = uniqueCharacteristics; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getPrivateIdentifyingInfo() { return privateIdentifyingInfo; }
    public void setPrivateIdentifyingInfo(String privateIdentifyingInfo) { this.privateIdentifyingInfo = privateIdentifyingInfo; }

    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }

    public User getReportedBy() { return reportedBy; }
    public void setReportedBy(User reportedBy) { this.reportedBy = reportedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
