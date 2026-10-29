# CampusGuard – Viva Demo Execution Guide

Follow these exact steps during your viva presentation to demonstrate the complete functional flow:

---

### Step 1: Open the Application
Navigate to `http://localhost:8080`. Point out the **Landing Page**, Hero section, live stats bar, How It Works, and FAQ.

---

### Step 2: Login as Student 1 (Item Owner)
1. Click **Login**.
2. Click the quick button **Student** (`student1@college.test` / `password123`).
3. Point out the **Student Dashboard** showing College ID (`SRM2026CSE001`) and department (`CSE`).

---

### Step 3: Report a Lost Item
1. Click **Report Lost Item**.
2. Enter:
   - **Item Name:** `Black Nike Backpack`
   - **Category:** `Bags`
   - **Brand:** `Nike` | **Color:** `Black`
   - **Location:** `Central Library`
   - **Private Identifying Info:** `Small tear on left strap near shoulder cushion`
3. Click **Submit Lost Report**.

---

### Step 4: Login as Student 2 (Item Finder)
1. Logout and click **Login**.
2. Click **Student** (change email to `student2@college.test` / `password123`).
3. Click **Report Found Item**.
4. Enter:
   - **Item Name:** `Black Nike Backpack`
   - **Category:** `Bags`
   - **Location:** `Central Library`
5. Click **Submit Found Report**.

---

### Step 5: Observe Smart Matching & Notification
1. Log back in as **Student 1** (`student1@college.test`).
2. Notice the **Navbar Notification Counter** update!
3. Open **Matches** -> Observe the **92.5% Match Score** computed by the Smart Matching Engine.
4. Click **Review Match & Verify Ownership**. Point out the 6-attribute weight breakdown (Text 40%, Location 20%, Date 15%, Category 10%, Brand 10%, Color 5%).

---

### Step 6: Submit Ownership Verification Secret
1. Click **Submit Ownership Claim**.
2. Answer the secret question: `Small tear on left strap`.
3. Click **Submit Claim & Verify Score**.
4. System evaluates answer -> Generates **95% Verification Score** (High Confidence).

---

### Step 7: Admin Review & Handover Code Generation
1. Log out and log in as **Admin** (`admin@college.test` / `password123`).
2. Open **Review Claims**.
3. Locate Claim #1 -> Click **Review & Decide**.
4. Select **APPROVE** -> Click **Submit Decision**.
5. Point out the auto-generated unique Handover Code `CG-HO-2026-000001`!

---

### Step 8: Security Officer Physical Handover
1. Log out and log in as **Security Officer** (`security@college.test` / `password123`).
2. Open **Security Officer Handover Desk**.
3. Locate Handover `CG-HO-2026-000001`.
4. Click **Verify & Complete**.
5. Verify Student ID `SRM2026CSE001` -> Click **Confirm ID & Complete Handover**.

---

### Step 9: Verify Final System State
1. Item status updates to **RETURNED**.
2. Open **Admin Control Center** (`/admin/dashboard`) -> Point out live updated statistics and Chart.js category/location distribution graphs!
