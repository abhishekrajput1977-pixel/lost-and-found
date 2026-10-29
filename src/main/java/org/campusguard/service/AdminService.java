package org.campusguard.service;

import org.campusguard.audit.AuditService;
import org.campusguard.dto.DashboardStatsDto;
import org.campusguard.dto.FraudFlagDto;
import org.campusguard.dto.StudentProfileDto;
import org.campusguard.dto.UserDto;
import org.campusguard.entity.*;
import org.campusguard.entity.enums.*;
import org.campusguard.exception.ResourceNotFoundException;
import org.campusguard.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final ItemMatchRepository itemMatchRepository;
    private final ClaimRepository claimRepository;
    private final HandoverRecordRepository handoverRecordRepository;
    private final FraudFlagRepository fraudFlagRepository;
    private final DepartmentRepository departmentRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final AuditLogRepository auditLogRepository;
    private final AuthService authService;
    private final AuditService auditService;

    public AdminService(UserRepository userRepository,
                        LostItemRepository lostItemRepository,
                        FoundItemRepository foundItemRepository,
                        ItemMatchRepository itemMatchRepository,
                        ClaimRepository claimRepository,
                        HandoverRecordRepository handoverRecordRepository,
                        FraudFlagRepository fraudFlagRepository,
                        DepartmentRepository departmentRepository,
                        LocationRepository locationRepository,
                        CategoryRepository categoryRepository,
                        AuditLogRepository auditLogRepository,
                        AuthService authService,
                        AuditService auditService) {
        this.userRepository = userRepository;
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.itemMatchRepository = itemMatchRepository;
        this.claimRepository = claimRepository;
        this.handoverRecordRepository = handoverRecordRepository;
        this.fraudFlagRepository = fraudFlagRepository;
        this.departmentRepository = departmentRepository;
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
        this.auditLogRepository = auditLogRepository;
        this.authService = authService;
        this.auditService = auditService;
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        long totalLost = lostItemRepository.count();
        long totalFound = foundItemRepository.count();
        long potentialMatches = itemMatchRepository.countByStatus(MatchStatus.PENDING) + itemMatchRepository.countByStatus(MatchStatus.NOTIFIED);
        long pendingClaims = claimRepository.countByStatus(ClaimStatus.SUBMITTED) + claimRepository.countByStatus(ClaimStatus.UNDER_VERIFICATION);
        long returnedItems = lostItemRepository.countByStatus(ItemStatus.RETURNED);
        long pendingVerification = claimRepository.countByStatus(ClaimStatus.MANUAL_REVIEW);
        long fraudFlagsCount = fraudFlagRepository.countByStatus(FraudFlagStatus.PENDING_REVIEW);
        long totalUsers = userRepository.count();

        double resolutionRate = totalLost > 0 ? (double) returnedItems / totalLost * 100.0 : 0.0;
        resolutionRate = Math.round(resolutionRate * 10.0) / 10.0;

        stats.setTotalLostItems(totalLost);
        stats.setTotalFoundItems(totalFound);
        stats.setPotentialMatches(potentialMatches);
        stats.setPendingClaims(pendingClaims);
        stats.setReturnedItems(returnedItems);
        stats.setPendingVerification(pendingVerification);
        stats.setFraudFlagsCount(fraudFlagsCount);
        stats.setTotalUsers(totalUsers);
        stats.setResolutionRate(resolutionRate);

        // Chart 1: Items by Category
        Map<String, Long> categoryMap = new LinkedHashMap<>();
        for (Category cat : categoryRepository.findAll()) {
            long count = lostItemRepository.countByCategoryId(cat.getId()) + foundItemRepository.countByCategoryId(cat.getId());
            if (count > 0) {
                categoryMap.put(cat.getName(), count);
            }
        }
        stats.setItemsByCategory(categoryMap);

        // Chart 2: Items by Location
        Map<String, Long> locationMap = new LinkedHashMap<>();
        for (Location loc : locationRepository.findAll()) {
            long lostCount = lostItemRepository.findAll().stream().filter(l -> l.getLocation().getId().equals(loc.getId())).count();
            long foundCount = foundItemRepository.findAll().stream().filter(f -> f.getLocation().getId().equals(loc.getId())).count();
            if (lostCount + foundCount > 0) {
                locationMap.put(loc.getName(), lostCount + foundCount);
            }
        }
        stats.setItemsByLocation(locationMap);

        // Chart 3: Monthly distribution
        Map<String, Long> monthlyMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        lostItemRepository.findAll().forEach(l -> {
            String monthKey = l.getCreatedAt().format(formatter);
            monthlyMap.put(monthKey, monthlyMap.getOrDefault(monthKey, 0L) + 1);
        });
        stats.setMonthlyReports(monthlyMap);

        return stats;
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> dtos = new ArrayList<>();
        for (User u : users) {
            StudentProfileDto profileDto = u.getStudentProfile() != null ? authService.mapProfileToDto(u.getStudentProfile()) : null;
            dtos.add(authService.mapUserToDto(u, profileDto));
        }
        return dtos;
    }

    @Transactional
    public void toggleUserStatus(Long userId, User admin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);

        auditService.logAction(admin, AuditAction.ADMIN_ACTION, "User", user.getId(),
                "Toggled User Account Status to " + user.getEnabled(), null);
    }

    public List<FraudFlagDto> getFraudFlags() {
        return fraudFlagRepository.findAll().stream().map(this::mapFraudToDto).toList();
    }

    @Transactional
    public void resolveFraudFlag(Long flagId, FraudFlagStatus newStatus, User admin) {
        FraudFlag flag = fraudFlagRepository.findById(flagId)
                .orElseThrow(() -> new ResourceNotFoundException("Fraud flag not found"));
        flag.setStatus(newStatus);
        flag.setReviewedBy(admin);
        fraudFlagRepository.save(flag);

        auditService.logAction(admin, AuditAction.ADMIN_ACTION, "FraudFlag", flag.getId(),
                "Resolved Fraud Flag #" + flag.getId() + " to " + newStatus, null);
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    public FraudFlagDto mapFraudToDto(FraudFlag flag) {
        FraudFlagDto dto = new FraudFlagDto();
        dto.setId(flag.getId());
        dto.setUserId(flag.getUser().getId());
        dto.setUserName(flag.getUser().getFullName());
        dto.setUserEmail(flag.getUser().getEmail());
        dto.setItemType(flag.getItemType());
        dto.setItemId(flag.getItemId());
        dto.setReason(flag.getReason());
        dto.setSeverity(flag.getSeverity());
        dto.setStatus(flag.getStatus());
        dto.setCreatedAt(flag.getCreatedAt());
        if (flag.getReviewedBy() != null) {
            dto.setReviewedByName(flag.getReviewedBy().getFullName());
        }
        return dto;
    }
}
