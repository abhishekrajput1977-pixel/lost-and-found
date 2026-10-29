package org.campusguard.matching;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class OwnershipVerifier {

    private final JaroWinklerSimilarity stringSimilarity = new JaroWinklerSimilarity();

    public double evaluateOwnership(String privateIdentifyingInfo, String claimantAnswer) {
        if (privateIdentifyingInfo == null || privateIdentifyingInfo.isBlank()) {
            return 50.0; // default baseline if no private info was provided originally
        }
        if (claimantAnswer == null || claimantAnswer.isBlank()) {
            return 0.0;
        }

        String expected = privateIdentifyingInfo.toLowerCase(Locale.ROOT).trim();
        String actual = claimantAnswer.toLowerCase(Locale.ROOT).trim();

        // Exact match or substring match
        if (expected.equalsIgnoreCase(actual)) {
            return 100.0;
        }
        if (expected.contains(actual) || actual.contains(expected)) {
            return 92.0;
        }

        // Keyword overlap calculation
        String[] expectedTokens = expected.split("\\s+");
        String[] actualTokens = actual.split("\\s+");
        int matchCount = 0;
        for (String t1 : actualTokens) {
            if (t1.length() <= 2) continue;
            for (String t2 : expectedTokens) {
                if (t2.length() <= 2) continue;
                Double sim = stringSimilarity.apply(t1, t2);
                if (sim != null && sim >= 0.85) {
                    matchCount++;
                    break;
                }
            }
        }

        if (expectedTokens.length > 0 && matchCount > 0) {
            double tokenOverlapRatio = (double) matchCount / Math.max(1, expectedTokens.length);
            if (tokenOverlapRatio >= 0.75) return 88.0;
            if (tokenOverlapRatio >= 0.50) return 75.0;
            if (tokenOverlapRatio >= 0.25) return 60.0;
        }

        Double jaroWinkler = stringSimilarity.apply(expected, actual);
        double simScore = jaroWinkler != null ? jaroWinkler * 100.0 : 0.0;
        return Math.round(simScore * 10.0) / 10.0;
    }

    public String classifyConfidence(double ownershipScore) {
        if (ownershipScore >= 90.0) {
            return "HIGH_CONFIDENCE";
        } else if (ownershipScore >= 70.0) {
            return "MANUAL_REVIEW";
        } else {
            return "SUSPICIOUS_REJECTED";
        }
    }
}
