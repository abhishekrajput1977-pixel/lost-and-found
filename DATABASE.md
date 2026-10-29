# CampusGuard – Database Schema & Entity Documentation

## Database Schema Design

CampusGuard uses relational database tables in PostgreSQL (or embedded H2 for development) mapped cleanly via JPA / Hibernate.

```
+---------------+     1:1     +-------------------+
|     users     |------------>| student_profiles  |
+---------------+             +-------------------+
        |
        | 1:N
        +----------------------------+----------------------------+
        |                            |                            |
        v                            v                            v
+---------------+            +---------------+            +---------------+
|  lost_items   |            |  found_items  |            |  notifications|
+---------------+            +---------------+            +---------------+
        |                            |
        +-------------+--------------+
                      |
                      v 1:N
             +------------------+
             |   item_matches   |
             +------------------+
                      |
                      v 1:1
             +------------------+
             |      claims      |
             +------------------+
                      |
                      v 1:1
             +------------------+
             | handover_records |
             +------------------+
```

---

## Key Table Structures

1. **`users`**: `id`, `email` (unique), `password` (BCrypt), `full_name`, `role` (STUDENT/STAFF/SECURITY_OFFICER/ADMIN), `enabled`, `created_at`, `updated_at`.
2. **`student_profiles`**: `id`, `user_id` (FK), `student_id` (e.g. `SRM2026CSE001`), `department_id` (FK), `year`, `section`, `phone_number`.
3. **`lost_items`**: `id`, `item_name`, `category_id` (FK), `brand`, `color`, `description`, `location_id` (FK), `lost_date`, `lost_time`, `photo_url`, `unique_characteristics`, `serial_number`, `private_identifying_info` (CONFIDENTIAL), `status`, `reported_by_id` (FK).
4. **`found_items`**: `id`, `item_name`, `category_id` (FK), `brand`, `color`, `description`, `location_id` (FK), `found_date`, `found_time`, `photo_url`, `condition_status`, `private_details` (CONFIDENTIAL), `status`, `reported_by_id` (FK).
5. **`item_matches`**: `id`, `lost_item_id` (FK), `found_item_id` (FK), `match_score`, `confidence_level`, `text_sim`, `location_sim`, `date_sim`, `category_sim`, `brand_sim`, `color_sim`, `status`.
6. **`claims`**: `id`, `lost_item_id` (FK), `found_item_id` (FK), `match_id` (FK), `claimant_id` (FK), `verification_score`, `status`, `submitted_at`, `reviewed_at`, `reviewed_by_id` (FK), `reason`.
7. **`handover_records`**: `id`, `reference_code` (e.g., `CG-HO-2026-000001`), `claim_id` (FK), `claimant_id` (FK), `security_officer_id` (FK), `location_id` (FK), `scheduled_time`, `completed_at`, `status`.
8. **`fraud_flags`**: `id`, `user_id` (FK), `item_type`, `item_id`, `reason`, `severity`, `status`.
9. **`trust_scores`**: `id`, `user_id` (FK), `score`, `last_updated_at`.
10. **`audit_logs`**: `id`, `user_id` (FK), `action`, `entity_name`, `entity_id`, `timestamp`, `metadata`, `ip_address`.
