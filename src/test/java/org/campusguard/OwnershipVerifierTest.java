package org.campusguard;

import org.campusguard.matching.OwnershipVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OwnershipVerifierTest {

    private final OwnershipVerifier verifier = new OwnershipVerifier();

    @Test
    public void testExactMatchScore() {
        String secret = "Small tear on left strap near shoulder cushion";
        String answer = "Small tear on left strap near shoulder cushion";

        double score = verifier.evaluateOwnership(secret, answer);
        assertEquals(100.0, score);
        assertEquals("HIGH_CONFIDENCE", verifier.classifyConfidence(score));
    }

    @Test
    public void testSubstringMatchScore() {
        String secret = "Small tear on left strap near shoulder cushion";
        String answer = "tear on left strap";

        double score = verifier.evaluateOwnership(secret, answer);
        assertTrue(score >= 90.0);
        assertEquals("HIGH_CONFIDENCE", verifier.classifyConfidence(score));
    }

    @Test
    public void testIncorrectAnswer() {
        String secret = "Small tear on left strap near shoulder cushion";
        String answer = "Completely wrong answer with no relation";

        double score = verifier.evaluateOwnership(secret, answer);
        assertTrue(score < 70.0);
        assertEquals("SUSPICIOUS_REJECTED", verifier.classifyConfidence(score));
    }
}
