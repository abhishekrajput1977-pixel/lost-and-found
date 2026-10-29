package org.campusguard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class LostItemRequest {

    @NotBlank(message = "Item name is required")
    private String itemName;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String brand;
    private String color;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Location is required")
    private Long locationId;

    @NotNull(message = "Lost date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lostDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime lostTime;

    private String photoUrl;
    private String uniqueCharacteristics;
    private String serialNumber;

    // PRIVATE CONFIDENTIAL FIELD
    private String privateIdentifyingInfo;

    public LostItemRequest() {}

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

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
}
