package org.campusguard.dto;

import org.campusguard.entity.enums.ItemStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class LostItemResponse {

    private Long id;
    private String itemName;
    private Long categoryId;
    private String categoryName;
    private String brand;
    private String color;
    private String description;
    private Long locationId;
    private String locationName;
    private LocalDate lostDate;
    private LocalTime lostTime;
    private String photoUrl;
    private String uniqueCharacteristics;
    private String serialNumber;
    private ItemStatus status;
    private Long reportedById;
    private String reportedByName;
    private LocalDateTime createdAt;

    // Optional field for owner only
    private String privateIdentifyingInfo;

    public LostItemResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

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

    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }

    public Long getReportedById() { return reportedById; }
    public void setReportedById(Long reportedById) { this.reportedById = reportedById; }

    public String getReportedByName() { return reportedByName; }
    public void setReportedByName(String reportedByName) { this.reportedByName = reportedByName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPrivateIdentifyingInfo() { return privateIdentifyingInfo; }
    public void setPrivateIdentifyingInfo(String privateIdentifyingInfo) { this.privateIdentifyingInfo = privateIdentifyingInfo; }
}
