# CampusGuard – Smart Matching Algorithm Specification

## Matching Weight Formula

When a new lost or found item is reported, the `SmartMatchingEngine` calculates a composite score:

$$\text{MatchScore} = (\text{TextSim} \times 0.40) + (\text{LocationSim} \times 0.20) + (\text{DateSim} \times 0.15) + (\text{CategorySim} \times 0.10) + (\text{BrandSim} \times 0.10) + (\text{ColorSim} \times 0.05)$$

---

## Attribute Breakdown

1. **Text Description & Name Similarity (40%)**:
   - Tokenized Jaro-Winkler string similarity comparing item name and detailed description.
2. **Location Similarity (20%)**:
   - `1.0` (100%) if exact location match.
   - `0.5` (50%) if same building match.
   - `0.0` (0%) otherwise.
3. **Date Similarity (15%)**:
   - `1.0` if same date.
   - Decays proportionally based on day difference (0 to 14 days).
4. **Category Match (10%)**:
   - `1.0` if category IDs match.
5. **Brand Match (10%)**:
   - Jaro-Winkler similarity on lowercased brand names.
6. **Color Match (5%)**:
   - String similarity / substring color containment.

---

## Confidence Threshold Classification

- **`HIGH_CONFIDENCE`**: Score $\ge 85.0\%$
- **`MEDIUM_CONFIDENCE`**: $65.0\% \le \text{Score} < 85.0\%$
- **`LOW_CONFIDENCE`**: $\text{Score} < 65.0\%$
