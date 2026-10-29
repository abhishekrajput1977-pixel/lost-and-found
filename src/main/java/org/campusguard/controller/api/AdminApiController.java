package org.campusguard.controller.api;

import org.campusguard.dto.ClaimResponse;
import org.campusguard.dto.DashboardStatsDto;
import org.campusguard.dto.FraudFlagDto;
import org.campusguard.dto.UserDto;
import org.campusguard.entity.AuditLog;
import org.campusguard.entity.User;
import org.campusguard.entity.enums.FraudFlagStatus;
import org.campusguard.service.AdminService;
import org.campusguard.service.AuthService;
import org.campusguard.service.ClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final AdminService adminService;
    private final ClaimService claimService;
    private final AuthService authService;

    public AdminApiController(AdminService adminService, ClaimService claimService, AuthService authService) {
        this.adminService = adminService;
        this.claimService = claimService;
        this.authService = authService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/users/{id}/toggle-status")
    public ResponseEntity<Void> toggleUserStatus(@PathVariable Long id) {
        User admin = authService.getCurrentAuthenticatedUser();
        adminService.toggleUserStatus(id, admin);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fraud")
    public ResponseEntity<List<FraudFlagDto>> getFraudFlags() {
        return ResponseEntity.ok(adminService.getFraudFlags());
    }

    @PostMapping("/fraud/{id}/resolve")
    public ResponseEntity<Void> resolveFraudFlag(
            @PathVariable Long id,
            @RequestParam("status") FraudFlagStatus status) {
        User admin = authService.getCurrentAuthenticatedUser();
        adminService.resolveFraudFlag(id, status, admin);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/claims/{id}/review")
    public ResponseEntity<ClaimResponse> reviewClaim(
            @PathVariable Long id,
            @RequestParam("approve") boolean approve,
            @RequestParam(value = "reason", required = false) String reason) {
        User admin = authService.getCurrentAuthenticatedUser();
        ClaimResponse response = claimService.reviewClaimByAdmin(id, approve, reason, admin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/audit")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(adminService.getAuditLogs());
    }
}
