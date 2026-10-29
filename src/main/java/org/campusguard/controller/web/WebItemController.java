package org.campusguard.controller.web;

import org.campusguard.dto.*;
import org.campusguard.entity.User;
import org.campusguard.notification.NotificationService;
import org.campusguard.repository.CategoryRepository;
import org.campusguard.repository.LocationRepository;
import org.campusguard.service.AuthService;
import org.campusguard.service.ItemService;
import org.campusguard.service.MatchService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/items")
public class WebItemController {

    private final ItemService itemService;
    private final MatchService matchService;
    private final AuthService authService;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final NotificationService notificationService;

    public WebItemController(ItemService itemService,
                             MatchService matchService,
                             AuthService authService,
                             CategoryRepository categoryRepository,
                             LocationRepository locationRepository,
                             NotificationService notificationService) {
        this.itemService = itemService;
        this.matchService = matchService;
        this.authService = authService;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.notificationService = notificationService;
    }

    @GetMapping("/report-lost")
    public String showReportLostForm(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("lostItemRequest", new LostItemRequest());
        return "items/report-lost";
    }

    @PostMapping("/report-lost")
    public String handleReportLost(
            @ModelAttribute LostItemRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Model model) {
        try {
            User user = authService.getCurrentAuthenticatedUser();
            itemService.reportLostItem(request, file, user);
            return "redirect:/dashboard?success=lost_reported";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("locations", locationRepository.findAll());
            return "items/report-lost";
        }
    }

    @GetMapping("/report-found")
    public String showReportFoundForm(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("foundItemRequest", new FoundItemRequest());
        return "items/report-found";
    }

    @PostMapping("/report-found")
    public String handleReportFound(
            @ModelAttribute FoundItemRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Model model) {
        try {
            User user = authService.getCurrentAuthenticatedUser();
            itemService.reportFoundItem(request, file, user);
            return "redirect:/dashboard?success=found_reported";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("locations", locationRepository.findAll());
            return "items/report-found";
        }
    }

    @GetMapping("/search")
    public String searchPage(@ModelAttribute ItemSearchFilter filter, Model model) {
        User currentUser = null;
        try {
            currentUser = authService.getCurrentAuthenticatedUser();
        } catch (Exception ignored) {}

        if ("FOUND".equalsIgnoreCase(filter.getItemType())) {
            Page<FoundItemResponse> foundPage = itemService.searchFoundItems(filter, currentUser);
            model.addAttribute("itemsPage", foundPage);
        } else {
            Page<LostItemResponse> lostPage = itemService.searchLostItems(filter, currentUser);
            model.addAttribute("itemsPage", lostPage);
        }

        model.addAttribute("filter", filter);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        return "items/search";
    }

    @GetMapping("/lost/{id}")
    public String viewLostItem(@PathVariable Long id, Model model) {
        User currentUser = null;
        try {
            currentUser = authService.getCurrentAuthenticatedUser();
        } catch (Exception ignored) {}
        LostItemResponse item = itemService.getLostItemById(id, currentUser);
        model.addAttribute("item", item);
        return "items/view-lost";
    }

    @GetMapping("/found/{id}")
    public String viewFoundItem(@PathVariable Long id, Model model) {
        User currentUser = null;
        try {
            currentUser = authService.getCurrentAuthenticatedUser();
        } catch (Exception ignored) {}
        FoundItemResponse item = itemService.getFoundItemById(id, currentUser);
        model.addAttribute("item", item);
        return "items/view-found";
    }

    @GetMapping("/matches")
    public String viewMatches(Model model) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        model.addAttribute("matches", matchService.getUserMatches(currentUser));
        return "items/matches";
    }

    @GetMapping("/matches/{id}")
    public String viewMatchDetail(@PathVariable Long id, Model model) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        ItemMatchDto match = matchService.getMatchById(id, currentUser);
        model.addAttribute("match", match);
        return "items/match-detail";
    }
}
