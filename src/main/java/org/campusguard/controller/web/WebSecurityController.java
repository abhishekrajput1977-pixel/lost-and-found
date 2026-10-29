package org.campusguard.controller.web;

import org.campusguard.dto.HandoverResponse;
import org.campusguard.entity.User;
import org.campusguard.service.AuthService;
import org.campusguard.service.HandoverService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/security")
@PreAuthorize("hasAnyRole('SECURITY_OFFICER', 'ADMIN')")
public class WebSecurityController {

    private final HandoverService handoverService;
    private final AuthService authService;

    public WebSecurityController(HandoverService handoverService, AuthService authService) {
        this.handoverService = handoverService;
        this.authService = authService;
    }

    @GetMapping("/dashboard")
    public String securityDashboard(Model model) {
        model.addAttribute("handovers", handoverService.getAllHandovers());
        model.addAttribute("scheduledHandovers", handoverService.getScheduledHandovers());
        return "security/dashboard";
    }

    @GetMapping("/handover/{id}")
    public String handoverDetail(@PathVariable Long id, Model model) {
        model.addAttribute("handover", handoverService.getAllHandovers().stream().filter(h -> h.getId().equals(id)).findFirst().orElseThrow());
        return "security/handover-detail";
    }

    @PostMapping("/handover/{id}/complete")
    public String completeHandover(@PathVariable Long id, @RequestParam(value = "notes", required = false) String notes) {
        User officer = authService.getCurrentAuthenticatedUser();
        handoverService.completeHandover(id, notes, officer);
        return "redirect:/security/dashboard?success=handover_completed";
    }
}
