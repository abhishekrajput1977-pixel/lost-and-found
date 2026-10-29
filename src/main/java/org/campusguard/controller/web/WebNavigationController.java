package org.campusguard.controller.web;

import org.campusguard.entity.User;
import org.campusguard.notification.NotificationService;
import org.campusguard.repository.CategoryRepository;
import org.campusguard.repository.DepartmentRepository;
import org.campusguard.repository.LocationRepository;
import org.campusguard.service.AdminService;
import org.campusguard.service.AuthService;
import org.campusguard.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebNavigationController {

    private final AuthService authService;
    private final ItemService itemService;
    private final AdminService adminService;
    private final NotificationService notificationService;
    private final DepartmentRepository departmentRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;

    public WebNavigationController(AuthService authService,
                                   ItemService itemService,
                                   AdminService adminService,
                                   NotificationService notificationService,
                                   DepartmentRepository departmentRepository,
                                   LocationRepository locationRepository,
                                   CategoryRepository categoryRepository) {
        this.authService = authService;
        this.itemService = itemService;
        this.adminService = adminService;
        this.notificationService = notificationService;
        this.departmentRepository = departmentRepository;
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping({"/", "/landing"})
    public String landingPage(Model model) {
        model.addAttribute("stats", adminService.getDashboardStats());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        return "landing";
    }

    @GetMapping("/login")
    public String loginPage() {
        try {
            User current = authService.getCurrentAuthenticatedUser();
            if (current != null) return "redirect:/dashboard";
        } catch (Exception ignored) {}
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        return "auth/register";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = authService.getCurrentAuthenticatedUser();
        model.addAttribute("user", user);
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(user));

        switch (user.getRole()) {
            case ADMIN:
                return "redirect:/admin/dashboard";
            case SECURITY_OFFICER:
                return "redirect:/security/dashboard";
            case STAFF:
            case STUDENT:
            default:
                model.addAttribute("myLostItems", itemService.getUserLostItems(user));
                model.addAttribute("myFoundItems", itemService.getUserFoundItems(user));
                model.addAttribute("notifications", notificationService.getUserNotifications(user));
                return "student/dashboard";
        }
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    @GetMapping("/faq")
    public String faqPage() {
        return "faq";
    }
}
