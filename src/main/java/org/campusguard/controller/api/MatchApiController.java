package org.campusguard.controller.api;

import org.campusguard.dto.ItemMatchDto;
import org.campusguard.entity.User;
import org.campusguard.service.AuthService;
import org.campusguard.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchApiController {

    private final MatchService matchService;
    private final AuthService authService;

    public MatchApiController(MatchService matchService, AuthService authService) {
        this.matchService = matchService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<ItemMatchDto>> getUserMatches() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(matchService.getUserMatches(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemMatchDto> getMatchById(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(matchService.getMatchById(id, currentUser));
    }
}
