package org.campusguard.dto;

import java.util.Map;

public class DashboardStatsDto {

    private long totalLostItems;
    private long totalFoundItems;
    private long potentialMatches;
    private long pendingClaims;
    private long returnedItems;
    private long pendingVerification;
    private long fraudFlagsCount;
    private long totalUsers;
    private double resolutionRate; // Percentage e.g. 78.5%

    private Map<String, Long> itemsByCategory;
    private Map<String, Long> itemsByLocation;
    private Map<String, Long> monthlyReports;

    public DashboardStatsDto() {}

    public long getTotalLostItems() { return totalLostItems; }
    public void setTotalLostItems(long totalLostItems) { this.totalLostItems = totalLostItems; }

    public long getTotalFoundItems() { return totalFoundItems; }
    public void setTotalFoundItems(long totalFoundItems) { this.totalFoundItems = totalFoundItems; }

    public long getPotentialMatches() { return potentialMatches; }
    public void setPotentialMatches(long potentialMatches) { this.potentialMatches = potentialMatches; }

    public long getPendingClaims() { return pendingClaims; }
    public void setPendingClaims(long pendingClaims) { this.pendingClaims = pendingClaims; }

    public long getReturnedItems() { return returnedItems; }
    public void setReturnedItems(long returnedItems) { this.returnedItems = returnedItems; }

    public long getPendingVerification() { return pendingVerification; }
    public void setPendingVerification(long pendingVerification) { this.pendingVerification = pendingVerification; }

    public long getFraudFlagsCount() { return fraudFlagsCount; }
    public void setFraudFlagsCount(long fraudFlagsCount) { this.fraudFlagsCount = fraudFlagsCount; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public double getResolutionRate() { return resolutionRate; }
    public void setResolutionRate(double resolutionRate) { this.resolutionRate = resolutionRate; }

    public Map<String, Long> getItemsByCategory() { return itemsByCategory; }
    public void setItemsByCategory(Map<String, Long> itemsByCategory) { this.itemsByCategory = itemsByCategory; }

    public Map<String, Long> getItemsByLocation() { return itemsByLocation; }
    public void setItemsByLocation(Map<String, Long> itemsByLocation) { this.itemsByLocation = itemsByLocation; }

    public Map<String, Long> getMonthlyReports() { return monthlyReports; }
    public void setMonthlyReports(Map<String, Long> monthlyReports) { this.monthlyReports = monthlyReports; }
}
