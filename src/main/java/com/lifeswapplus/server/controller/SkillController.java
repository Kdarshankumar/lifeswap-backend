package com.lifeswapplus.server.controller;

import com.lifeswapplus.server.model.MatchResult;
import com.lifeswapplus.server.model.Skill;
import com.lifeswapplus.server.model.User;
import com.lifeswapplus.server.repository.UserRepository;
import com.lifeswapplus.server.service.SkillService;
import com.lifeswapplus.server.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "*")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ============================================================
    // ADD SKILL (public)
    // ============================================================
    @PostMapping("/{userId}")
    public Skill addSkill(@PathVariable Long userId, @RequestBody Skill skill) {
        return skillService.addSkill(userId, skill);
    }

    // ============================================================
    // GET SKILLS OF A USER (public)
    // ============================================================
    @GetMapping("/{userId}")
    public List<Skill> getSkills(@PathVariable Long userId) {
        return skillService.getSkillsByUser(userId);
    }

    // ============================================================
    // GET MATCHES (SECURED - user must be accessing their own matches)
    // ============================================================
    @GetMapping("/match/{userId}")
    public ResponseEntity<?> findMatches(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token
    ) {

        try {
            // remove "Bearer "
            String jwt = token.replace("Bearer ", "");

            // extract user email from token
            String email = jwtUtil.extractUsername(jwt);

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Invalid token"));
            }

            User loggedInUser = userOpt.get();

            // ensure the user is accessing ONLY their own matches
            if (!loggedInUser.getId().equals(userId)) {
                return ResponseEntity.status(403)
                        .body(Map.of("message", "Access denied"));
            }

            // return match results
            List<MatchResult> matches = skillService.findMatches(userId);
            return ResponseEntity.ok(matches);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // UPDATE SKILL (SECURED)
    // ============================================================
    @PutMapping("/{skillId}")
    public ResponseEntity<?> updateSkill(
            @PathVariable Long skillId,
            @RequestBody Skill updatedSkill,
            @RequestHeader("Authorization") String token
    ) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractUsername(jwt);

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
            }

            User loggedInUser = userOpt.get();

            Optional<Skill> skillOpt = skillService.getSkillById(skillId);
            if (skillOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Skill not found"));
            }

            Skill skill = skillOpt.get();

            // Ownership check — only owner can update
            if (!skill.getUser().getId().equals(loggedInUser.getId())) {
                return ResponseEntity.status(403)
                        .body(Map.of("message", "You cannot edit this skill"));
            }

            // Perform update
            skill.setSkillName(updatedSkill.getSkillName());
            skill.setCategory(updatedSkill.getCategory());
            skill.setLevel(updatedSkill.getLevel());
            skill.setDescription(updatedSkill.getDescription());
            skill.setType(updatedSkill.getType());

            Skill savedSkill = skillService.saveSkill(skill);
            return ResponseEntity.ok(savedSkill);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // DELETE SKILL (SECURED)
    // ============================================================
    @DeleteMapping("/{skillId}")
    public ResponseEntity<?> deleteSkill(
            @PathVariable Long skillId,
            @RequestHeader("Authorization") String token
    ) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractUsername(jwt);

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
            }

            User loggedInUser = userOpt.get();

            Optional<Skill> skillOpt = skillService.getSkillById(skillId);
            if (skillOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Skill not found"));
            }

            Skill skill = skillOpt.get();

            // Ownership check
            if (!skill.getUser().getId().equals(loggedInUser.getId())) {
                return ResponseEntity.status(403)
                        .body(Map.of("message", "You cannot delete this skill"));
            }

            skillService.deleteSkill(skillId);
            return ResponseEntity.ok(Map.of("message", "Skill deleted"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
