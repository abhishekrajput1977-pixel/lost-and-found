package org.campusguard.matching;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.campusguard.entity.FoundItem;
import org.campusguard.entity.ItemMatch;
import org.campusguard.entity.LostItem;
import org.campusguard.entity.enums.MatchConfidence;
import org.campusguard.entity.enums.MatchStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Component
public class SmartMatchingEngine {

    @Value("${campusguard.matching.weights.text:0.40}")
    private double textWeight;

    @Value("${campusguard.matching.weights.location:0.20}")
    private double locationWeight;

    @Value("${campusguard.matching.weights.date:0.15}")
    private double dateWeight;

    @Value("${campusguard.matching.weights.category:0.10}")
    private double categoryWeight;

    @Value("${campusguard.matching.weights.brand:0.10}")
    private double brandWeight;

    @Value("${campusguard.matching.weights.color:0.05}")
    private double colorWeight;

    @Value("${campusguard.matching.threshold-high:85.0}")
    private double thresholdHigh;

    @Value("${campusguard.matching.threshold-medium:65.0}")
    private double thresholdMedium;

    private final JaroWinklerSimilarity stringSimilarity = new JaroWinklerSimilarity();

    public ItemMatch computeMatch(LostItem lost, FoundItem found) {
        double textSim = calculateTextSimilarity(lost.getItemName() + " " + lost.getDescription(),
                                                 found.getItemName() + " " + found.getDescription());
        double locationSim = calculateLocationSimilarity(lost, found);
        double dateSim = calculateDateSimilarity(lost, found);
        double categorySim = calculateCategorySimilarity(lost, found);
        double brandSim = calculateBrandSimilarity(lost.getBrand(), found.getBrand());
        double colorSim = calculateColorSimilarity(lost.getColor(), found.getColor());

        double totalScore = (textSim * textWeight)
                + (locationSim * locationWeight)
                + (dateSim * dateWeight)
                + (categorySim * categoryWeight)
                + (brandSim * brandWeight)
                + (colorSim * colorWeight);

        double percentageScore = Math.round(totalScore * 1000.0) / 10.0; // e.g. 91.5

        MatchConfidence confidenceLevel;
        if (percentageScore >= thresholdHigh) {
            confidenceLevel = MatchConfidence.HIGH_CONFIDENCE;
        } else if (percentageScore >= thresholdMedium) {
            confidenceLevel = MatchConfidence.MEDIUM_CONFIDENCE;
        } else {
            confidenceLevel = MatchConfidence.LOW_CONFIDENCE;
        }

        ItemMatch match = new ItemMatch();
        match.setLostItem(lost);
        match.setFoundItem(found);
        match.setMatchScore(percentageScore);
        match.setConfidenceLevel(confidenceLevel);
        match.setTextSim(Math.round(textSim * 1000.0) / 10.0);
        match.setLocationSim(Math.round(locationSim * 1000.0) / 10.0);
        match.setDateSim(Math.round(dateSim * 1000.0) / 10.0);
        match.setCategorySim(Math.round(categorySim * 1000.0) / 10.0);
        match.setBrandSim(Math.round(brandSim * 1000.0) / 10.0);
        match.setColorSim(Math.round(colorSim * 1000.0) / 10.0);
        match.setStatus(MatchStatus.PENDING);

        return match;
    }

    private double calculateTextSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) return 0.0;
        String s1 = text1.toLowerCase(Locale.ROOT).trim();
        String s2 = text2.toLowerCase(Locale.ROOT).trim();
        Double jaroWinkler = stringSimilarity.apply(s1, s2);
        return jaroWinkler != null ? jaroWinkler : 0.0;
    }

    private double calculateLocationSimilarity(LostItem lost, FoundItem found) {
        if (lost.getLocation() == null || found.getLocation() == null) return 0.0;
        if (lost.getLocation().getId().equals(found.getLocation().getId())) {
            return 1.0;
        }
        if (lost.getLocation().getBuilding() != null &&
            lost.getLocation().getBuilding().equalsIgnoreCase(found.getLocation().getBuilding())) {
            return 0.5;
        }
        return 0.0;
    }

    private double calculateDateSimilarity(LostItem lost, FoundItem found) {
        if (lost.getLostDate() == null || found.getFoundDate() == null) return 0.0;
        long daysBetween = Math.abs(ChronoUnit.DAYS.between(lost.getLostDate(), found.getFoundDate()));
        if (daysBetween == 0) return 1.0;
        if (daysBetween <= 1) return 0.9;
        if (daysBetween <= 3) return 0.7;
        if (daysBetween <= 7) return 0.5;
        if (daysBetween <= 14) return 0.3;
        return 0.1;
    }

    private double calculateCategorySimilarity(LostItem lost, FoundItem found) {
        if (lost.getCategory() == null || found.getCategory() == null) return 0.0;
        return lost.getCategory().getId().equals(found.getCategory().getId()) ? 1.0 : 0.0;
    }

    private double calculateBrandSimilarity(String brand1, String brand2) {
        if (brand1 == null || brand2 == null || brand1.isBlank() || brand2.isBlank()) return 0.5; // neutral if unassigned
        String b1 = brand1.toLowerCase(Locale.ROOT).trim();
        String b2 = brand2.toLowerCase(Locale.ROOT).trim();
        Double score = stringSimilarity.apply(b1, b2);
        return score != null ? score : 0.0;
    }

    private double calculateColorSimilarity(String color1, String color2) {
        if (color1 == null || color2 == null || color1.isBlank() || color2.isBlank()) return 0.5;
        String c1 = color1.toLowerCase(Locale.ROOT).trim();
        String c2 = color2.toLowerCase(Locale.ROOT).trim();
        if (c1.contains(c2) || c2.contains(c1)) return 1.0;
        Double score = stringSimilarity.apply(c1, c2);
        return score != null ? score : 0.0;
    }
}
