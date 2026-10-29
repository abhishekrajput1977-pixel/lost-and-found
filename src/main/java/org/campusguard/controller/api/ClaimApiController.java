package org.campusguard.controller.api;

import jakarta.validation.Valid;
import org.campusguard.dto.ClaimRequest;
import org.campusguard.dto.ClaimResponse;
import org.campusguard.dto.VerificationRequest;
import org.campusguard.dto.VerificationResponse;
import org.campusguard.entity.User;
import org.campusguard.service.AuthService;
import org.campusguard.service.ClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimApiController {

    private final ClaimService claimService;
    private final AuthService authService;

    public ClaimApiController(ClaimService claimService, AuthService authService) {
        this.claimService = claimService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<ClaimResponse> submitClaim(@Valid @RequestBody ClaimRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        ClaimResponse response = claimService.submitClaim(request, currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<VerificationResponse> verifyClaim(
            @PathVariable Long id,
            @Valid @RequestBody VerificationRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        request.setClaimId(id);
        VerificationResponse response = claimService.verifyClaimOwnership(request, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClaimResponse>> getUserClaims() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(claimService.getUserClaims(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponse> getClaimById(@PathVariable Long id) {
        return ResponseEntity.ok(claimService.getClaimById(id));
    }
}
