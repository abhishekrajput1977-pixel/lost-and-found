# CampusGuard – Smart College Lost & Found System

> **Tagline:** *Lost on Campus. Found Faster. Returned Safely.*

CampusGuard is a full-stack, enterprise-grade Java 21 / Spring Boot 3 web application designed to solve the real problem of lost and found items inside a college campus using **verified college identity**, **smart composite matching**, **confidential ownership verification**, **anti-fraud protection**, and **secure security officer handovers**.

---

## 🚀 Key Features

1. **College Identity Verification & Closed Network**: Closed registration enforcing official student IDs (e.g. `SRM2026CSE001`) and department mappings.
2. **Privacy-First Architecture**: Student emails, phone numbers, and secret item identifying marks are encrypted and restricted from public view.
3. **Smart Matching Engine (Java-Based)**: Multi-attribute composite score algorithm:
   - Text Description & Name Similarity – **40%**
   - Location Match – **20%**
   - Date Proximity – **15%**
   - Category Match – **10%**
   - Brand Match – **10%**
   - Color Match – **5%**
   - Automatically computes percentage score (e.g., `92.5%`) and classifies into `HIGH_CONFIDENCE`, `MEDIUM_CONFIDENCE`, or `LOW_CONFIDENCE`.
4. **Ownership Verification Protocol**: Evaluates claimant answers against stored confidential identifying marks without revealing secret answers.
5. **Anti-Fraud & Internal Trust Scoring**: Maintains user `TrustScore` (+10 for verified return, -10/-5 for fraudulent/failed attempts) and flags suspicious multi-claim behavior.
6. **Security Officer Handover Desk**: Generates unique reference codes (`CG-HO-2026-000001`) and requires physical college ID verification before marking items `RETURNED`.
7. **Dual Controller Interface**: Interactive Thymeleaf Web UI + RESTful JSON API endpoints with JWT authentication.
8. **Admin Control Center**: Analytics dashboard with Chart.js visualization, user status toggling, fraud flag review, and security audit logs.

---

## 🛠️ Technology Stack

- **Backend Framework**: Spring Boot 3.2.4 (Java 21 / Java 26)
- **Security**: Spring Security 6, JWT (jjwt 0.12.5), BCrypt Password Hashing, Role-Based Access Control (RBAC)
- **Database**: PostgreSQL (Production) + Embedded H2 Database (In-Memory Dev Mode)
- **Persistence**: Spring Data JPA / Hibernate ORM with custom indexes
- **Frontend UI**: Thymeleaf + HTML5 + Bootstrap 5 + FontAwesome + Chart.js + Custom CSS3/JS
- **Matching Engine**: Apache Commons Text (Jaro-Winkler & Levenshtein String Similarity)
- **Build System**: Apache Maven 3.9+

---

## 👥 User Roles & Access Matrix

| Role | Permissions |
| :--- | :--- |
| **STUDENT** | Register, Login, Report Lost/Found items, Upload photos, Search items, View matches, Submit claims, Complete ownership verification, Track handovers. |
| **STAFF** | Report found items, View campus reports, Assist with item verification. |
| **SECURITY_OFFICER** | View scheduled handovers, Verify student physical college ID, Complete item return, Update status to `RETURNED`. |
| **ADMIN** | Manage users, Approve/reject claims, Review fraud flags, View Chart.js analytics, Inspect security audit logs. |

---

## 🔐 Pre-seeded Viva Demo Accounts

> **Note:** Included in `DataInitializer` for instant local execution and viva presentation!

| Role | Email | Password | Details |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@college.test` | `password123` | Full control center & analytics |
| **SECURITY_OFFICER** | `security@college.test` | `password123` | Security Handover Desk |
| **STUDENT (Owner)** | `student1@college.test` | `password123` | Student ID: `SRM2026CSE001` (Rahul Sharma) |
| **STUDENT (Finder)** | `student2@college.test` | `password123` | Student ID: `SRM2026CSE002` (Aman Verma) |

---

## 💻 How to Run the Application

### Option A: Standard Run (Embedded H2 Database - Zero Setup)

1. Clone or open the workspace:
   ```bash
   cd "e:\Lost and found"
   ```
2. Build and run via Maven:
   ```bash
   mvn spring-boot:run
   ```
3. Open your browser:
   - **Application URL**: [http://localhost:8080](http://localhost:8080)
   - **H2 Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:campusguarddb`, Username: `sa`, Password: *blank*)

### Option B: PostgreSQL Production Database Setup

1. Create PostgreSQL database:
   ```sql
   CREATE DATABASE campusguard;
   ```
2. Set environment variables:
   ```bash
   set SPRING_PROFILES_ACTIVE=postgres
   set SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/campusguard
   set SPRING_DATASOURCE_USERNAME=postgres
   set SPRING_DATASOURCE_PASSWORD=postgres
   ```
3. Launch application:
   ```bash
   mvn spring-boot:run
   ```

---

## 🎬 Viva End-to-End Demo Workflow (17 Steps)

1. Log in as **Student 1** (`student1@college.test`).
2. Click **Report Lost Item** -> Enter `Black Nike Backpack`, Location `Central Library`, Private Mark `Small tear on left strap`.
3. Log out and log in as **Student 2** (`student2@college.test`).
4. Click **Report Found Item** -> Enter `Black Nike Backpack`, Location `Central Library`.
5. The **Smart Matching Engine** triggers instantly and computes a **92.5% Match Score**.
6. System sends an in-app alert and college email notification to Student 1.
7. Log in back as **Student 1** -> Go to **Matches** -> View score breakdown.
8. Click **Submit Ownership Claim**.
9. Answer secret question: `Small tear on left strap`.
10. System evaluates answer with `OwnershipVerifier` -> Calculates `95% Verification Score`.
11. Log in as **Admin** (`admin@college.test`) -> Open **Review Claims**.
12. Click **Approve Claim**. System generates unique Handover Reference `CG-HO-2026-000001`.
13. Log in as **Security Officer** (`security@college.test`) -> Open **Handover Queue**.
14. Locate Handover `CG-HO-2026-000001` -> Verify student ID `SRM2026CSE001`.
15. Click **Confirm ID & Complete Handover**.
16. Item status transitions to **RETURNED**.
17. Open **Admin Dashboard** to observe live updated statistics and charts.
