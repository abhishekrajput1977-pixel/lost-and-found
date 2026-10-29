package org.campusguard.controller.api;

import org.campusguard.entity.Notification;
import org.campusguard.entity.User;
import org.campusguard.notification.NotificationService;
import org.campusguard.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationApiController {

    private final NotificationService notificationService;
    private final AuthService authService;

    public NotificationApiController(NotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(notificationService.getUserNotifications(currentUser));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Map<String, Long> res = new HashMap<>();
        res.put("count", notificationService.getUnreadCount(currentUser));
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        notificationService.markAsRead(id, currentUser);
        return ResponseEntity.ok().build();
    }
}
