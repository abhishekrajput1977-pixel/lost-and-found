package org.campusguard.controller.web;

import org.campusguard.entity.User;
import org.campusguard.entity.enums.FraudFlagStatus;
import org.campusguard.service.AdminService;
import org.campusguard.service.AuthService;
import org.campusguard.service.ClaimService;
import org.campusguard.service.ItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class WebAdminController {

    private final AdminService adminService;
    private final ClaimService claimService;
    private final ItemService itemService;
    private final AuthService authService;

    public WebAdminController(AdminService adminService,
                              ClaimService claimService,
                              ItemService itemService,
                              AuthService authService) {
        this.adminService = adminService;
        this.claimService = claimService;
        this.itemService = itemService;
        this.authService = authService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("stats", adminService.getDashboardStats());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String adminUsers(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUserStatus(@PathVariable Long id) {
        User admin = authService.getCurrentAuthenticatedUser();
        adminService.toggleUserStatus(id, admin);
        return "redirect:/admin/users?success=status_toggled";
    }

    @GetMapping("/claims")
    public String adminClaims(Model model) {
        model.addAttribute("claims", claimService.getAllClaims());
        return "admin/claims";
    }

    @PostMapping("/claims/{id}/review")
    public String reviewClaim(
            @PathVariable Long id,
            @RequestParam("approve") boolean approve,
            @RequestParam(value = "reason", required = false) String reason) {
        User admin = authService.getCurrentAuthenticatedUser();
        claimService.reviewClaimByAdmin(id, approve, reason, admin);
        return "redirect:/admin/claims?success=claim_reviewed";
    }

    @GetMapping("/fraud")
    public String adminFraud(Model model) {
        model.addAttribute("fraudFlags", adminService.getFraudFlags());
        return "admin/fraud";
    }

    @PostMapping("/fraud/{id}/resolve")
    public String resolveFraud(@PathVariable Long id, @RequestParam("status") FraudFlagStatus status) {
        User admin = authService.getCurrentAuthenticatedUser();
        adminService.resolveFraudFlag(id, status, admin);
        return "redirect:/admin/fraud?success=flag_resolved";
    }

    @GetMapping("/audit")
    public String adminAudit(Model model) {
        model.addAttribute("auditLogs", adminService.getAuditLogs());
        return "admin/audit";
    }
}
