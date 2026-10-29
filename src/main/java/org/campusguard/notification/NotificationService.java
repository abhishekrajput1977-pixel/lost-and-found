package org.campusguard.notification;

import org.campusguard.entity.Notification;
import org.campusguard.entity.User;
import org.campusguard.entity.enums.NotificationType;
import org.campusguard.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Value("${campusguard.mail.dev-mode:true}")
    private boolean devMode;

    @Value("${campusguard.mail.from-email:no-reply@campusguard.edu}")
    private String fromEmail;

    public NotificationService(NotificationRepository notificationRepository, @Autowired(required = false) JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }

    public Notification createNotification(User user, String title, String message, NotificationType type, String targetUrl) {
        Notification notification = new Notification(user, title, message, type, targetUrl);
        return notificationRepository.save(notification);
    }

    @Async
    public void sendEmailNotification(String toEmail, String subject, String body) {
        if (devMode || mailSender == null) {
            log.info("================ EMAIL NOTIFICATION [DEV MODE LOG] ================");
            log.info("To: {}", toEmail);
            log.info("Subject: {}", subject);
            log.info("Body:\n{}", body);
            log.info("===================================================================");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email notification sent successfully to {}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send email to {}", toEmail, ex);
        }
    }

    public void notifyMatchFound(User user, String itemName, String locationName, String dateStr, double matchScore, Long matchId) {
        String title = "Potential Match Found for " + itemName;
        String message = String.format("A potential match (%.1f%% confidence) was found for your report at %s on %s.",
                matchScore, locationName, dateStr);

        createNotification(user, title, message, NotificationType.POTENTIAL_MATCH, "/items/matches/" + matchId);

        String emailSubject = "Potential Match Found – CampusGuard";
        String emailBody = String.format("""
                Dear %s,

                A potential match has been identified on CampusGuard for your lost item.

                Item: %s
                Location: %s
                Date: %s
                Match Confidence: %.1f%%

                Please log into CampusGuard to view the potential match and complete ownership verification:
                http://localhost:8080/items/matches/%d

                Lost on Campus. Found Faster. Returned Safely.
                CampusGuard Security Team
                """, user.getFullName(), itemName, locationName, dateStr, matchScore, matchId);

        sendEmailNotification(user.getEmail(), emailSubject, emailBody);
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    public void markAsRead(Long notificationId, User user) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUser().getId().equals(user.getId())) {
                n.setIsRead(true);
                notificationRepository.save(n);
            }
        });
    }
}
