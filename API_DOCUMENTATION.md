# CampusGuard – REST API Reference Documentation

## Base URL
`http://localhost:8080/api`

---

## 🔑 Authentication Endpoints

### 1. Register User
`POST /api/auth/register`
- **Request Body:**
  ```json
  {
    "email": "student1@college.edu",
    "password": "password123",
    "fullName": "Rahul Sharma",
    "studentId": "SRM2026CSE001",
    "departmentId": 1,
    "year": "1st Year",
    "section": "A",
    "phoneNumber": "+919876543210",
    "role": "STUDENT"
  }
  ```

### 2. Login User
`POST /api/auth/login`
- **Request Body:**
  ```json
  {
    "email": "student1@college.test",
    "password": "password123"
  }
  ```
- **Response:**
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "id": 4,
    "email": "student1@college.test",
    "fullName": "Rahul Sharma",
    "role": "STUDENT"
  }
  ```

---

## 🎒 Items Endpoints

### 3. Report Lost Item
`POST /api/lost-items` (`multipart/form-data`)
- **Fields:** `data` (JSON), `file` (Multipart image)

### 4. Search Items
`GET /api/items/search?query=backpack&itemType=LOST&categoryId=7&locationId=1`

---

## 🤖 Smart Matching Endpoints

### 5. Get User Matches
`GET /api/matches`

---

## 🛡️ Claims & Verification Endpoints

### 6. Submit Claim
`POST /api/claims`
```json
{
  "lostItemId": 1,
  "foundItemId": 1,
  "matchId": 1,
  "verificationAnswer": "Small tear on left strap near shoulder cushion",
  "reason": "Requesting claim review"
}
```

---

## 👮 Security Handover Endpoints

### 7. Complete Handover
`POST /api/handover/{id}/complete?notes=Physical ID verified` (Requires `ROLE_SECURITY_OFFICER` or `ROLE_ADMIN`)
