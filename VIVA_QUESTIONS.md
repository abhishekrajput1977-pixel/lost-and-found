# CampusGuard – Viva Preparation Questions & Answers

### Q1: What makes CampusGuard different from a basic CRUD project?
**Answer:** CampusGuard is an enterprise college security application featuring:
1. Closed college identity verification (SRM Student IDs).
2. A multi-attribute weighted Smart Matching Engine calculating exact similarity percentages (Text 40%, Location 20%, Date 15%, etc.).
3. Confidential secret mark ownership verification protecting privacy.
4. Internal trust scoring (`TrustScore`) and automated fraud flag detection.
5. Security officer physical handover workflow with unique reference codes (`CG-HO-2026-XXXXXX`).
6. Security audit logging for compliance and Chart.js analytics dashboard.

---

### Q2: How does the Smart Matching algorithm compute match scores?
**Answer:** The algorithm evaluates 6 weighted attributes:
- **Text Similarity (40%)**: Jaro-Winkler & Levenshtein string similarity on tokenized item name and description.
- **Location Proximity (20%)**: 100% for exact location match, 50% for same building.
- **Date Proximity (15%)**: Proportional decay based on day difference.
- **Category (10%)**, **Brand (10%)**, and **Color (5%)**.

---

### Q3: How is student privacy protected in the database and API?
**Answer:** 
- Private identifying information (`privateIdentifyingInfo` and `privateDetails`) is stripped from public DTOs.
- Student email addresses and phone numbers are hidden from general search views.
- Ownership verification compares claimant answers against stored confidential marks without revealing the secret answer.

---

### Q4: How is security and role-based access control configured?
**Answer:** Using Spring Security 6 with BCrypt password hashing, custom `UserDetailsService`, stateless JWT authentication for REST APIs, and session-based authentication for Thymeleaf Web views across four distinct roles (`STUDENT`, `STAFF`, `SECURITY_OFFICER`, `ADMIN`).

---

### Q5: How does the handover workflow work?
**Answer:** When an admin approves a verified claim, the system generates a unique reference code (`CG-HO-2026-XXXXXX`). The student presents this code and their official college ID card to the Security Officer. Upon physical ID verification, the Security Officer completes the handover in the portal, transitioning item status to `RETURNED`.
