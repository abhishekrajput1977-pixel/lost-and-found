package org.campusguard;

import org.campusguard.entity.Category;
import org.campusguard.entity.FoundItem;
import org.campusguard.entity.ItemMatch;
import org.campusguard.entity.Location;
import org.campusguard.entity.LostItem;
import org.campusguard.entity.enums.ItemStatus;
import org.campusguard.entity.enums.MatchConfidence;
import org.campusguard.matching.SmartMatchingEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SmartMatchingEngineTest {

    private SmartMatchingEngine matchingEngine;

    @BeforeEach
    public void setUp() {
        matchingEngine = new SmartMatchingEngine();
        ReflectionTestUtils.setField(matchingEngine, "textWeight", 0.40);
        ReflectionTestUtils.setField(matchingEngine, "locationWeight", 0.20);
        ReflectionTestUtils.setField(matchingEngine, "dateWeight", 0.15);
        ReflectionTestUtils.setField(matchingEngine, "categoryWeight", 0.10);
        ReflectionTestUtils.setField(matchingEngine, "brandWeight", 0.10);
        ReflectionTestUtils.setField(matchingEngine, "colorWeight", 0.05);
        ReflectionTestUtils.setField(matchingEngine, "thresholdHigh", 85.0);
        ReflectionTestUtils.setField(matchingEngine, "thresholdMedium", 65.0);
    }

    @Test
    public void testHighConfidenceMatch() {
        Category cat = new Category(1L, "Bags", "briefcase", "Bags");
        Location loc = new Location(1L, "Central Library", "Main Building", "Reading Hall", true);

        LostItem lost = new LostItem();
        lost.setItemName("Black Nike Backpack");
        lost.setBrand("Nike");
        lost.setColor("Black");
        lost.setDescription("Black Nike college backpack with laptop compartment");
        lost.setCategory(cat);
        lost.setLocation(loc);
        lost.setLostDate(LocalDate.now());

        FoundItem found = new FoundItem();
        found.setItemName("Black Nike Backpack");
        found.setBrand("Nike");
        found.setColor("Black");
        found.setDescription("Found black Nike college backpack in reading hall");
        found.setCategory(cat);
        found.setLocation(loc);
        found.setFoundDate(LocalDate.now());

        ItemMatch match = matchingEngine.computeMatch(lost, found);

        assertNotNull(match);
        assertTrue(match.getMatchScore() >= 85.0, "Score should be >= 85.0 for near identical reports");
        assertEquals(MatchConfidence.HIGH_CONFIDENCE, match.getConfidenceLevel());
    }
}
