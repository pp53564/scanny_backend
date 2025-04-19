package project.scanny.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.scanny.dto.NeighborDTO;
import project.scanny.dto.StatsPerUserAndLanguageDTO;
import project.scanny.models.User;
import project.scanny.services.StatsService;
import project.scanny.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final StatsService statsService;
    private final UserService userService;

    public StatsController(StatsService statsService, UserService userService) {
        this.statsService = statsService;
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<List<StatsPerUserAndLanguageDTO>> getUserLanguagesStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ResponseEntity.ok(statsService.getStatsPerUserAndLanguages(user));
    }

    @GetMapping("/user/{selectedLangCode}")
    public ResponseEntity<List<NeighborDTO>> getNeighborsForLanguage(@PathVariable String selectedLangCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ResponseEntity.ok(statsService.getNeighborsForLanguage(user, selectedLangCode));
    }

}
