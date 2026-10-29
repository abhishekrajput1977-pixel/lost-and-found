# CampusGuard – System Architecture Document

## Architecture Overview

CampusGuard follows a **Clean Layered Architecture** separating concerns across Controllers, Services, Domain Entities, Repositories, Security Components, and Thymeleaf Views.

```
                    +---------------------------------------+
                    |           Browser / Client            |
                    +---------------------------------------+
                                  |           |
                        HTML/Form |           | JSON / Bearer JWT
                                  v           v
                    +---------------------------------------+
                    |    Web Controllers | REST API Cont.   |
                    +---------------------------------------+
                                        |
                                        v
                    +---------------------------------------+
                    |             Service Layer             |
                    | (Matching, Verification, Fraud, etc.) |
                    +---------------------------------------+
                                        |
                                        v
                    +---------------------------------------+
                    |        Repository Layer (JPA)         |
                    +---------------------------------------+
                                        |
                                        v
                    +---------------------------------------+
                    |      PostgreSQL / H2 Database         |
                    +---------------------------------------+
```

---

## Package Breakdown

- `org.campusguard.config`: Application profiles, security rules, and seed data initialization (`DataInitializer`).
- `org.campusguard.controller.api`: REST API Controllers exposing JSON endpoints.
- `org.campusguard.controller.web`: Thymeleaf Web Controllers serving interactive pages.
- `org.campusguard.dto`: Data Transfer Objects protecting entity boundaries and sensitive data.
- `org.campusguard.entity`: JPA Entities representing database domain models.
- `org.campusguard.entity.enums`: Domain enums (`Role`, `ItemStatus`, `MatchConfidence`, `ClaimStatus`, `HandoverStatus`, `FraudSeverity`, `AuditAction`).
- `org.campusguard.exception`: Custom runtime exceptions & `GlobalExceptionHandler`.
- `org.campusguard.fraud`: `FraudEngine` for TrustScore calculations and FraudFlag creation.
- `org.campusguard.matching`: `SmartMatchingEngine` and `OwnershipVerifier`.
- `org.campusguard.notification`: `NotificationService` handling emails and in-app alerts.
- `org.campusguard.repository`: Spring Data JPA repositories with custom queries and specifications.
- `org.campusguard.security`: Spring Security 6 config, JWT token provider, and Custom User Details.
- `org.campusguard.service`: Pure business logic handling auth, items, matches, claims, handovers, and admin stats.
- `org.campusguard.util`: File storage service for safe image uploads.
