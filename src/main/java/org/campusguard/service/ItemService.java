package org.campusguard.service;

import org.campusguard.audit.AuditService;
import org.campusguard.dto.*;
import org.campusguard.entity.*;
import org.campusguard.entity.enums.AuditAction;
import org.campusguard.entity.enums.ItemStatus;
import org.campusguard.exception.ForbiddenException;
import org.campusguard.exception.ResourceNotFoundException;
import org.campusguard.matching.SmartMatchingEngine;
import org.campusguard.notification.NotificationService;
import org.campusguard.repository.*;
import org.campusguard.util.FileStorageService;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ItemMatchRepository itemMatchRepository;
    private final FileStorageService fileStorageService;
    private final SmartMatchingEngine matchingEngine;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public ItemService(LostItemRepository lostItemRepository,
                       FoundItemRepository foundItemRepository,
                       CategoryRepository categoryRepository,
                       LocationRepository locationRepository,
                       ItemMatchRepository itemMatchRepository,
                       FileStorageService fileStorageService,
                       SmartMatchingEngine matchingEngine,
                       NotificationService notificationService,
                       AuditService auditService) {
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.itemMatchRepository = itemMatchRepository;
        this.fileStorageService = fileStorageService;
        this.matchingEngine = matchingEngine;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional
    public LostItemResponse reportLostItem(LostItemRequest request, MultipartFile file, User reportedBy) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        String photoUrl = request.getPhotoUrl();
        if (file != null && !file.isEmpty()) {
            photoUrl = fileStorageService.storeFile(file);
        }

        LostItem item = new LostItem();
        item.setItemName(request.getItemName());
        item.setCategory(category);
        item.setBrand(request.getBrand());
        item.setColor(request.getColor());
        item.setDescription(request.getDescription());
        item.setLocation(location);
        item.setLostDate(request.getLostDate());
        item.setLostTime(request.getLostTime());
        item.setPhotoUrl(photoUrl);
        item.setUniqueCharacteristics(request.getUniqueCharacteristics());
        item.setSerialNumber(request.getSerialNumber());
        item.setPrivateIdentifyingInfo(request.getPrivateIdentifyingInfo());
        item.setStatus(ItemStatus.LOST);
        item.setReportedBy(reportedBy);

        LostItem saved = lostItemRepository.save(item);

        auditService.logAction(reportedBy, AuditAction.REPORT_CREATED, "LostItem", saved.getId(), "Reported Lost Item: " + saved.getItemName(), null);

        // Run matching engine asynchronously against active found items
        triggerMatchingForLostItem(saved);

        return mapLostToResponse(saved, true);
    }

    @Transactional
    public FoundItemResponse reportFoundItem(FoundItemRequest request, MultipartFile file, User reportedBy) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        String photoUrl = request.getPhotoUrl();
        if (file != null && !file.isEmpty()) {
            photoUrl = fileStorageService.storeFile(file);
        }

        FoundItem item = new FoundItem();
        item.setItemName(request.getItemName());
        item.setCategory(category);
        item.setBrand(request.getBrand());
        item.setColor(request.getColor());
        item.setDescription(request.getDescription());
        item.setLocation(location);
        item.setFoundDate(request.getFoundDate());
        item.setFoundTime(request.getFoundTime());
        item.setPhotoUrl(photoUrl);
        item.setConditionStatus(request.getConditionStatus());
        item.setPrivateDetails(request.getPrivateDetails());
        item.setStatus(ItemStatus.FOUND);
        item.setReportedBy(reportedBy);

        FoundItem saved = foundItemRepository.save(item);

        auditService.logAction(reportedBy, AuditAction.REPORT_CREATED, "FoundItem", saved.getId(), "Reported Found Item: " + saved.getItemName(), null);

        // Run matching engine asynchronously against active lost items
        triggerMatchingForFoundItem(saved);

        return mapFoundToResponse(saved, true);
    }

    @Transactional
    public void triggerMatchingForLostItem(LostItem lostItem) {
        List<FoundItem> activeFound = foundItemRepository.findAllActiveFoundItems();
        for (FoundItem found : activeFound) {
            ItemMatch match = matchingEngine.computeMatch(lostItem, found);
            if (match.getMatchScore() >= 45.0) { // store candidate matches above threshold
                ItemMatch savedMatch = itemMatchRepository.save(match);
                lostItem.setStatus(ItemStatus.MATCHED);
                found.setStatus(ItemStatus.MATCHED);
                lostItemRepository.save(lostItem);
                foundItemRepository.save(found);

                if (match.getMatchScore() >= 65.0) {
                    // Send notification & email to lost item owner
                    notificationService.notifyMatchFound(
                            lostItem.getReportedBy(),
                            lostItem.getItemName(),
                            found.getLocation().getName(),
                            found.getFoundDate().toString(),
                            savedMatch.getMatchScore(),
                            savedMatch.getId()
                    );
                }
            }
        }
    }

    @Transactional
    public void triggerMatchingForFoundItem(FoundItem foundItem) {
        List<LostItem> activeLost = lostItemRepository.findAllActiveLostItems();
        for (LostItem lost : activeLost) {
            ItemMatch match = matchingEngine.computeMatch(lost, foundItem);
            if (match.getMatchScore() >= 45.0) {
                ItemMatch savedMatch = itemMatchRepository.save(match);
                lost.setStatus(ItemStatus.MATCHED);
                foundItem.setStatus(ItemStatus.MATCHED);
                lostItemRepository.save(lost);
                foundItemRepository.save(foundItem);

                if (match.getMatchScore() >= 65.0) {
                    notificationService.notifyMatchFound(
                            lost.getReportedBy(),
                            lost.getItemName(),
                            foundItem.getLocation().getName(),
                            foundItem.getFoundDate().toString(),
                            savedMatch.getMatchScore(),
                            savedMatch.getId()
                    );
                }
            }
        }
    }

    public Page<LostItemResponse> searchLostItems(ItemSearchFilter filter, User currentUser) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by("createdAt").descending());
        Specification<LostItem> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
                String pattern = "%" + filter.getQuery().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("itemName")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("brand")), pattern)
                ));
            }
            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
            }
            if (filter.getLocationId() != null) {
                predicates.add(cb.equal(root.get("location").get("id"), filter.getLocationId()));
            }
            if (filter.getBrand() != null && !filter.getBrand().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), filter.getBrand().toLowerCase()));
            }
            if (filter.getColor() != null && !filter.getColor().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("color")), filter.getColor().toLowerCase()));
            }
            if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
                predicates.add(cb.equal(root.get("status"), ItemStatus.valueOf(filter.getStatus())));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<LostItem> page = lostItemRepository.findAll(spec, pageable);
        return page.map(item -> mapLostToResponse(item, currentUser != null && item.getReportedBy().getId().equals(currentUser.getId())));
    }

    public Page<FoundItemResponse> searchFoundItems(ItemSearchFilter filter, User currentUser) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by("createdAt").descending());
        Specification<FoundItem> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
                String pattern = "%" + filter.getQuery().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("itemName")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("brand")), pattern)
                ));
            }
            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
            }
            if (filter.getLocationId() != null) {
                predicates.add(cb.equal(root.get("location").get("id"), filter.getLocationId()));
            }
            if (filter.getBrand() != null && !filter.getBrand().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), filter.getBrand().toLowerCase()));
            }
            if (filter.getColor() != null && !filter.getColor().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("color")), filter.getColor().toLowerCase()));
            }
            if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
                predicates.add(cb.equal(root.get("status"), ItemStatus.valueOf(filter.getStatus())));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<FoundItem> page = foundItemRepository.findAll(spec, pageable);
        return page.map(item -> mapFoundToResponse(item, currentUser != null && item.getReportedBy().getId().equals(currentUser.getId())));
    }

    public LostItemResponse getLostItemById(Long id, User currentUser) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lost item not found"));
        boolean isOwner = currentUser != null && (item.getReportedBy().getId().equals(currentUser.getId()) || currentUser.getRole().name().equals("ADMIN"));
        return mapLostToResponse(item, isOwner);
    }

    public FoundItemResponse getFoundItemById(Long id, User currentUser) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found"));
        boolean isOwner = currentUser != null && (item.getReportedBy().getId().equals(currentUser.getId()) || currentUser.getRole().name().equals("ADMIN"));
        return mapFoundToResponse(item, isOwner);
    }

    public List<LostItemResponse> getUserLostItems(User user) {
        return lostItemRepository.findByReportedByOrderByCreatedAtDesc(user)
                .stream().map(item -> mapLostToResponse(item, true)).toList();
    }

    public List<FoundItemResponse> getUserFoundItems(User user) {
        return foundItemRepository.findByReportedByOrderByCreatedAtDesc(user)
                .stream().map(item -> mapFoundToResponse(item, true)).toList();
    }

    public LostItemResponse mapLostToResponse(LostItem item, boolean includePrivate) {
        LostItemResponse res = new LostItemResponse();
        res.setId(item.getId());
        res.setItemName(item.getItemName());
        res.setCategoryId(item.getCategory().getId());
        res.setCategoryName(item.getCategory().getName());
        res.setBrand(item.getBrand());
        res.setColor(item.getColor());
        res.setDescription(item.getDescription());
        res.setLocationId(item.getLocation().getId());
        res.setLocationName(item.getLocation().getName());
        res.setLostDate(item.getLostDate());
        res.setLostTime(item.getLostTime());
        res.setPhotoUrl(item.getPhotoUrl());
        res.setUniqueCharacteristics(item.getUniqueCharacteristics());
        res.setSerialNumber(item.getSerialNumber());
        res.setStatus(item.getStatus());
        res.setReportedById(item.getReportedBy().getId());
        res.setReportedByName(item.getReportedBy().getFullName());
        res.setCreatedAt(item.getCreatedAt());
        if (includePrivate) {
            res.setPrivateIdentifyingInfo(item.getPrivateIdentifyingInfo());
        }
        return res;
    }

    public FoundItemResponse mapFoundToResponse(FoundItem item, boolean includePrivate) {
        FoundItemResponse res = new FoundItemResponse();
        res.setId(item.getId());
        res.setItemName(item.getItemName());
        res.setCategoryId(item.getCategory().getId());
        res.setCategoryName(item.getCategory().getName());
        res.setBrand(item.getBrand());
        res.setColor(item.getColor());
        res.setDescription(item.getDescription());
        res.setLocationId(item.getLocation().getId());
        res.setLocationName(item.getLocation().getName());
        res.setFoundDate(item.getFoundDate());
        res.setFoundTime(item.getFoundTime());
        res.setPhotoUrl(item.getPhotoUrl());
        res.setConditionStatus(item.getConditionStatus());
        res.setStatus(item.getStatus());
        res.setReportedById(item.getReportedBy().getId());
        res.setReportedByName(item.getReportedBy().getFullName());
        res.setCreatedAt(item.getCreatedAt());
        if (includePrivate) {
            res.setPrivateDetails(item.getPrivateDetails());
        }
        return res;
    }
}
