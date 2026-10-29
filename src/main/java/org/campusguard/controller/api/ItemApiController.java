package org.campusguard.controller.api;

import jakarta.validation.Valid;
import org.campusguard.dto.*;
import org.campusguard.entity.User;
import org.campusguard.service.AuthService;
import org.campusguard.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ItemApiController {

    private final ItemService itemService;
    private final AuthService authService;

    public ItemApiController(ItemService itemService, AuthService authService) {
        this.itemService = itemService;
        this.authService = authService;
    }

    @PostMapping(value = "/lost-items", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<LostItemResponse> createLostItem(
            @Valid @RequestPart("data") LostItemRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        LostItemResponse response = itemService.reportLostItem(request, file, currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/found-items", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<FoundItemResponse> createFoundItem(
            @Valid @RequestPart("data") FoundItemRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        FoundItemResponse response = itemService.reportFoundItem(request, file, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lost-items/{id}")
    public ResponseEntity<LostItemResponse> getLostItem(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(itemService.getLostItemById(id, currentUser));
    }

    @GetMapping("/found-items/{id}")
    public ResponseEntity<FoundItemResponse> getFoundItem(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(itemService.getFoundItemById(id, currentUser));
    }

    @GetMapping("/items/search")
    public ResponseEntity<Page<?>> searchItems(@ModelAttribute ItemSearchFilter filter) {
        User currentUser = null;
        try {
            currentUser = authService.getCurrentAuthenticatedUser();
        } catch (Exception ignored) {}

        if ("FOUND".equalsIgnoreCase(filter.getItemType())) {
            return ResponseEntity.ok(itemService.searchFoundItems(filter, currentUser));
        } else {
            return ResponseEntity.ok(itemService.searchLostItems(filter, currentUser));
        }
    }
}
