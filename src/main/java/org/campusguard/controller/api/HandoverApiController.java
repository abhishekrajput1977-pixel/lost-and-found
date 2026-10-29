package org.campusguard.controller.api;

import org.campusguard.dto.HandoverResponse;
import org.campusguard.entity.User;
import org.campusguard.service.AuthService;
import org.campusguard.service.HandoverService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/handover")
public class HandoverApiController {

    private final HandoverService handoverService;
    private final AuthService authService;

    public HandoverApiController(HandoverService handoverService, AuthService authService) {
        this.handoverService = handoverService;
        this.authService = authService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SECURITY_OFFICER', 'ADMIN')")
    public ResponseEntity<List<HandoverResponse>> getScheduledHandovers() {
        return ResponseEntity.ok(handoverService.getScheduledHandovers());
    }

    @GetMapping("/reference/{code}")
    public ResponseEntity<HandoverResponse> getByReference(@PathVariable String code) {
        return ResponseEntity.ok(handoverService.getByReferenceCode(code));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('SECURITY_OFFICER', 'ADMIN')")
    public ResponseEntity<HandoverResponse> completeHandover(
            @PathVariable Long id,
            @RequestParam(value = "notes", required = false) String notes) {
        User securityOfficer = authService.getCurrentAuthenticatedUser();
        HandoverResponse response = handoverService.completeHandover(id, notes, securityOfficer);
        return ResponseEntity.ok(response);
    }
}
