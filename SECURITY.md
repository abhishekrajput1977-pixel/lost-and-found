# CampusGuard – Cybersecurity & Privacy Architecture

## Security Principles Implemented

1. **Role-Based Access Control (RBAC)**:
   - `STUDENT`: Limited to filing reports, submitting claims, and verifying ownership for personal items.
   - `STAFF`: Access to found item submission and campus support tools.
   - `SECURITY_OFFICER`: Exclusive access to scheduled handover queues and college ID verification actions.
   - `ADMIN`: Full administrative control, user status toggling, claim decisions, and audit log inspection.

2. **Data Privacy & Confidentiality Rules**:
   - `privateIdentifyingInfo` and `privateDetails` are filtered from all public search DTO responses.
   - Student emails and phone numbers are hidden from general users.
   - Secret verification questions are checked algorithmically without displaying exact answers.

3. **BCrypt Password Hashing**:
   - All user passwords are salted and hashed with BCrypt before persistence.

4. **Dual Authentication Pipeline**:
   - **REST APIs (`/api/**`)**: Stateless JWT token authentication via `Authorization: Bearer <token>` header or `JWT_TOKEN` cookie.
   - **Web Interface (`/**`)**: Session/Cookie-based authentication with CSRF protection on forms.

5. **Anti-Fraud & Audit Logging**:
   - `AuditService` records immutable security logs for all critical system actions (`LOGIN`, `CLAIM_SUBMITTED`, `VERIFICATION_ATTEMPTED`, `CLAIM_APPROVED`, `HANDOVER_COMPLETED`).
   - `FraudEngine` maintains internal user `TrustScore` metrics and flags suspicious claim attempts.
