package org.campusguard.service;

import org.campusguard.dto.ItemMatchDto;
import org.campusguard.entity.ItemMatch;
import org.campusguard.entity.User;
import org.campusguard.exception.ResourceNotFoundException;
import org.campusguard.repository.ItemMatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {

    private final ItemMatchRepository itemMatchRepository;
    private final ItemService itemService;

    public MatchService(ItemMatchRepository itemMatchRepository, ItemService itemService) {
        this.itemMatchRepository = itemMatchRepository;
        this.itemService = itemService;
    }

    public List<ItemMatchDto> getUserMatches(User user) {
        List<ItemMatch> matches = itemMatchRepository.findAllByUser(user);
        return matches.stream().map(m -> mapToDto(m, user)).toList();
    }

    public ItemMatchDto getMatchById(Long id, User user) {
        ItemMatch match = itemMatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        return mapToDto(match, user);
    }

    public ItemMatchDto mapToDto(ItemMatch match, User currentUser) {
        ItemMatchDto dto = new ItemMatchDto();
        dto.setId(match.getId());

        boolean isLostOwner = currentUser != null && match.getLostItem().getReportedBy().getId().equals(currentUser.getId());
        boolean isFoundOwner = currentUser != null && match.getFoundItem().getReportedBy().getId().equals(currentUser.getId());

        dto.setLostItem(itemService.mapLostToResponse(match.getLostItem(), isLostOwner));
        dto.setFoundItem(itemService.mapFoundToResponse(match.getFoundItem(), isFoundOwner));
        dto.setMatchScore(match.getMatchScore());
        dto.setConfidenceLevel(match.getConfidenceLevel());
        dto.setTextSim(match.getTextSim());
        dto.setLocationSim(match.getLocationSim());
        dto.setDateSim(match.getDateSim());
        dto.setCategorySim(match.getCategorySim());
        dto.setBrandSim(match.getBrandSim());
        dto.setColorSim(match.getColorSim());
        dto.setStatus(match.getStatus());
        dto.setCreatedAt(match.getCreatedAt());

        return dto;
    }
}
