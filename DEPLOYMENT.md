# Deploying CampusGuard

CampusGuard runs as one Docker web service and needs a PostgreSQL database. The supplied `Dockerfile` is suitable for a hosting service that can build from a Git repository or a Docker image.

## Required configuration

Set these service environment variables:

| Name | Value |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `postgres` |
| `SPRING_DATASOURCE_URL` | JDBC connection string, e.g. `jdbc:postgresql://HOST:5432/campusguard` |
| `SPRING_DATASOURCE_USERNAME` | PostgreSQL username |
| `SPRING_DATASOURCE_PASSWORD` | PostgreSQL password |
| `CAMPUSGUARD_JWT_SECRET` | A new random secret of at least 32 characters |
| `CAMPUSGUARD_UPLOAD_DIR` | `/data/uploads` (or the path mounted to persistent storage) |

The host must make its assigned `PORT` available to the container. The application reads it automatically.

## Persistent storage

Attach a persistent disk to `/data`. PostgreSQL stores the database; the disk retains item photo uploads. Without the disk, uploaded photos disappear after a redeploy or restart.

## Before publishing publicly

This repository starts with seeded demonstration data, including known administrator and security-officer passwords. It is suitable only for a short-lived demo. For a real deployment, remove or replace the demo seed data, create private administrator accounts, use a newly generated JWT secret, and configure a real SMTP provider before inviting users.

## Verification after deployment

1. Open the service URL and confirm the landing page loads.
2. Sign in only with non-demo production credentials.
3. Upload an image and open its `/uploads/...` URL to confirm the persistent disk and upload mapping work.
4. Restart or redeploy the service and confirm both database data and uploaded image remain available.
