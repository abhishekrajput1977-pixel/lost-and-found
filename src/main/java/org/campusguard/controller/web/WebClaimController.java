package org.campusguard.controller.web;

import org.campusguard.dto.ClaimRequest;
import org.campusguard.dto.ClaimResponse;
import org.campusguard.dto.VerificationRequest;
import org.campusguard.dto.VerificationResponse;
import org.campusguard.entity.User;
import org.campusguard.service.AuthService;
import org.campusguard.service.ClaimService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/claims")
public class WebClaimController {

    private final ClaimService claimService;
    private final AuthService authService;

    public WebClaimController(ClaimService claimService, AuthService authService) {
        this.claimService = claimService;
        this.authService = authService;
    }

    @GetMapping
    public String viewClaims(Model model) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        model.addAttribute("claims", claimService.getUserClaims(currentUser));
        return "student/claims";
    }

    @GetMapping("/{id}")
    public String viewClaimDetail(@PathVariable Long id, Model model) {
        ClaimResponse claim = claimService.getClaimById(id);
        model.addAttribute("claim", claim);
        return "student/claim-detail";
    }

    @PostMapping
    public String submitClaim(@ModelAttribute ClaimRequest request, Model model) {
        try {
            User currentUser = authService.getCurrentAuthenticatedUser();
            ClaimResponse res = claimService.submitClaim(request, currentUser);
            return "redirect:/claims/" + res.getId() + "?success=claim_submitted";
        } catch (Exception ex) {
            return "redirect:/items/matches/" + request.getMatchId() + "?error=" + ex.getMessage();
        }
    }

    @PostMapping("/{id}/verify")
    public String verifyOwnership(@PathVariable Long id, @RequestParam("answer") String answer, Model model) {
        try {
            User currentUser = authService.getCurrentAuthenticatedUser();
            VerificationResponse res = claimService.verifyClaimOwnership(new VerificationRequest(id, answer), currentUser);
            return "redirect:/claims/" + id + "?verified=" + res.getConfidenceCategory();
        } catch (Exception ex) {
            return "redirect:/claims/" + id + "?error=" + ex.getMessage();
        }
    }
}
