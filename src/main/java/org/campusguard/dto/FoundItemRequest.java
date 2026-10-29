package org.campusguard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class FoundItemRequest {

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

    @NotNull(message = "Found date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate foundDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime foundTime;

    private String photoUrl;
    private String conditionStatus;

    // PRIVATE CONFIDENTIAL FIELD
    private String privateDetails;

    public FoundItemRequest() {}

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

    public LocalDate getFoundDate() { return foundDate; }
    public void setFoundDate(LocalDate foundDate) { this.foundDate = foundDate; }

    public LocalTime getFoundTime() { return foundTime; }
    public void setFoundTime(LocalTime foundTime) { this.foundTime = foundTime; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getConditionStatus() { return conditionStatus; }
    public void setConditionStatus(String conditionStatus) { this.conditionStatus = conditionStatus; }

    public String getPrivateDetails() { return privateDetails; }
    public void setPrivateDetails(String privateDetails) { this.privateDetails = privateDetails; }
}
